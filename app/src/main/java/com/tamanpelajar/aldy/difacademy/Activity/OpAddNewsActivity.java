package com.tamanpelajar.aldy.difacademy.Activity;

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
import com.tamanpelajar.aldy.difacademy.Model.NewsModel;
import com.tamanpelajar.aldy.difacademy.R;
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

import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.PHOTO_PICK_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpAddNewsActivity extends AppCompatActivity {

    private static final String TAG = "OpAddNewsActivity";
    private ConstraintLayout clAddPhoto, clBack;
    private ImageView imgThumbnail;
    private EditText edtJudul, edtIsi;
    private Button btnHapus, btnSimpan;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference newsRef = db.collection("News");

    private NewsModel newsModel;
    private int index;
    private long dateCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_news);
        initFirebaseStorage();
        initView();
        onClick();
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

    private void initFirebaseStorage() {
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseStorage.setMaxUploadRetryTimeMillis(60000);
        storageReference = firebaseStorage.getReference();
    }

    private void initView() {
        clAddPhoto = findViewById(R.id.cl_op_add_news_add_photo);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgThumbnail = findViewById(R.id.img_op_add_news_thumbnail);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        imgBack.setVisibility(View.VISIBLE);
        TextView tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText(R.string.tambah_artikel);
        edtJudul = findViewById(R.id.edt_judul);
        edtIsi = findViewById(R.id.edt_op_add_news_isi);
        btnHapus = findViewById(R.id.btn_op_add_news_hapus);
        btnSimpan = findViewById(R.id.btn_op_add_news_simpan);
        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();

        //Menentukan index apabila ada
        int index = intent.getIntExtra("index", -1);
        if (index != -1) {
            this.index = index;
        }

        //Jika user menambahkan berita baru maka tombol hapus akan di hide
        if (intent.getParcelableExtra("newsModel") == null) {
            btnHapus.setVisibility(View.GONE);
        } else {
            btnHapus.setVisibility(View.VISIBLE);
            newsModel = intent.getParcelableExtra("newsModel");
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
                startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PHOTO_PICK_REQUEST_CODE);
            }

        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konfirmasiHapus();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konfirmasiSimpan();
            }
        });
    }

    private void konfirmasiHapus() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghapus?");
        builder.setTitle("Hapus");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddNewsActivity.this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
                } else {
                    deleteImageFromFirebaseStorage();
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

    private void deleteImageFromFirebaseStorage() {
        progressDialog.setMessage("Menghapus");
        progressDialog.setCancelable(false);
        progressDialog.show();
        storageReference = firebaseStorage.getReferenceFromUrl(newsModel.getLinkfoto());
        storageReference
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteNewsFromFirestore();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void deleteNewsFromFirestore() {
        newsRef.document(newsModel.getNewsId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(OpAddNewsActivity.this, OpNewsActivity.class);
                        intent.putExtra("index", index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, DELETE_REQUEST_CODE);
                        Toast.makeText(OpAddNewsActivity.this, "Artikel telah dihapus", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void konfirmasiSimpan() {
        //Jika user menambahkan berita tetapi belum menambahkan foto maka akan muncul toast
        if (imageUri == null && newsModel == null) {
            Toast.makeText(this, "Harap unggah foto thumbnail terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else if (edtJudul.length() == 0) {
            Toast.makeText(this, "Harap memasukkan judul artikel terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else if (edtIsi.length() == 0) {
            Toast.makeText(this, "Harap memasukkan isi artikel terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Apakah anda yakin ingin menyimpan?");
            builder.setTitle("Simpan");
            builder.setCancelable(false);
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!isNetworkConnected()) {
                        Toast.makeText(OpAddNewsActivity.this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (newsModel == null) {
                            uploadImageToFirebaseStorage();
                        } else {
                            updateImageToFirebaseStorage();
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
    }

    private void uploadImageToFirebaseStorage() {
        progressDialog.setCancelable(false);
        progressDialog.show();
        final StorageReference ref = storageReference.child("Berita/" + UUID.randomUUID().toString());
        ref
                .putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        addNewsToFirestore(uri.toString());
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

    private void addNewsToFirestore(String downloadURL) {
        try {
            dateCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            progressDialog.dismiss();
            return;
        }
        if (dateCreated != 0) {
            final NewsModel newsModel = new NewsModel(edtJudul.getText().toString(), edtIsi.getText().toString(), downloadURL, dateCreated);
            newsRef
                    .add(newsModel)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(OpAddNewsActivity.this, OpNewsActivity.class);
                            intent.putExtra("newsModel", newsModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, ADD_REQUEST_CODE);
                            Toast.makeText(OpAddNewsActivity.this, "Artikel telah ditambahkan", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });
        }

    }

    private void updateImageToFirebaseStorage() {
        progressDialog.setCancelable(false);
        progressDialog.show();
        storageReference = firebaseStorage.getReferenceFromUrl(newsModel.getLinkfoto());
        //Jika user mengubah gambar maka gambar di firebase storage akan diupdate
        if (imageUri != null) {
            storageReference
                    .putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            updateNewstoFirestore(uri.toString());
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
        } else {
            //Jika tidak maka akan diupdate judul dan isinya saja
            progressDialog.setMessage("Menyimpan");
            updateNewstoFirestore(newsModel.getLinkfoto());
        }
    }

    private void updateNewstoFirestore(String downloadURL) {
        try {
            dateCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            progressDialog.dismiss();
            return;
        }
        if (dateCreated != 0) {
            final NewsModel newsModel = new NewsModel(edtJudul.getText().toString(), edtIsi.getText().toString(), downloadURL, dateCreated);
            newsRef.document(this.newsModel.getNewsId())
                    .set(newsModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(OpAddNewsActivity.this, OpNewsActivity.class);
                            intent.putExtra("newsModel", newsModel);
                            intent.putExtra("index", index);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, UPDATE_REQUEST_CODE);
                            Toast.makeText(OpAddNewsActivity.this, "Artikel telah disunting", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
