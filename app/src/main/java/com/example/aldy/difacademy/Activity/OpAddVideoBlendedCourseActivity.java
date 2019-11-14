package com.example.aldy.difacademy.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldy.difacademy.Model.BlendedVideoModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import static com.example.aldy.difacademy.Activity.OpMainActivity.ADD_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.DELETE_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpAddVideoBlendedCourseActivity extends AppCompatActivity {
    private static final String TAG = "OpAddVideoBlendedCourse";

    TextView tvNavbar;
    ConstraintLayout clBack;
    ImageView imgBack;

    TextView tvUploadProses;
    EditText edtTitle, edtDescription, edtFile;
    ConstraintLayout clUploadVideo, clCancelUpload;
    ProgressBar pbUpload;
    Button btnHapus, btnSimpan;

    Uri videoUri;
    String docBlendedCourseId;
    UploadTask uploadTask;
    String videoUrl = "";
    boolean isUploading = false;
    boolean thereIsData = false;
    int index;
    long dateCreated = 0;
    BlendedVideoModel blendedVideoModelIntent;

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    int PICK_VIDEO_REQUEST_CODE = 1;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;
    CollectionReference colVideoMateriRef;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_video_blended_course);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");

        initView();
        onClick();
        loadData();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Add Video Materi");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);

        tvUploadProses = findViewById(R.id.tv_op_add_video_blended_upload_process);
        edtTitle = findViewById(R.id.edt_op_add_video_blended_judul);
        edtDescription = findViewById(R.id.edt_op_add_video_blended_deskripsi);
        edtFile = findViewById(R.id.edt_op_add_video_blended_file_video);
        clUploadVideo = findViewById(R.id.cl_op_blended_upload);
        clCancelUpload = findViewById(R.id.cl_op_blended_cancel_upload);
        pbUpload = findViewById(R.id.pb_op_add_video_blended_upload);
        btnHapus = findViewById(R.id.btn_op_add_video_blended_hapus);
        btnSimpan = findViewById(R.id.btn_op_add_video_blended_simpan);
    }

    private void onClick() {
        edtFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVideoFromGallery();
            }
        });
        clUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isUploading) {
                    uploadVideo();
                } else {
                    showCancelUploadDialog();
                }
            }
        });
        clCancelUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelUploadDialog();
            }
        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHapusDialog();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKonfirmasiDialog();
            }
        });
    }

    private void loadData() {
        Intent intent = getIntent();
        docBlendedCourseId = intent.getStringExtra("document_id");
        docRef = db.collection("BlendedCourse").document(docBlendedCourseId);
        colVideoMateriRef = docRef.collection("VideoMateri");

        //ambil video yang udah ada sebelumnya
        blendedVideoModelIntent = intent.getParcelableExtra("blended_video_model");
        if (blendedVideoModelIntent != null){
            btnHapus.setVisibility(View.VISIBLE);
            int index = intent.getIntExtra("index", -1);
            if (index != -1) {
                this.index = index;
            }

            thereIsData = true;
            edtTitle.setText(blendedVideoModelIntent.getTitle());
            edtDescription.setText(blendedVideoModelIntent.getDescription());
            videoUrl = blendedVideoModelIntent.getVideoUrl();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_VIDEO_REQUEST_CODE) {
                videoUri = data.getData();
                edtFile.setText(videoUri.getPath());
            }
        }
    }

    private void getVideoFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE);
    }

    private void uploadVideo() {
        if (isUploading) {
            showCancelUploadDialog();
        } else if (videoUri == null) {
            getVideoFromGallery();
        } else {
            isUploading = true;
            pbUpload.setVisibility(View.VISIBLE);
            clCancelUpload.setVisibility(View.VISIBLE);
            final StorageReference storageReference = firebaseStorage.getReference()
                    .child("VideoMateriBlended/" + UUID.randomUUID().toString());
            uploadTask = storageReference.putFile(videoUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    videoUrl = uri.toString();
                                    clCancelUpload.setVisibility(View.GONE);
                                    pbUpload.setVisibility(View.GONE);
                                    tvUploadProses.setText("Upload Complete");
                                    isUploading = false;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    isUploading = false;
                                    Toast.makeText(OpAddVideoBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    isUploading = false;
                    Toast.makeText(OpAddVideoBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                            .getTotalByteCount());
                    pbUpload.setProgress((int) progress);
                    tvUploadProses.setText("Uploading " + (int) progress + "%");
                }
            });
        }
    }

    private void hapus() {
        pd.show();
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(blendedVideoModelIntent.getVideoUrl());
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = colVideoMateriRef.document(blendedVideoModelIntent.getDocumentId());
                        docRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Intent intent = new Intent(OpAddVideoBlendedCourseActivity.this, OpVideoBlendedCourseActivity.class);
                                        intent.putExtra("index", index);
                                        intent.putExtra("document_id", docBlendedCourseId);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivityForResult(intent, DELETE_REQUEST_CODE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(OpAddVideoBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddVideoBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addOrUpdate() {
        pd.show();
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        String videoUrl = this.videoUrl;
        if (thereIsData){ //edit
            dateCreated = blendedVideoModelIntent.getDateCreated();
            final BlendedVideoModel blendedVideoModel = new BlendedVideoModel(title, description, videoUrl, dateCreated);
            DocumentReference docRef = colVideoMateriRef.document(blendedVideoModelIntent.getDocumentId());
            docRef.set(blendedVideoModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Intent intent = new Intent(OpAddVideoBlendedCourseActivity.this, OpVideoBlendedCourseActivity.class);
                            intent.putExtra("blended_video_model", blendedVideoModel);
                            intent.putExtra("index", index);
                            intent.putExtra("document_id", docBlendedCourseId);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, UPDATE_REQUEST_CODE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(OpAddVideoBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else { //tambah
            try {
                dateCreated = Timestamp.now().getSeconds();
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                return;
            }

            final BlendedVideoModel blendedVideoModel = new BlendedVideoModel(title, description, videoUrl, dateCreated);
            colVideoMateriRef.add(blendedVideoModel)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            pd.dismiss();
                            Intent intent = new Intent(OpAddVideoBlendedCourseActivity.this, OpVideoBlendedCourseActivity.class);
                            intent.putExtra("blended_video_model", blendedVideoModel);
                            intent.putExtra("document_id", docBlendedCourseId);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, ADD_REQUEST_CODE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(OpAddVideoBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showCancelUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin membatalkan upload?");
        builder.setTitle("Batalkan Upload");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadTask.cancel();
                tvUploadProses.setText("");
                pbUpload.setProgress(0);
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showKonfirmasiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menyimpan video ini?");
        builder.setTitle("Tambah Video Kelas Blended");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isUploading){
                    Toast.makeText(OpAddVideoBlendedCourseActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
                } else {
                    addOrUpdate();
                }
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showHapusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghapus video ini?");
        builder.setTitle("Menghapus Video Kelas Blended");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hapus();
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
