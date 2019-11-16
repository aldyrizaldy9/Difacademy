package com.example.aldy.difacademy.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import com.bumptech.glide.Glide;
import com.example.aldy.difacademy.Model.BlendedCourseModel;
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
    boolean wannaAddVideoMateri = false;
    boolean wannaAddSoal = false;
    BlendedCourseModel blendedCourseModelIntent;

    ProgressDialog pd;
    Uri imageUri;

    String thumbnailUrl = "";

    public static String blendedCourseDocId = "";

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_blended_course);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);


        initView();
        onClick();
        loadTags();

        Intent intent = getIntent();

        int index = intent.getIntExtra("index", -1);
        if (index != -1) {
            this.index = index;
        }

        blendedCourseModelIntent = intent.getParcelableExtra("blended_course_model");
        if (blendedCourseModelIntent != null) {
            thereIsData = true;
            thumbnailUrl = blendedCourseModelIntent.getThumbnailUrl();
            btnHapus.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(blendedCourseModelIntent.getThumbnailUrl())
                    .into(imgThumbnail);
            edtJudul.setText(blendedCourseModelIntent.getTitle());
            edtLinkGDrive.setText(blendedCourseModelIntent.getgDriveUrl());
            edtHargaKelas.setText(blendedCourseModelIntent.getHarga());
            edtDeskripsi.setText(blendedCourseModelIntent.getDescription());
            blendedCourseDocId = blendedCourseModelIntent.getDocumentId();
        } else {
            blendedCourseDocId = "";
        }
    }

    @Override
    protected void onPause() {
        pd.dismiss();
        super.onPause();
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
                showKonfirmasiDialog();
            }
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        wannaAddSoal = false;
        wannaAddVideoMateri = false;
    }

    private void onClick() {
        clAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
        clAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wannaAddVideoMateri = true;
                if (thereIsData) {
                    if (edtJudul.getText().equals("") ||
                            edtDeskripsi.getText().equals("") ||
                            edtHargaKelas.getText().equals("") ||
                            tagCourseId.equals("") ||
                            tagCourse.equals("")) {
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                    } else {
                        showKonfirmasiDialog();
                    }
                } else {
                    if (imageUri == null ||
                            edtJudul.getText().equals("") ||
                            edtDeskripsi.getText().equals("") ||
                            edtHargaKelas.getText().equals("") ||
                            tagCourseId.equals("") ||
                            tagCourse.equals("")) {
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                    } else {
                        showKonfirmasiDialog();
                    }
                }
            }
        });
        clAddQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wannaAddSoal = true;
                if (thereIsData) {
                    if (edtJudul.getText().equals("") ||
                            edtDeskripsi.getText().equals("") ||
                            edtHargaKelas.getText().equals("") ||
                            tagCourseId.equals("") ||
                            tagCourse.equals("")) {
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                    } else {
                        showKonfirmasiDialog();
                    }
                } else {
                    if (imageUri == null ||
                            edtJudul.getText().equals("") ||
                            edtDeskripsi.getText().equals("") ||
                            edtHargaKelas.getText().equals("") ||
                            tagCourseId.equals("") ||
                            tagCourse.equals("")) {
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                    } else {
                        showKonfirmasiDialog();
                    }
                }
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thereIsData) {
                    if (edtJudul.getText().equals("") ||
                            edtDeskripsi.getText().equals("") ||
                            edtHargaKelas.getText().equals("") ||
                            tagCourseId.equals("") ||
                            tagCourse.equals("")) {
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                    } else {
                        showKonfirmasiDialog();
                    }
                } else {
                    if (imageUri == null ||
                            edtJudul.getText().equals("") ||
                            edtDeskripsi.getText().equals("") ||
                            edtHargaKelas.getText().equals("") ||
                            tagCourseId.equals("") ||
                            tagCourse.equals("")) {
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                    } else {
                        showKonfirmasiDialog();
                    }
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

    private void checkEditOrAddImage() {
        pd.show();

        if (thereIsData && imageUri == null) {
            addOrUpdateData();
        } else if (thereIsData && imageUri != null) {
            StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(blendedCourseModelIntent.getThumbnailUrl());
            deleteRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            uploadImageToFirebase(imageUri);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
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
                                        addOrUpdateData();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTags() {
        final List<String> tagList = new ArrayList<>();
        final List<TagModel> tagModels = new ArrayList<>();

        tagList.add("Tag");
        final ArrayAdapter<String> spnArrayAdapterTag = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, tagList) {
            @Override
            public boolean isEnabled(int position) {
                return true;
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
                            String tagId = blendedCourseModelIntent.getTagId();
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

    private void addOrUpdateData() {
        String title = edtJudul.getText().toString();
        String description = edtDeskripsi.getText().toString();
        String gDriveUrl = edtLinkGDrive.getText().toString();
        String tag = tagCourse;
        String tagId = tagCourseId;
        String harga = edtHargaKelas.getText().toString();

        long dataCreated = 0;
        try {
            dataCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            pd.dismiss();
            Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        if (dataCreated > 0) {
            final BlendedCourseModel blendedCourseModel = new BlendedCourseModel(title, description, tagId, tag, thumbnailUrl, gDriveUrl, harga, dataCreated);
            if (thereIsData) {
                DocumentReference docRef = blendedCourseRef.document(blendedCourseDocId);
                docRef.set(blendedCourseModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                if (wannaAddVideoMateri) {
                                    Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseVideoActivity.class);
                                    startActivity(intent);
                                } else if (wannaAddSoal) {
                                    Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpQuizActivity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseActivity.class);
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
            } else {
                blendedCourseRef.add(blendedCourseModel)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                pd.dismiss();
                                blendedCourseDocId = documentReference.getId();
                                if (wannaAddVideoMateri) {
                                    Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseVideoActivity.class);
                                    startActivity(intent);
                                } else if (wannaAddSoal) {
                                    Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpAddQuizActivity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseActivity.class);
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
        }
    }

    private void hapus() {
        //yang hapus ini belum hapus semua video materi dan semua soalnya
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(blendedCourseModelIntent.getThumbnailUrl());
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = blendedCourseRef.document(blendedCourseModelIntent.getDocumentId());
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
                checkEditOrAddImage();
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
