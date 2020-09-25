package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.MateriBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.MateriModel;
import com.tamanpelajar.aldy.difacademy.Model.VideoBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.VideoModel;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddBlendedKelasActivity.kelasBlendedDocId;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpBlendedMateriActivity.isMateriChanged;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.PHOTO_PICK_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpAddBlendedMateriActivity extends AppCompatActivity {
    public static String blendedMateriDocId = "";

    private TextView tvNavbar;
    private ConstraintLayout clBack, clHapus;
    private ImageView imgBack, imgHapus;
    private ImageView imgThumbnail;
    private Button btnAddVideo, btnAddSoal, btnSimpan;
    private EditText edtJudul, edtDeskripsi, edtLampiran;
    private ConstraintLayout clAddPhoto;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference blendedMateriRef = db.collection(CommonMethod.refKelasBlended)
            .document(kelasBlendedDocId)
            .collection(CommonMethod.refMateriBlended);

    private boolean thereIsData = false;
    private boolean addVideo = false;
    private boolean addSoal = false;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private MateriBlendedModel materiModel, oldMateriModel;
    private Uri imageUri;
    private String thumbnailUrl = "";
    private int index;
    private long dateCreated = 0;
    private ProgressDialog pd;

    private ArrayList<String> listVideoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_blended_materi);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        firebaseStorage.setMaxUploadRetryTimeMillis(60000);
        listVideoUrl = new ArrayList<>();

        initView();
        onClick();
        checkIntent();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Materi Kelas Blended");
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
//        clHapus.setVisibility(View.VISIBLE);
        imgHapus = findViewById(R.id.img_icon3);
        imgHapus.setImageResource(R.drawable.ic_delete);

        imgThumbnail = findViewById(R.id.img_op_add_blended_materi_thumbnail);
        btnAddVideo = findViewById(R.id.btn_op_add_blended_materi_add_video);
        btnAddSoal = findViewById(R.id.btn_op_add_blended_materi_add_soal);
        btnSimpan = findViewById(R.id.btn_op_add_blended_materi_simpan);
        clAddPhoto = findViewById(R.id.cl_op_add_blended_materi_add_photo);
        edtJudul = findViewById(R.id.edt_op_add_blended_materi_judul);
        edtDeskripsi = findViewById(R.id.edt_op_add_blended_materi_deskripsi);
        edtLampiran = findViewById(R.id.edt_op_add_blended_materi_lampiran);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        materiModel = intent.getParcelableExtra(CommonMethod.intentMateriBlendedModel);
        if (materiModel != null) {
            oldMateriModel = materiModel;
            index = intent.getIntExtra(CommonMethod.intentIndex, -1);
            thereIsData = true;
            thumbnailUrl = materiModel.getThumbnailUrl();
            Glide.with(this)
                    .load(thumbnailUrl)
                    .into(imgThumbnail);
            edtJudul.setText(materiModel.getTitle());
            edtDeskripsi.setText(materiModel.getDescription());
            edtLampiran.setText(materiModel.getLampiranUrl());
            blendedMateriDocId = materiModel.getDocumentId();
            clHapus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PICK_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            Glide.with(this)
                    .load(imageUri)
                    .into(imgThumbnail);
        }
    }

    private void onClick() {
        clAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });
        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tambah video
                if (isDataComplete()) {
                    addSoal = false;
                    addVideo = true;

                    String title = edtJudul.getText().toString();
                    String deskripsi = edtDeskripsi.getText().toString();
                    String lampiran = edtLampiran.getText().toString();

                    if (thereIsData) {
                        if (title.equals(oldMateriModel.getTitle())
                                && deskripsi.equals(oldMateriModel.getDescription())
                                && lampiran.equals(oldMateriModel.getLampiranUrl())
                                && imageUri == null) {
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedVideoActivity.class);
                            startActivity(intent);
                        } else {
                            showSimpanDialog();
                        }
                    } else {
                        showSimpanDialog();
                    }
                } else {
                    Toast.makeText(OpAddBlendedMateriActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnAddSoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tambah soal
                if (isDataComplete()) {
                    addSoal = true;
                    addVideo = false;

                    String title = edtJudul.getText().toString();
                    String deskripsi = edtDeskripsi.getText().toString();
                    String lampiran = edtLampiran.getText().toString();

                    if (thereIsData) {
                        if (title.equals(oldMateriModel.getTitle())
                                && deskripsi.equals(oldMateriModel.getDescription())
                                && lampiran.equals(oldMateriModel.getLampiranUrl())
                                && imageUri == null) {
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedSoalActivity.class);
                            startActivity(intent);
                        } else {
                            showSimpanDialog();
                        }
                    } else {
                        showSimpanDialog();
                    }
                } else {
                    Toast.makeText(OpAddBlendedMateriActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        clHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hapus materi
                showHapusDialog();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //simpan materi
                addSoal = false;
                addVideo = false;
                if (isDataComplete()) {
                    showSimpanDialog();
                } else {
                    Toast.makeText(OpAddBlendedMateriActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_PICK_REQUEST_CODE);
    }

    private boolean isDataComplete() {
        if (thereIsData) {
            return !edtJudul.getText().toString().equals("");
        } else {
            return !edtJudul.getText().toString().equals("")
                    && imageUri != null;
        }
    }

    private void simpanMateri() {
        String title = edtJudul.getText().toString();
        String deskripsi = edtDeskripsi.getText().toString();
        String lampiran = edtLampiran.getText().toString();

        if (!CommonMethod.isInternetAvailable(OpAddBlendedMateriActivity.this)) {
            return;
        }

        dateCreated = CommonMethod.getTimeStamp();

        MateriBlendedModel model = new MateriBlendedModel(title, deskripsi, thumbnailUrl, lampiran, kelasBlendedDocId, dateCreated);

        if (thereIsData) {
            model.setDateCreated(oldMateriModel.getDateCreated());
            editMateri(model);
        } else {
            tambahMateri(model);
        }
    }

    private void editMateri(final MateriBlendedModel model) {
        DocumentReference docRef = blendedMateriRef.document(blendedMateriDocId);
        docRef.set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (imageUri != null) {
                            deletePhotoInFirebase(model);
                        } else {
                            if (addSoal) {
                                imageUri = null;
                                pd.dismiss();
                                Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedSoalActivity.class);
                                startActivity(intent);
                            } else if (addVideo) {
                                imageUri = null;
                                pd.dismiss();
                                Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedVideoActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedMateriActivity.class);
                                intent.putExtra(CommonMethod.intentMateriBlendedModel, model);
                                intent.putExtra(CommonMethod.intentIndex, index);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivityForResult(intent, UPDATE_REQUEST_CODE);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddBlendedMateriActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tambahMateri(final MateriBlendedModel model) {
        blendedMateriRef.add(model)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        blendedMateriDocId = documentReference.getId();
                        materiModel = model;
//                        materiModel.setDocumentId(blendedMateriDocId);

                        oldMateriModel = materiModel;
                        imageUri = null;
                        thereIsData = true;
                        clHapus.setVisibility(View.VISIBLE);

                        pd.dismiss();

                        if (addSoal) {
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedSoalActivity.class);
                            startActivity(intent);
                        } else if (addVideo) {
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedVideoActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedMateriActivity.class);
                            intent.putExtra(CommonMethod.intentMateriBlendedModel, model);
                            intent.putExtra(CommonMethod.intentIndex, index);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, ADD_REQUEST_CODE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddBlendedMateriActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPhotoToFirebase() {
        final StorageReference ref = firebaseStorage.getReference().child(CommonMethod.storageBlendedMateri + UUID.randomUUID().toString());
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        thumbnailUrl = uri.toString();
                                        simpanMateri();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddBlendedMateriActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deletePhotoInFirebase(final MateriBlendedModel model) {
        //method ini khusus untuk edit materi saja
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(oldMateriModel.getThumbnailUrl());
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        imageUri = null;
                        pd.dismiss();

                        if (addSoal) {
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedSoalActivity.class);
                            startActivity(intent);
                        } else if (addVideo) {
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedVideoActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedMateriActivity.class);
                            intent.putExtra(CommonMethod.intentMateriBlendedModel, model);
                            intent.putExtra(CommonMethod.intentIndex, index);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, UPDATE_REQUEST_CODE);
                        }
                    }
                });
    }

    private void showSimpanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menyimpan materi ini?");
        builder.setTitle("Simpan Materi Kelas Blended");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!CommonMethod.isInternetAvailable(OpAddBlendedMateriActivity.this)) {
                    return;
                }

                isMateriChanged = true;
                pd.show();
                if (imageUri != null) {
                    uploadPhotoToFirebase();
                } else {
                    simpanMateri();
                }
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                addSoal = false;
                addVideo = false;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showHapusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghapus materi ini?");
        builder.setTitle("Hapus Materi Kelas Blended");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CommonMethod.isInternetAvailable(OpAddBlendedMateriActivity.this)) {
                    pd.show();
                    hapusMateri();
                }
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                addSoal = false;
                addVideo = false;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void hapusMateri() {
        /**
         * ambil link blended video di storage
         * hapus blended video document
         * hapus blended video collection
         * hapus blended video storage
         *
         * hapus blended soal document
         * hapus blended soal collection
         *
         * ambil link blended materi thumbnail
         * hapus blended materi document
         * hapus blended materi thumbnail storage
         */
        getListVideoUrl();
    }

    private void getListVideoUrl() {
        CollectionReference ref = blendedMateriRef.document(blendedMateriDocId)
                .collection(CommonMethod.refVideoBlended);

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            VideoBlendedModel model = documentSnapshot.toObject(VideoBlendedModel.class);
                            listVideoUrl.add(model.getVideoUrl());
                        }
                        hapusBlendedVideoDoc();
                    }
                });
    }

    private void hapusBlendedVideoDoc() {
        final CollectionReference ref = blendedMateriRef.document(blendedMateriDocId)
                .collection(CommonMethod.refVideoBlended);

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = ref.document(documentSnapshot.getId());
                            docRef.delete();
                        }
                        hapusBlendedVideoStorage();
                    }
                });
    }

    private void hapusBlendedVideoStorage() {
        for (String url : listVideoUrl) {
            StorageReference ref = firebaseStorage.getReferenceFromUrl(url);
            ref.delete();
        }
        hapusBlendedSoalDoc();
    }

    private void hapusBlendedSoalDoc() {
        final CollectionReference ref = blendedMateriRef.document(blendedMateriDocId)
                .collection(CommonMethod.refSoalBlended);

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = ref.document(documentSnapshot.getId());
                            docRef.delete();
                        }
                        hapusBlendedMateriDoc();
                    }
                });
    }

    private void hapusBlendedMateriDoc() {
        DocumentReference ref = blendedMateriRef.document(blendedMateriDocId);
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hapusBlendedMateriStorage();
                    }
                });
    }

    private void hapusBlendedMateriStorage() {
        StorageReference ref = firebaseStorage.getReferenceFromUrl(thumbnailUrl);
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedMateriActivity.class);
                        intent.putExtra(CommonMethod.intentIndex, index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, DELETE_REQUEST_CODE);
                    }
                });
    }
}
