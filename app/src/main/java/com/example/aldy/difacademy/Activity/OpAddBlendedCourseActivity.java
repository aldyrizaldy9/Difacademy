package com.example.aldy.difacademy.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.aldy.difacademy.Model.BlendedCourseModel;
import com.example.aldy.difacademy.Model.BlendedVideoModel;
import com.example.aldy.difacademy.Model.TagModel;
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
import java.util.List;
import java.util.UUID;

import static com.example.aldy.difacademy.Activity.OpMainActivity.ADD_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.DELETE_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.PHOTO_PICK_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.UPDATE_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.WRITE_PERM_REQUEST_CODE;

public class OpAddBlendedCourseActivity extends AppCompatActivity {
    private static final String TAG = "OpAddBlendedCourseActiv";

    public static String blendedCourseDocId = "";

    TextView tvNavbar;
    ConstraintLayout clBack;
    ImageView imgBack;

    ImageView imgThumbnail;
    ConstraintLayout clAddPhoto, clAddVideo, clAddQuiz;
    EditText edtJudul, edtDeskripsi, edtLinkGDrive, edtHargaKelas;
    Spinner spnTag;
    Button btnHapus, btnSimpan;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference blendedCourseRef = db.collection("BlendedCourse");
    String tagCourse = "";
    String tagCourseId = "";
    boolean thereIsData = false;
    boolean addVideo = false;
    boolean addQuiz = false;
    BlendedCourseModel blendedCourseModel;
    Uri imageUri;
    String thumbnailUrl = "";
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    int index;
    long dateCreated = 0;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_blended_course);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        initView();
        onClick();
        checkIntent();
        loadTags();
    }

    private void checkIntent() {
        Intent intent = getIntent();
        blendedCourseModel = intent.getParcelableExtra("blended_course_model");
        if (blendedCourseModel != null) {
            index = intent.getIntExtra("index", -1);
            thereIsData = true;
            thumbnailUrl = blendedCourseModel.getThumbnailUrl();
            btnHapus.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(blendedCourseModel.getThumbnailUrl())
                    .into(imgThumbnail);
            edtJudul.setText(blendedCourseModel.getTitle());
            edtLinkGDrive.setText(blendedCourseModel.getgDriveUrl());
            edtHargaKelas.setText(blendedCourseModel.getHarga());
            edtDeskripsi.setText(blendedCourseModel.getDescription());
            blendedCourseDocId = blendedCourseModel.getDocumentId();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_PERM_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_PICK_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (pd.isShowing())
            pd.dismiss();
    }

    private void loadTags() {
        final List<String> tagList = new ArrayList<>();
        final List<TagModel> tagModels = new ArrayList<>();

        tagList.add("Tag");
        final ArrayAdapter<String> spnArrayAdapterTag = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, tagList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                return view;
            }
        };

        spnArrayAdapterTag.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spnTag.setAdapter(spnArrayAdapterTag);

        spnTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != 0) {
                    tagCourse = tagModels.get(position - 1).getTag();
                    tagCourseId = tagModels.get(position - 1).getTagid();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        CollectionReference tagRef = db.collection("Tags");
        tagRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        tagList.clear();
                        tagList.add("Tag");
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            TagModel tagModel = documentSnapshot.toObject(TagModel.class);
                            tagModel.setTagid(documentSnapshot.getId());

                            tagModels.add(new TagModel(tagModel.getTag(), tagModel.getTagid()));
                            tagList.add(tagModel.getTag());
                        }
                        spnArrayAdapterTag.notifyDataSetChanged();

                        if (thereIsData) {
                            String tagId = blendedCourseModel.getTagId();
                            for (int i = 0; i < tagModels.size(); i++) {
                                if (tagModels.get(i).getTagid().equals(tagId)) {
                                    spnTag.setSelection(i + 1);
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Add Video Blended");
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

        imgThumbnail = findViewById(R.id.img_op_add_blended_thumbnail);
        clAddPhoto = findViewById(R.id.cl_op_add_blended_course_add_photo);
        clAddVideo = findViewById(R.id.cl_op_add_blended_video_materi);
        clAddQuiz = findViewById(R.id.cl_op_add_blended_soal_tes);
        btnHapus = findViewById(R.id.btn_op_add_blended_hapus);
        btnSimpan = findViewById(R.id.btn_op_add_blended_simpan);
        edtJudul = findViewById(R.id.edt_op_add_blended_judul);
        edtDeskripsi = findViewById(R.id.edt_op_add_blended_isi);
        edtLinkGDrive = findViewById(R.id.edt_op_add_blended_link);
        edtHargaKelas = findViewById(R.id.edt_op_add_blended_harga);
        spnTag = findViewById(R.id.spn_op_add_blended_tag);
    }

    private void onClick() {
        clAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPhotoFromGallery();
            }
        });
        clAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                addVideo = true;
                addQuiz = false;

                if (!thumbnailUrl.equals("")) {
                    thereIsData = true;
                }

                if (isDataComplete()) {
                    if (thereIsData) {
                        if (imageUri == null) {
                            edit();
                        } else {
                            uploadImageToFirebase(imageUri);
                        }
                    } else {
                        uploadImageToFirebase(imageUri);
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        clAddQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                addQuiz = true;
                addVideo = false;

                if (!thumbnailUrl.equals("")) {
                    thereIsData = true;
                }

                if (isDataComplete()) {
                    if (thereIsData) {
                        if (imageUri == null) {
                            edit();
                        } else {
                            uploadImageToFirebase(imageUri);
                        }
                    } else {
                        uploadImageToFirebase(imageUri);
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDataComplete()) {
                    showKonfirmasiDialog();
                } else {
                    Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHapusDialog();
            }
        });
    }

    private void getPhotoFromGallery() {
        if (ContextCompat.checkSelfPermission(OpAddBlendedCourseActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OpAddBlendedCourseActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERM_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PHOTO_PICK_REQUEST_CODE);
        }
    }

    private boolean isDataComplete() {
        if (thereIsData) {
            return !edtJudul.getText().equals("") &&
                    !edtDeskripsi.getText().equals("") &&
                    !edtHargaKelas.getText().equals("") &&
                    !tagCourseId.equals("") &&
                    !tagCourse.equals("");
        } else {
            return !edtJudul.getText().equals("") &&
                    !edtDeskripsi.getText().equals("") &&
                    !edtHargaKelas.getText().equals("") &&
                    !tagCourseId.equals("") &&
                    !tagCourse.equals("") &&
                    imageUri != null;
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        if (!thumbnailUrl.equals("")) {
            StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(thumbnailUrl);
            deleteRef.delete()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
        }


        final StorageReference ref = firebaseStorage.getReference().child("BlendedCourse/" + UUID.randomUUID().toString());
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        thumbnailUrl = uri.toString();
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
                                        pd.dismiss();
                                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void edit() {
        String title = edtJudul.getText().toString();
        String description = edtDeskripsi.getText().toString();
        String gDriveUrl = edtLinkGDrive.getText().toString();
        String tag = tagCourse;
        String tagId = tagCourseId;
        String harga = edtHargaKelas.getText().toString();

        try {
            dateCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            pd.dismiss();
            Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        final BlendedCourseModel blendedCourseModel = new BlendedCourseModel(title, description, tagId, tag, thumbnailUrl, gDriveUrl, harga, dateCreated);
        DocumentReference docRef = blendedCourseRef.document(blendedCourseDocId);
        docRef.set(blendedCourseModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent;
                        if (addVideo) {
                            intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseVideoActivity.class);
                            addVideo = false;
                            startActivity(intent);
                        } else if (addQuiz) {
                            intent = new Intent(OpAddBlendedCourseActivity.this, OpQuizActivity.class);
                            addQuiz = false;
                            startActivity(intent);
                        } else {
                            intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseActivity.class);
                            intent.putExtra("blended_course_model", blendedCourseModel);
                            intent.putExtra("index", index);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, UPDATE_REQUEST_CODE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tambah() {
        String title = edtJudul.getText().toString();
        String description = edtDeskripsi.getText().toString();
        String gDriveUrl = edtLinkGDrive.getText().toString();
        String tag = tagCourse;
        String tagId = tagCourseId;
        String harga = edtHargaKelas.getText().toString();

        try {
            dateCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            pd.dismiss();
            Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        final BlendedCourseModel blendedCourseModel = new BlendedCourseModel(title, description, tagId, tag, thumbnailUrl, gDriveUrl, harga, dateCreated);
        blendedCourseRef.add(blendedCourseModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        blendedCourseDocId = documentReference.getId();
                        Intent intent;
                        thereIsData = true;
                        if (addVideo) {
                            intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseVideoActivity.class);
                            addVideo = false;
                            startActivity(intent);
                        } else if (addQuiz) {
                            intent = new Intent(OpAddBlendedCourseActivity.this, OpQuizActivity.class);
                            addQuiz = false;
                            startActivity(intent);
                        } else {
                            intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseActivity.class);
                            intent.putExtra("blended_course_model", blendedCourseModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, ADD_REQUEST_CODE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hapus() {
        hapusVideoMateri();
    }

    private void hapusVideoMateri() {
        //check videonya dulu
        CollectionReference colRef = blendedCourseRef
                .document(blendedCourseDocId)
                .collection("VideoMateri");

        colRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            BlendedVideoModel blendedVideoModel = documentSnapshot.toObject(BlendedVideoModel.class);
                            final String urlVideo = blendedVideoModel.getVideoUrl();
                            StorageReference delVideo = firebaseStorage.getReferenceFromUrl(urlVideo);
                            delVideo.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            return;
                                        }
                                    });
                        }

                        hapusThumbnail();
                    }
                });
    }

    private void hapusThumbnail() {
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(blendedCourseModel.getThumbnailUrl());
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hapusDocBlendedCourseVideo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                    }
                });
    }

    private void hapusDocBlendedCourseVideo() {
        CollectionReference colRef = blendedCourseRef.document(blendedCourseDocId)
                .collection("VideoMateri");

        colRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (final QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = blendedCourseRef.document(blendedCourseDocId)
                                    .collection("VideoMateri")
                                    .document(documentSnapshot.getId());
                            docRef.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            return;
                                        }
                                    });
                        }
                        hapusQuiz();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        return;
                    }
                });
    }

    private void hapusQuiz() {
        CollectionReference colRef = blendedCourseRef.document(blendedCourseDocId)
                .collection("VideoMateri");

        colRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (final QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = blendedCourseRef.document(blendedCourseDocId)
                                    .collection("Quiz")
                                    .document(documentSnapshot.getId());
                            docRef.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            return;
                                        }
                                    });
                        }
                        hapusDocBlendedCourse();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        return;
                    }
                });
    }

    private void hapusDocBlendedCourse() {
        DocumentReference docRef = blendedCourseRef.document(blendedCourseDocId);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseActivity.class);
                        intent.putExtra("index", index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, DELETE_REQUEST_CODE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showHapusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghapus kelas ini?");
        builder.setTitle("Hapus Kelas Blended");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                pd.show();
                hapus();
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
        builder.setMessage("Apakah anda yakin ingin menyimpan kelas ini?");
        builder.setTitle("Simpan Kelas Blended");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                pd.show();
                if (thereIsData) {
                    if (imageUri != null) {
                        uploadImageToFirebase(imageUri);
                    } else {
                        edit();
                    }
                } else {
                    uploadImageToFirebase(imageUri);
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
