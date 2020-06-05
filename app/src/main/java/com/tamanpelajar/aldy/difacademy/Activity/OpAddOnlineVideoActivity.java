package com.tamanpelajar.aldy.difacademy.Activity;

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

import com.tamanpelajar.aldy.difacademy.Model.VideoModel;
import com.tamanpelajar.aldy.difacademy.R;
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

import static com.tamanpelajar.aldy.difacademy.Activity.OpAddOnlineCourseActivity.onlineCourseDocId;
import static com.tamanpelajar.aldy.difacademy.Activity.OpAddOnlineMateriActivity.onlineMateriDocId;
import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpAddOnlineVideoActivity extends AppCompatActivity {
    public static String onlineVideoDocId = "";

    TextView tvNavbar;
    ConstraintLayout clBack;
    ImageView imgBack;

    EditText edtJudul, edtDeskripsi;
    ProgressBar pbUploadProses;
    Button btnPilihFile, btnHapus, btnSimpan, btnCancelUpload;
    TextView tvUploadProses, tvFileName;

    VideoModel videoModel, oldVideoModel;
    boolean thereIsData = false;
    int PICK_VIDEO_REQUEST_CODE = 11;
    Uri videoUri;
    boolean isUploading = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference onlineVideoRef = db.collection("OnlineCourse")
            .document(onlineCourseDocId)
            .collection("OnlineMateri")
            .document(onlineMateriDocId)
            .collection("OnlineVideo");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    long dateCreated = 0;
    String urlVideo = "";
    UploadTask uploadTask;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_online_video);

        firebaseStorage.setMaxUploadRetryTimeMillis(60000);

        initView();
        checkIntent();
        onClick();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Detail Video Materi Online");
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

        edtJudul = findViewById(R.id.edt_op_add_online_video_judul);
        edtDeskripsi = findViewById(R.id.edt_op_add_online_video_deskripsi);
        pbUploadProses = findViewById(R.id.pb_op_add_online_video_upload);
        btnPilihFile = findViewById(R.id.btn_op_add_online_video_pilih_file);
        btnHapus = findViewById(R.id.btn_op_add_online_video_hapus);
        btnSimpan = findViewById(R.id.btn_op_add_online_video_simpan);
        btnCancelUpload = findViewById(R.id.btn_op_add_online_video_cancel);
        tvFileName = findViewById(R.id.tv_op_add_online_video_pilih_file);
        tvUploadProses = findViewById(R.id.tv_op_add_online_video_upload_process);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        videoModel = intent.getParcelableExtra("online_video_model");
        if (videoModel != null) {
            oldVideoModel = videoModel;
            thereIsData = true;
            edtJudul.setText(videoModel.getTitle());
            edtDeskripsi.setText(videoModel.getDescription());
            onlineVideoDocId = videoModel.getDocumentId();
            btnHapus.setVisibility(View.VISIBLE);
            dateCreated = videoModel.getDateCreated();
            urlVideo = videoModel.getVideoUrl();
            index = intent.getIntExtra("index", -1);
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

    private boolean isDataComplete() {
        if (thereIsData) {
            return !edtJudul.getText().toString().equals("") &&
                    !edtDeskripsi.getText().toString().equals("");
        } else {
            return !edtJudul.getText().toString().equals("") &&
                    !edtDeskripsi.getText().toString().equals("") &&
                    videoUri != null;
        }
    }

    private void onClick() {
        btnPilihFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideoFromGallery();
            }
        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHapusDialog();
            }
        });
        btnCancelUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelUploadDialog();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataComplete()) {
                    showSimpanDialog();
                } else {
                    Toast.makeText(OpAddOnlineVideoActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadVideoToFirebase() {
        final StorageReference uploadRef = firebaseStorage.getReference()
                .child("OnlineVideo/" + UUID.randomUUID().toString());

        uploadTask = uploadRef.putFile(videoUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                urlVideo = uri.toString();
                                isUploading = false;
                                pbUploadProses.setProgress(0);
                                tvUploadProses.setText("Loading...");
                                btnCancelUpload.setVisibility(View.GONE);
                                btnSimpan.setVisibility(View.GONE);
                                simpanVideo();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                isUploading = false;
                                Toast.makeText(OpAddOnlineVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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

                edtDeskripsi.setEnabled(true);
                edtJudul.setEnabled(true);
                btnHapus.setEnabled(true);
                btnPilihFile.setEnabled(true);
                btnSimpan.setEnabled(true);
                Toast.makeText(OpAddOnlineVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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
                    btnHapus.setEnabled(true);
                    btnPilihFile.setEnabled(true);
                    btnSimpan.setEnabled(true);
                }
            }
        });
    }

    private void simpanVideo() {
        String title = edtJudul.getText().toString();
        String description = edtDeskripsi.getText().toString();

        try {
            dateCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            Toast.makeText(OpAddOnlineVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        VideoModel model = new VideoModel(title, description, urlVideo, onlineCourseDocId, onlineMateriDocId, dateCreated);

        if (thereIsData) {
            editVideo(model);
        } else {
            tambahVideo(model);
        }
    }

    private void editVideo(final VideoModel model) {
        DocumentReference docRef = onlineVideoRef.document(onlineVideoDocId);
        docRef.set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (videoUri != null) {
                            deleteThanUpdateVideoInFirebase(model);
                        } else {
                            Intent intent = new Intent(OpAddOnlineVideoActivity.this, OpOnlineVideoActivity.class);
                            intent.putExtra("online_video_model", model);
                            intent.putExtra("index", index);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, UPDATE_REQUEST_CODE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpAddOnlineVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tambahVideo(final VideoModel model) {
        onlineVideoRef.add(model)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(OpAddOnlineVideoActivity.this, OpOnlineVideoActivity.class);
                        intent.putExtra("online_video_model", model);
                        intent.putExtra("index", index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, ADD_REQUEST_CODE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpAddOnlineVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteThanUpdateVideoInFirebase(final VideoModel model) {
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(oldVideoModel.getVideoUrl());
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(OpAddOnlineVideoActivity.this, OpOnlineVideoActivity.class);
                        intent.putExtra("online_video_model", model);
                        intent.putExtra("index", index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, UPDATE_REQUEST_CODE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpAddOnlineVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteVideoInFirebase() {
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(urlVideo);
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(OpAddOnlineVideoActivity.this, OpOnlineVideoActivity.class);
                        intent.putExtra("index", index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, DELETE_REQUEST_CODE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpAddOnlineVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hapusVideo() {
        DocumentReference docRef = onlineVideoRef.document(onlineVideoDocId);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteVideoInFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpAddOnlineVideoActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showCancelUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin membatalkan upload?");
        builder.setTitle("Cancel Upload");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddOnlineVideoActivity.this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
                } else {
                    uploadTask.cancel();
                    Toast.makeText(OpAddOnlineVideoActivity.this, "Cancelling...", Toast.LENGTH_SHORT).show();
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

    private void showSimpanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menyimpan video ini?");
        builder.setTitle("Simpan Video Materi Online");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddOnlineVideoActivity.this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
                } else {
                    if (videoUri != null) {
                        edtDeskripsi.setEnabled(false);
                        edtJudul.setEnabled(false);
                        btnHapus.setEnabled(false);
                        btnPilihFile.setEnabled(false);
                        btnSimpan.setEnabled(false);
                        btnCancelUpload.setVisibility(View.VISIBLE);
                        uploadVideoToFirebase();
                    } else {
                        simpanVideo();
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

    private void showHapusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghapus video ini?");
        builder.setTitle("Hapus Video Materi Online");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddOnlineVideoActivity.this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
                } else {
                    hapusVideo();
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
}
