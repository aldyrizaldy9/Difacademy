package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.tamanpelajar.aldy.difacademy.Model.MateriModel;
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

import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddOnlineKelasActivity.onlineCourseDocId;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.PHOTO_PICK_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpAddOnlineMateriActivity extends AppCompatActivity {
    public static String onlineMateriDocId = "";

    TextView tvNavbar;
    ConstraintLayout clBack;
    ImageView imgBack;

    ImageView imgThumbnail;
    Button btnAddVideo, btnAddSoal, btnHapus, btnSimpan;
    EditText edtJudul, edtHarga;
    ConstraintLayout clAddPhoto;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference onlineMateriRef = db.collection("OnlineCourse")
            .document(onlineCourseDocId)
            .collection("OnlineMateri");
    boolean thereIsData = false;
    boolean addVideo = false;
    boolean addSoal = false;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    MateriModel materiModel, oldMateriModel;
    Uri imageUri;
    String thumbnailUrl = "";
    int index;
    long dateCreated = 0;
    ProgressDialog pd;

    ArrayList<String> listVideoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_online_materi);

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
        tvNavbar.setText("Materi Kelas Online");
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

        imgThumbnail = findViewById(R.id.img_op_add_online_materi_thumbnail);
        btnAddVideo = findViewById(R.id.btn_op_add_online_materi_add_video);
        btnAddSoal = findViewById(R.id.btn_op_add_online_materi_add_soal);
        btnHapus = findViewById(R.id.btn_op_add_online_materi_hapus);
        btnSimpan = findViewById(R.id.btn_op_add_online_materi_simpan);
        clAddPhoto = findViewById(R.id.cl_op_add_online_materi_add_photo);
        edtJudul = findViewById(R.id.edt_op_add_online_materi_judul);
        edtHarga = findViewById(R.id.edt_op_add_online_materi_harga);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        materiModel = intent.getParcelableExtra("online_materi_model");
        if (materiModel != null) {
            oldMateriModel = materiModel;
            index = intent.getIntExtra("index", -1);
            thereIsData = true;
            thumbnailUrl = materiModel.getThumbnailUrl();
            btnHapus.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(thumbnailUrl)
                    .into(imgThumbnail);
            edtJudul.setText(materiModel.getTitle());
            edtHarga.setText(materiModel.getHarga());
            onlineMateriDocId = materiModel.getDocumentId();
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
                    String harga = edtHarga.getText().toString();
                    if (thereIsData) {
                        if (title.equals(oldMateriModel.getTitle())
                                && harga.equals(oldMateriModel.getHarga())
                                && imageUri == null) {
                            Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineVideoActivity.class);
                            startActivity(intent);
                        } else {
                            showSimpanDialog();
                        }
                    } else {
                        showSimpanDialog();
                    }
                } else {
                    Toast.makeText(OpAddOnlineMateriActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
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
                    String harga = edtHarga.getText().toString();
                    if (thereIsData) {
                        if (title.equals(oldMateriModel.getTitle())
                                && harga.equals(oldMateriModel.getHarga())
                                && imageUri == null) {
                            Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineSoalActivity.class);
                            startActivity(intent);
                        } else {
                            showSimpanDialog();
                        }
                    } else {
                        showSimpanDialog();
                    }
                } else {
                    Toast.makeText(OpAddOnlineMateriActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(OpAddOnlineMateriActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
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
            return !edtJudul.getText().toString().equals("") && !edtHarga.getText().toString().equals("");
        } else {
            return !edtJudul.getText().toString().equals("")
                    && !edtHarga.getText().toString().equals("")
                    && imageUri != null;
        }
    }

    private void simpanMateri() {
        if (!CommonMethod.isInternetAvailable(OpAddOnlineMateriActivity.this)) {
            pd.dismiss();
            return;
        }

        dateCreated = CommonMethod.getTimeStamp();

        String title = edtJudul.getText().toString();
        String harga = edtHarga.getText().toString();

        MateriModel model = new MateriModel(title, thumbnailUrl, harga, onlineCourseDocId, dateCreated);

        if (thereIsData) {
            editMateri(model);
        } else {
            tambahMateri(model);
        }
    }

    private void editMateri(final MateriModel model) {
        DocumentReference docRef = onlineMateriRef.document(onlineMateriDocId);
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
                                Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineSoalActivity.class);
                                startActivity(intent);
                            } else if (addVideo) {
                                imageUri = null;
                                pd.dismiss();
                                Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineVideoActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineMateriActivity.class);
                                intent.putExtra(CommonMethod.intentMateriOnlineModel, model);
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
                        Toast.makeText(OpAddOnlineMateriActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tambahMateri(final MateriModel model) {
        onlineMateriRef.add(model)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        onlineMateriDocId = documentReference.getId();
                        if (addSoal) {
                            thereIsData = true;
                            materiModel = new MateriModel(edtJudul.getText().toString(), thumbnailUrl, edtHarga.getText().toString(), onlineCourseDocId, dateCreated);
                            oldMateriModel = materiModel;
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineSoalActivity.class);
                            startActivity(intent);
                        } else if (addVideo) {
                            thereIsData = true;
                            materiModel = new MateriModel(edtJudul.getText().toString(), thumbnailUrl, edtHarga.getText().toString(), onlineCourseDocId, dateCreated);
                            oldMateriModel = materiModel;
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineVideoActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineMateriActivity.class);
                            intent.putExtra(CommonMethod.intentMateriOnlineModel, model);
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
                        Toast.makeText(OpAddOnlineMateriActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPhotoToFirebase() {
        final StorageReference ref = firebaseStorage.getReference().child("OnlineMateri/" + UUID.randomUUID().toString());
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
                        Toast.makeText(OpAddOnlineMateriActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deletePhotoInFirebase(final MateriModel model) {
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(oldMateriModel.getThumbnailUrl());
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (addVideo) {
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineVideoActivity.class);
                            startActivity(intent);
                        } else if (addSoal) {
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineVideoActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineMateriActivity.class);
                            intent.putExtra(CommonMethod.intentMateriOnlineModel, model);
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
        builder.setTitle("Simpan Materi Kelas Online");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!CommonMethod.isInternetAvailable(OpAddOnlineMateriActivity.this)) {
                    return;
                }

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
        builder.setTitle("Hapus Materi Kelas Online");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!CommonMethod.isInternetAvailable(OpAddOnlineMateriActivity.this)) {
                    return;
                }

                pd.show();
                hapusMateri();
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
        //ambil link online video di storage
        //hapus online video document
        //hapus online video collection
        //hapus online video storage
        //hapus online soal document
        //hapus online soal collection
        //ambil link online materi thumbnail
        //hapus online materi document
        //hapus online materi thumbnail storage
        getListVideoUrl();
    }

    private void getListVideoUrl() {
        CollectionReference ref = onlineMateriRef.document(onlineMateriDocId)
                .collection("OnlineVideo");

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            VideoModel model = documentSnapshot.toObject(VideoModel.class);
                            listVideoUrl.add(model.getVideoUrl());
                        }
                        hapusOnlineVideoDoc();
                    }
                });
    }

    private void hapusOnlineVideoDoc() {
        final CollectionReference ref = onlineMateriRef.document(onlineMateriDocId)
                .collection("OnlineVideo");

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = ref.document(documentSnapshot.getId());
                            docRef.delete();
                        }
                        hapusOnlineVideoStorage();
                    }
                });
    }

    private void hapusOnlineVideoStorage() {
        for (String url : listVideoUrl) {
            StorageReference ref = firebaseStorage.getReferenceFromUrl(url);
            ref.delete();
        }
        hapusOnlineSoalDoc();
    }

    private void hapusOnlineSoalDoc() {
        final CollectionReference ref = onlineMateriRef.document(onlineMateriDocId)
                .collection("OnlineSoal");

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = ref.document(documentSnapshot.getId());
                            docRef.delete();
                        }
                        hapusOnlineMateriDoc();
                    }
                });
    }

    private void hapusOnlineMateriDoc() {
        DocumentReference ref = onlineMateriRef.document(onlineMateriDocId);
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hapusOnlineMateriStorage();
                    }
                });
    }

    private void hapusOnlineMateriStorage() {
        StorageReference ref = firebaseStorage.getReferenceFromUrl(thumbnailUrl);
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(OpAddOnlineMateriActivity.this, OpOnlineMateriActivity.class);
                        intent.putExtra(CommonMethod.intentIndex, index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, DELETE_REQUEST_CODE);
                    }
                });
    }
}
