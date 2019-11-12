package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.aldy.difacademy.Model.NewsModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class OpAddNewsActivity extends AppCompatActivity {

    private ConstraintLayout clAddPhoto, clBack;
    private ImageView imgThumbnail;
    private EditText edtJudul, edtIsi;
    private Button btnHapus, btnSimpan;
    private Uri imageUri;
    private static final String TAG = "OpAddNewsActivity";
    private ProgressDialog progressDialog;
    private StorageReference storageReference;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference newsRef = db.collection("News");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_news);
        initFirebaseStorage();
        findView();
        onClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgThumbnail.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    private void initFirebaseStorage() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void findView() {
        clAddPhoto = findViewById(R.id.cl_op_add_news_add_photo);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgThumbnail = findViewById(R.id.img_op_add_news_thumbnail);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        imgBack.setVisibility(View.VISIBLE);
        TextView tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Tambah Berita");
        edtJudul = findViewById(R.id.edt_judul);
        edtIsi = findViewById(R.id.edt_op_add_news_isi);
        btnHapus = findViewById(R.id.btn_op_add_news_hapus);
        btnSimpan = findViewById(R.id.btn_op_add_news_simpan);
        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        if (intent.getParcelableExtra("newsModel") == null) {
            btnHapus.setVisibility(View.GONE);
        } else {
            btnHapus.setVisibility(View.VISIBLE);
            NewsModel newsModel = intent.getParcelableExtra("newsModel");
            setViewWithParcelable(newsModel);
        }
    }

    private void setViewWithParcelable(NewsModel newsModel) {
        edtJudul.setText(newsModel.getJudul());
        edtIsi.setText(newsModel.getIsi());
        Glide.with(this).load(newsModel.getLinkfoto()).into(imgThumbnail);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), 1);
            }

        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanData();
            }
        });
    }

    private void simpanData() {
        if (imageUri == null) {
            Toast.makeText(this, "Harap unggah foto thumbnail terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else if (edtJudul.length() == 0) {
            Toast.makeText(this, "Harap memasukkan judul berita terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else if (edtIsi.length() == 0) {
            Toast.makeText(this, "Harap memasukkan isi berita terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else {
            showDialogKonfirmasi();
        }
    }

    private void uploadToFirebase() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        final StorageReference ref = storageReference.child("Berita/" + UUID.randomUUID().toString());
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        addToFirestore(uri.toString());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Proses unggah " + (int) progress + "%");
                    }
                });

    }

    private void addToFirestore(String downloadURL) {
        NewsModel newsModel = new NewsModel(edtJudul.getText().toString(), edtIsi.getText().toString(), downloadURL);
        newsRef.add(newsModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast.makeText(OpAddNewsActivity.this, "Berita telah ditambahkan", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });

    }

    private void showDialogKonfirmasi() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menyimpan?");
        builder.setTitle("Simpan");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadToFirebase();
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
