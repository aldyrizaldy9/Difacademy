package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.Model.BlendedVideoModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnCompleteListener;
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

import static com.example.aldy.difacademy.Activity.OpAddBlendedCourseActivity.blendedCourseDocId;
import static com.example.aldy.difacademy.Activity.OpMainActivity.ADD_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.DELETE_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpAddBlendedCourseVideoActivity extends AppCompatActivity {
    private static final String TAG = "OpAddVideoBlendedCourse";

    public static String blendedCourseVideoId = "";

    TextView tvNavbar;
    ConstraintLayout clBack;
    ImageView imgBack;

    EditText edtJudul, edtDeskripsi;
    ProgressBar pbUploadProses;
    Button btnPilihFile, btnHapus, btnSimpan, btnCancelUpload;
    TextView tvUploadProses, tvFileName;

    BlendedVideoModel blendedVideoModel;
    boolean thereIsData = false;
    int PICK_VIDEO_REQUEST_CODE = 11;
    Uri videoUri;
    boolean isUploading = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference blendedCourseVideoRef = db.collection("BlendedCourse")
            .document(blendedCourseDocId)
            .collection("VideoMateri");
    long dateCreated = 0;
    String urlVideo = "";
    UploadTask uploadTask;
    int index;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_blended_course_video);

        initView();
        checkIntent();
        onClick();
    }

    private void initView() {
        pd = new ProgressDialog(this);
        pd.setCancelable(false);

        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Detail Video Materi");
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

        edtJudul = findViewById(R.id.edt_op_add_blended_video_judul);
        edtDeskripsi = findViewById(R.id.edt_op_add_blended_video_deskripsi);
        pbUploadProses = findViewById(R.id.pb_op_add_blended_video_upload);
        btnPilihFile = findViewById(R.id.btn_op_add_blended_video_pilih_file);
        btnHapus = findViewById(R.id.btn_op_add_blended_video_hapus);
        btnSimpan = findViewById(R.id.btn_op_add_blended_video_simpan);
        btnCancelUpload = findViewById(R.id.btn_op_add_blended_video_upload);
        tvFileName = findViewById(R.id.tv_op_add_blended_video_pilih_file);
        tvUploadProses = findViewById(R.id.tv_op_add_blended_video_upload_process);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        blendedVideoModel = intent.getParcelableExtra("blended_video_model");
        if (blendedVideoModel != null) {
            thereIsData = true;
            edtJudul.setText(blendedVideoModel.getTitle());
            edtDeskripsi.setText(blendedVideoModel.getDescription());
            blendedCourseVideoId = blendedVideoModel.getDocumentId();
            btnHapus.setVisibility(View.VISIBLE);
            dateCreated = blendedVideoModel.getDateCreated();
            urlVideo = blendedVideoModel.getVideoUrl();
            index = intent.getIntExtra("index", -1);
        } else {
            blendedCourseVideoId = "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_VIDEO_REQUEST_CODE) {
                videoUri = data.getData();
                tvFileName.setText(getFileName(videoUri));
            }
        }
    }

    private void getVideoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Pilih Video"), PICK_VIDEO_REQUEST_CODE);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void onClick() {
        btnPilihFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUploading) {
                    showCancelUploadDialog();
                } else {
                    getVideoFromGallery();
                }
            }
        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUploading) {
                    showCancelUploadDialog();
                } else {
                    showHapusDialog();
                }
            }
        });
        btnCancelUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelUploadDialog();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUploading) {
                    showCancelUploadDialog();
                } else {
                    if (thereIsData) {
                        if (edtJudul.getText().toString().equals("") || edtDeskripsi.getText().toString().equals("")) {
                            Toast.makeText(OpAddBlendedCourseVideoActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                        } else {
                            showTambahDialog();
                        }
                    } else {
                        if (edtJudul.getText().toString().equals("") || edtDeskripsi.getText().toString().equals("") || videoUri == null) {
                            Toast.makeText(OpAddBlendedCourseVideoActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                        } else {
                            showTambahDialog();
                        }
                    }
                }
            }
        });
    }

    private void uploadvideo() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        firebaseStorage.setMaxUploadRetryTimeMillis(60000);
        if (!urlVideo.equals("")) {
            StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(urlVideo);
            deleteRef.delete()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OpAddBlendedCourseVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

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
                                urlVideo = uri.toString();
                                isUploading = false;
                                pbUploadProses.setProgress(0);
                                tvUploadProses.setText("Loading...");
                                btnCancelUpload.setVisibility(View.GONE);
                                btnSimpan.setVisibility(View.GONE);

                                if (thereIsData) {
                                    edit();
                                } else {
                                    tambah();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                isUploading = false;
                                Toast.makeText(OpAddBlendedCourseVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tvUploadProses.setText("");
                pbUploadProses.setProgress(0);
                isUploading = false;
                btnCancelUpload.setVisibility(View.GONE);

                btnCancelUpload.setEnabled(true);
                edtDeskripsi.setEnabled(true);
                edtJudul.setEnabled(true);
                btnSimpan.setEnabled(true);
                btnHapus.setEnabled(true);
                Toast.makeText(OpAddBlendedCourseVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                        .getTotalByteCount());

                if (progress >= 0) {
                    isUploading = true;
                }

                pbUploadProses.setProgress((int) progress);
                tvUploadProses.setText("Uploading " + (int) progress + "%");
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isCanceled()) {
                    videoUri = null;
                    tvFileName.setText("Belum Pilih Video");
                    tvUploadProses.setText("");
                    pbUploadProses.setProgress(0);
                    isUploading = false;
                    btnCancelUpload.setVisibility(View.GONE);

                    edtDeskripsi.setEnabled(true);
                    edtJudul.setEnabled(true);
                    btnCancelUpload.setEnabled(true);
                    btnSimpan.setEnabled(true);
                    btnHapus.setEnabled(true);
                }
            }
        });
    }

    private void tambah() {
        try {
            dateCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        String judul = edtJudul.getText().toString();
        String deskripsi = edtDeskripsi.getText().toString();

        final BlendedVideoModel blendedVideoModel = new BlendedVideoModel(judul, deskripsi, urlVideo, dateCreated);
        blendedCourseVideoRef.add(blendedVideoModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(OpAddBlendedCourseVideoActivity.this, "Video berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OpAddBlendedCourseVideoActivity.this, OpBlendedCourseVideoActivity.class);
                        intent.putExtra("blended_video_model", blendedVideoModel);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, ADD_REQUEST_CODE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpAddBlendedCourseVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void edit() {
        DocumentReference documentReference = blendedCourseVideoRef.document(blendedCourseVideoId);
        String judul = edtJudul.getText().toString();
        String deskripsi = edtDeskripsi.getText().toString();
        final BlendedVideoModel blendedVideoModel = new BlendedVideoModel(judul, deskripsi, urlVideo, dateCreated);

        documentReference.set(blendedVideoModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(OpAddBlendedCourseVideoActivity.this, "Video berhasil disimpan", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OpAddBlendedCourseVideoActivity.this, OpBlendedCourseVideoActivity.class);
                        intent.putExtra("blended_video_model", blendedVideoModel);
                        intent.putExtra("index", index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, UPDATE_REQUEST_CODE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddBlendedCourseVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hapus() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        firebaseStorage.setMaxUploadRetryTimeMillis(60000);
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(urlVideo);
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference documentReference = blendedCourseVideoRef.document(blendedCourseVideoId);
                        documentReference.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(OpAddBlendedCourseVideoActivity.this, "Video berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(OpAddBlendedCourseVideoActivity.this, OpBlendedCourseVideoActivity.class);
                                        intent.putExtra("index", index);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivityForResult(intent, DELETE_REQUEST_CODE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(OpAddBlendedCourseVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddBlendedCourseVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showHapusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghapus video ini?");
        builder.setTitle("Menghapus Video Kelas Blended");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddBlendedCourseVideoActivity.this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
                } else {
                    pd.setMessage("Menghapus...");
                    pd.show();
                    hapus();
                }
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

    private void showCancelUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin membatalkan upload?");
        builder.setTitle("Batalkan Upload");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddBlendedCourseVideoActivity.this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
                } else {
                    uploadTask.cancel();
                    Toast.makeText(OpAddBlendedCourseVideoActivity.this, "Cancelling...", Toast.LENGTH_SHORT).show();
                    btnCancelUpload.setEnabled(false);
                    btnSimpan.setEnabled(false);
                    btnHapus.setEnabled(false);
                }
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

    private void showTambahDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menyimpan video ini?");
        builder.setTitle("Tambah Video Kelas Blended");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddBlendedCourseVideoActivity.this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
                } else {
                    if (thereIsData && videoUri == null) {
                        pd.setMessage("Loading...");
                        pd.show();
                        edit();
                    } else {
                        edtDeskripsi.setEnabled(false);
                        edtJudul.setEnabled(false);
                        btnCancelUpload.setVisibility(View.VISIBLE);
                        uploadvideo();
                    }
                }
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

    @Override
    public void onBackPressed() {
        if (isUploading) {
            showCancelUploadDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isUploading) {
            uploadTask.cancel();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
