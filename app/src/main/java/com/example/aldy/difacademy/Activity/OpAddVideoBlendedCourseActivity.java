package com.example.aldy.difacademy.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

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
    String documentId;
    UploadTask uploadTask;
    String videoUrl = "";
    boolean isUploading = false;

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    int PICK_VIDEO_REQUEST_CODE = 1;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_video_blended_course);

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
        documentId = intent.getStringExtra("document_id");
        docRef = db.collection("BlendedCourse").document(documentId);

        //ambil video yang udah ada sebelumnya
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

    }

    private void addOrUpdate() {
        CollectionReference collRef = docRef.collection("VideoMateri");
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        String videoUrl = this.videoUrl;
        long dateCreated = 0;

        try {
            dateCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        BlendedVideoModel blendedVideoModel = new BlendedVideoModel(title, description, videoUrl, dateCreated);
        collRef.add(blendedVideoModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpAddVideoBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
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
                addOrUpdate();
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
