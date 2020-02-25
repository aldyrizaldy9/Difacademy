package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import com.example.aldy.difacademy.Model.MateriModel;
import com.example.aldy.difacademy.Model.VideoModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
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

import static com.example.aldy.difacademy.Activity.OpAddBlendedCourseActivity.blendedCourseDocId;
import static com.example.aldy.difacademy.Activity.OpMainActivity.ADD_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.DELETE_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.PHOTO_PICK_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpAddBlendedMateriActivity extends AppCompatActivity {
    public static String blendedMateriDocId = "";

    TextView tvNavbar;
    ConstraintLayout clBack;
    ImageView imgBack;

    ImageView imgThumbnail;
    Button btnAddVideo, btnAddSoal, btnHapus, btnSimpan;
    EditText edtJudul, edtHarga;
    ConstraintLayout clAddPhoto;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference blendedMateriRef = db.collection("BlendedCourse")
            .document(blendedCourseDocId)
            .collection("BlendedMateri");
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
        tvNavbar.setText("Detail Materi Kelas Blended");
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

        imgThumbnail = findViewById(R.id.img_op_add_blended_materi_thumbnail);
        btnAddVideo = findViewById(R.id.btn_op_add_blended_materi_add_video);
        btnAddSoal = findViewById(R.id.btn_op_add_blended_materi_add_soal);
        btnHapus = findViewById(R.id.btn_op_add_blended_materi_hapus);
        btnSimpan = findViewById(R.id.btn_op_add_blended_materi_simpan);
        clAddPhoto = findViewById(R.id.cl_op_add_blended_materi_add_photo);
        edtJudul = findViewById(R.id.edt_op_add_blended_materi_judul);
        edtHarga = findViewById(R.id.edt_op_add_blended_materi_harga);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        materiModel = intent.getParcelableExtra("blended_materi_model");
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
            blendedMateriDocId = materiModel.getDocumentId();
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
                    String harga = edtHarga.getText().toString();
                    if (thereIsData) {
                        if (title.equals(oldMateriModel.getTitle())
                                && harga.equals(oldMateriModel.getHarga())
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
                    Toast.makeText(OpAddBlendedMateriActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
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
        String title = edtJudul.getText().toString();
        String harga = edtHarga.getText().toString();

        try {
            dateCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            pd.dismiss();
            Toast.makeText(OpAddBlendedMateriActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        MateriModel model = new MateriModel(title, thumbnailUrl, harga, blendedCourseDocId, dateCreated);

        if (thereIsData) {
            editMateri(model);
        } else {
            tambahMateri(model);
        }
    }

    private void editMateri(final MateriModel model) {
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
                                intent.putExtra("blended_materi_model", model);
                                intent.putExtra("index", index);
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

    private void tambahMateri(final MateriModel model) {
        blendedMateriRef.add(model)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        blendedMateriDocId = documentReference.getId();
                        if (addSoal) {
                            thereIsData = true;
                            materiModel = new MateriModel(edtJudul.getText().toString(), thumbnailUrl, edtHarga.getText().toString(), blendedCourseDocId, dateCreated);
                            oldMateriModel = materiModel;
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedSoalActivity.class);
                            startActivity(intent);
                        } else if (addVideo) {
                            thereIsData = true;
                            materiModel = new MateriModel(edtJudul.getText().toString(), thumbnailUrl, edtHarga.getText().toString(), blendedCourseDocId, dateCreated);
                            oldMateriModel = materiModel;
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedVideoActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedMateriActivity.class);
                            intent.putExtra("blended_materi_model", model);
                            intent.putExtra("index", index);
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
        final StorageReference ref = firebaseStorage.getReference().child("BlendedMateri/" + UUID.randomUUID().toString());
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

    private void deletePhotoInFirebase(final MateriModel model) {
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(oldMateriModel.getThumbnailUrl());
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (addVideo) {
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedVideoActivity.class);
                            startActivity(intent);
                        } else if (addSoal) {
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedVideoActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OpAddBlendedMateriActivity.this, OpBlendedMateriActivity.class);
                            intent.putExtra("blended_materi_model", model);
                            intent.putExtra("index", index);
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
                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddBlendedMateriActivity.this, "Tidak ada koneksi intenet!", Toast.LENGTH_SHORT).show();
                } else {
                    pd.show();
                    if (imageUri != null) {
                        uploadPhotoToFirebase();
                    } else {
                        simpanMateri();
                    }
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
                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddBlendedMateriActivity.this, "Tidak ada koneksi intenet!", Toast.LENGTH_SHORT).show();
                } else {
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
        //ambil link blended video di storage
        //hapus blended video document
        //hapus blended video collection
        //hapus blended video storage
        //hapus blended soal document
        //hapus blended soal collection
        //ambil link blended materi thumbnail
        //hapus blended materi document
        //hapus blended materi thumbnail storage
        getListVideoUrl();
    }

    private void getListVideoUrl() {
        CollectionReference ref = blendedMateriRef.document(blendedMateriDocId)
                .collection("BlendedVideo");

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            VideoModel model = documentSnapshot.toObject(VideoModel.class);
                            listVideoUrl.add(model.getVideoUrl());
                        }
                        hapusBlendedVideoDoc();
                    }
                });
    }

    private void hapusBlendedVideoDoc() {
        final CollectionReference ref = blendedMateriRef.document(blendedMateriDocId)
                .collection("BlendedVideo");

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
                .collection("BlendedSoal");

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
                        intent.putExtra("index", index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, DELETE_REQUEST_CODE);
                    }
                });
    }
}
