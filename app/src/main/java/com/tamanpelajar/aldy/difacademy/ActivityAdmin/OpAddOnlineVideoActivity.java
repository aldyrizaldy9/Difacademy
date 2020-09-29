package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.VideoOnlineModel;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddOnlineKelasActivity.kelasOnlineDocId;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddOnlineMateriActivity.onlineMateriDocId;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpAddOnlineVideoActivity extends AppCompatActivity {
    public static String onlineVideoDocId = "";

    private TextView tvNavbar;
    private ConstraintLayout clBack, clHapus;
    private ImageView imgBack, imgHapus;

    private EditText edtJudul, edtDeskripsi;
    private ProgressBar pbUploadProses;
    private Button btnPilihFile, btnSimpan, btnCancelUpload;
    private TextView tvUploadProses, tvFileName;

    private VideoOnlineModel videoModel, oldVideoModel;
    private boolean thereIsData = false;
    private int PICK_VIDEO_REQUEST_CODE = 11;

    private Uri videoUri;
    private boolean isUploading = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference onlineVideoRef = db.collection(CommonMethod.refKelasOnline)
            .document(kelasOnlineDocId)
            .collection(CommonMethod.refMateriOnline)
            .document(onlineMateriDocId)
            .collection(CommonMethod.refVideoOnline);
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private long dateCreated = 0;
    private String urlVideo = "";
    private UploadTask uploadTask;
    private int index;

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
        tvNavbar.setText("Video Materi Online");
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
        clHapus = findViewById(R.id.cl_icon3);
        imgHapus = findViewById(R.id.img_icon3);
        imgHapus.setImageResource(R.drawable.ic_delete);

        edtJudul = findViewById(R.id.edt_op_add_online_video_judul);
        edtDeskripsi = findViewById(R.id.edt_op_add_online_video_deskripsi);
        pbUploadProses = findViewById(R.id.pb_op_add_online_video_upload);
        btnPilihFile = findViewById(R.id.btn_op_add_online_video_pilih_file);
        btnSimpan = findViewById(R.id.btn_op_add_online_video_simpan);
        btnCancelUpload = findViewById(R.id.btn_op_add_online_video_cancel);
        tvFileName = findViewById(R.id.tv_op_add_online_video_pilih_file);
        tvUploadProses = findViewById(R.id.tv_op_add_online_video_upload_process);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        videoModel = intent.getParcelableExtra(CommonMethod.intentVideoOnlineModel);
        if (videoModel != null) {
            oldVideoModel = videoModel;
            thereIsData = true;
            clHapus.setVisibility(View.VISIBLE);

            edtJudul.setText(videoModel.getTitle());
            edtDeskripsi.setText(videoModel.getDescription());
            onlineVideoDocId = videoModel.getDocumentId();
            dateCreated = videoModel.getDateCreated();
            urlVideo = videoModel.getVideoUrl();
            index = intent.getIntExtra(CommonMethod.intentIndex, -1);
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
        clHapus.setOnClickListener(new View.OnClickListener() {
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
                .child(CommonMethod.storageOnlineVideo + UUID.randomUUID().toString());

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
                    btnPilihFile.setEnabled(true);
                    btnSimpan.setEnabled(true);
                }
            }
        });
    }

    private void simpanVideo() {
        String title = edtJudul.getText().toString();
        String description = edtDeskripsi.getText().toString();

        VideoOnlineModel model = new VideoOnlineModel(title, description, urlVideo, kelasOnlineDocId, onlineMateriDocId, dateCreated);

        if (thereIsData) {
            editVideo(model);
        } else {
            tambahVideo(model);
        }
    }

    private void editVideo(final VideoOnlineModel model) {
        model.setDateCreated(oldVideoModel.getDateCreated());
        DocumentReference docRef = onlineVideoRef.document(onlineVideoDocId);
        docRef.set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (videoUri != null) {
                            deleteThanUpdateVideoInFirebase(model);
                        } else {
                            Intent intent = new Intent(OpAddOnlineVideoActivity.this, OpOnlineVideoActivity.class);
                            intent.putExtra(CommonMethod.intentVideoOnlineModel, model);
                            intent.putExtra(CommonMethod.intentIndex, index);
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

    private void tambahVideo(final VideoOnlineModel model) {
        onlineVideoRef.add(model)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(OpAddOnlineVideoActivity.this, OpOnlineVideoActivity.class);
                        intent.putExtra(CommonMethod.intentVideoOnlineModel, model);
                        intent.putExtra(CommonMethod.intentIndex, index);
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

    private void deleteThanUpdateVideoInFirebase(final VideoOnlineModel model) {
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(oldVideoModel.getVideoUrl());
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(OpAddOnlineVideoActivity.this, OpOnlineVideoActivity.class);
                        intent.putExtra(CommonMethod.intentVideoOnlineModel, model);
                        intent.putExtra(CommonMethod.intentIndex, index);
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
                        intent.putExtra(CommonMethod.intentIndex, index);
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
                if (!CommonMethod.isInternetAvailable(OpAddOnlineVideoActivity.this)) {
                    return;
                }

                uploadTask.cancel();
                Toast.makeText(OpAddOnlineVideoActivity.this, "Cancelling...", Toast.LENGTH_SHORT).show();
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
                if (!CommonMethod.isInternetAvailable(OpAddOnlineVideoActivity.this)) {
                    return;
                }

                dateCreated = CommonMethod.getTimeStamp();

                if (videoUri != null) {
                    edtDeskripsi.setEnabled(false);
                    edtJudul.setEnabled(false);
                    clHapus.setVisibility(View.GONE);
                    btnPilihFile.setEnabled(false);
                    btnSimpan.setEnabled(false);
                    btnCancelUpload.setVisibility(View.VISIBLE);
                    uploadVideoToFirebase();
                } else {
                    simpanVideo();
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
                if (!CommonMethod.isInternetAvailable(OpAddOnlineVideoActivity.this)) {
                    return;
                }

                tvUploadProses.setText("Deleting...");
                hapusVideo();
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
