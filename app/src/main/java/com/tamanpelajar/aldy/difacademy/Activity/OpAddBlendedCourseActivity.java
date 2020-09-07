package com.tamanpelajar.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
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

import com.bumptech.glide.Glide;
import com.tamanpelajar.aldy.difacademy.Model.CourseModel;
import com.tamanpelajar.aldy.difacademy.Model.MateriModel;
import com.tamanpelajar.aldy.difacademy.Model.TagModel;
import com.tamanpelajar.aldy.difacademy.Model.VideoModel;
import com.tamanpelajar.aldy.difacademy.R;
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

import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.PHOTO_PICK_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpAddBlendedCourseActivity extends AppCompatActivity {

    public static String blendedCourseDocId = "";

    TextView tvNavbar;
    ConstraintLayout clBack;
    ImageView imgBack;

    ImageView imgThumbnail;
    Button btnAddMateri, btnHapus, btnSimpan;
    EditText edtJudul, edtDeskripsi, edtLinkGDrive;
    Spinner spnTag;
    ConstraintLayout clAddPhoto;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference blendedCourseRef = db.collection("BlendedCourse");
    String tagCourse = "";
    String tagCourseId = "";
    boolean thereIsData = false;
    boolean addMateri = false;

    CourseModel courseModel, oldCourseModel;
    Uri imageUri;
    String thumbnailUrl = "";
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    int index;
    long dateCreated = 0;
    ProgressDialog pd;

    ArrayList<String> listBlendedVideoUrl;
    ArrayList<String> listBlendedMateriThumbnailUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_blended_course);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        firebaseStorage.setMaxUploadRetryTimeMillis(60000);
        listBlendedVideoUrl = new ArrayList<>();
        listBlendedMateriThumbnailUrl = new ArrayList<>();

        initView();
        onClick();
        checkIntent();
        loadTags();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Detail Kelas Blended");
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

        imgThumbnail = findViewById(R.id.img_op_add_blended_course_thumbnail);
        btnAddMateri = findViewById(R.id.btn_op_add_blended_course_add_materi);
        btnHapus = findViewById(R.id.btn_op_add_blended_course_hapus);
        btnSimpan = findViewById(R.id.btn_op_add_blended_course_simpan);
        edtJudul = findViewById(R.id.edt_op_add_blended_course_judul);
        edtDeskripsi = findViewById(R.id.edt_op_add_blended_course_deskripsi);
        edtLinkGDrive = findViewById(R.id.edt_op_add_blended_course_link);
        spnTag = findViewById(R.id.spn_op_add_blended_course_tag);
        clAddPhoto = findViewById(R.id.cl_op_add_blended_course_add_photo);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        courseModel = intent.getParcelableExtra("blended_course_model");
        if (courseModel != null) {
            oldCourseModel = courseModel;
            index = intent.getIntExtra("index", -1);
            thereIsData = true;
            thumbnailUrl = courseModel.getThumbnailUrl();
            btnHapus.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(thumbnailUrl)
                    .into(imgThumbnail);
            edtJudul.setText(courseModel.getTitle());
            edtDeskripsi.setText(courseModel.getDescription());
            edtLinkGDrive.setText(courseModel.getGoogleDrive());
            blendedCourseDocId = courseModel.getDocumentId();
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
                            String tagId = courseModel.getTagId();
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

    private void onClick() {
        clAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });
        btnAddMateri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataComplete()) {
                    addMateri = true;
                    String title = edtJudul.getText().toString();
                    String description = edtDeskripsi.getText().toString();
                    String googleDrive = edtLinkGDrive.getText().toString();
                    String tag = tagCourse;
                    String tagId = tagCourseId;

                    if (thereIsData) {
                        if (title.equals(oldCourseModel.getTitle()) &&
                                description.equals(oldCourseModel.getDescription()) &&
                                googleDrive.equals(oldCourseModel.getGoogleDrive()) &&
                                tag.equals(oldCourseModel.getTag()) &&
                                tagId.equals(oldCourseModel.getTagId()) &&
                                thumbnailUrl.equals(oldCourseModel.getThumbnailUrl()) &&
                                imageUri == null) {
                            Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedMateriActivity.class);
                            startActivity(intent);
                        } else {
                            showSimpanDialog();
                        }
                    } else {
                        showSimpanDialog();
                    }
                } else {
                    Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHapusDialog();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMateri = false;
                if (isDataComplete()) {
                    showSimpanDialog();
                } else {
                    Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
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
            return !edtJudul.getText().toString().equals("") &&
                    !edtDeskripsi.getText().toString().equals("") &&
                    !tagCourseId.equals("") &&
                    !tagCourse.equals("");
        } else {
            return !edtJudul.getText().toString().equals("") &&
                    !edtDeskripsi.getText().toString().equals("") &&
                    !tagCourseId.equals("") &&
                    !tagCourse.equals("") &&
                    imageUri != null;
        }
    }

    private void simpanKelas() {
        String title = edtJudul.getText().toString();
        String description = edtDeskripsi.getText().toString();
        String googleDrive = edtLinkGDrive.getText().toString();
        String tag = tagCourse;
        String tagId = tagCourseId;

        try {
            dateCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            pd.dismiss();
            Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        CourseModel model = new CourseModel(title, description, thumbnailUrl, googleDrive, tagId, tag, dateCreated);

        if (thereIsData) {
            editKelas(model);
        } else {
            tambahKelas(model);
        }
    }

    private void editKelas(final CourseModel model) {
        DocumentReference docRef = blendedCourseRef.document(blendedCourseDocId);
        docRef.set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (imageUri != null) {
                            deletePhotoInFirebase(model);
                        } else {
                            if (addMateri) {
                                imageUri = null;
                                pd.dismiss();
                                Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedMateriActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseActivity.class);
                                intent.putExtra("blended_course_model", model);
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
                        Toast.makeText(OpAddBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tambahKelas(final CourseModel model) {
        blendedCourseRef.add(model)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        blendedCourseDocId = documentReference.getId();
                        if (addMateri) {
                            thereIsData = true;
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedMateriActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseActivity.class);
                            intent.putExtra("blended_course_model", model);
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

    private void uploadPhotoToFirebase() {
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
                                        simpanKelas();
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

    private void deletePhotoInFirebase(final CourseModel model) {
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(oldCourseModel.getThumbnailUrl());
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (addMateri) {
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedMateriActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseActivity.class);
                            intent.putExtra("blended_course_model", model);
                            intent.putExtra("index", index);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, UPDATE_REQUEST_CODE);
                        }
                    }
                });
    }

    private void showSimpanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menyimpan kelas ini?");
        builder.setTitle("Simpan Kelas Blended");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddBlendedCourseActivity.this, "Tidak ada koneksi intenet!", Toast.LENGTH_SHORT).show();
                } else {
                    pd.show();
                    if (imageUri != null) {
                        uploadPhotoToFirebase();
                    } else {
                        simpanKelas();
                    }
                }
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                addMateri = false;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showHapusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghapus kelas ini?");
        builder.setTitle("Hapus Kelas Blended");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddBlendedCourseActivity.this, "Tidak ada koneksi intenet!", Toast.LENGTH_SHORT).show();
                } else {
                    pd.show();
                    hapusKelas();
                }
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                addMateri = false;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void hapusKelas() {
        //ambil link blended video di storage
        //hapus blended video document
        //hapus blended video collection
        //hapus blended video storage
        //hapus blended soal document
        //hapus blended soal collection
        //ambil link blended materi thumbnail
        //hapus blended materi document
        //hapus blended materi collection
        //hapus blended materi thumbnail storage
        //ambil link blended course thumbnail
        //hapus blended course document
        //hapus blended course thumbnail storage
        getListVideoUrl();
    }

    private void getListVideoUrl() {
        final CollectionReference ref1 = blendedCourseRef
                .document(blendedCourseDocId)
                .collection("BlendedMateri");

        ref1.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0){
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                MateriModel model = documentSnapshot.toObject(MateriModel.class);
                                model.setDocumentId(documentSnapshot.getId());

                                CollectionReference ref2 = ref1.document(model.getDocumentId())
                                        .collection("BlendedVideo");

                                getListVideoUrl2(ref2, documentSnapshot.getId());
                            }
                        } else {
                            hapusBlendedCourseDoc();
                        }
                    }
                });
    }

    private void getListVideoUrl2(CollectionReference ref, final String docRef) {
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            VideoModel model = documentSnapshot.toObject(VideoModel.class);
                            listBlendedVideoUrl.add(model.getVideoUrl());
                        }
                        hapusBlendedVideoDoc(docRef);
                    }
                });
    }

    private void hapusBlendedVideoDoc(final String docRef) {
        final CollectionReference ref = blendedCourseRef
                .document(blendedCourseDocId)
                .collection("BlendedMateri")
                .document(docRef)
                .collection("BlendedVideo");

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = ref.document(documentSnapshot.getId());
                            docRef.delete();
                        }
                        hapusBlendedVideoStorage(docRef);
                    }
                });
    }

    private void hapusBlendedVideoStorage(String docRef) {
        for (String url : listBlendedVideoUrl) {
            StorageReference ref = firebaseStorage.getReferenceFromUrl(url);
            ref.delete();
        }
        hapusBlendedSoalDoc(docRef);
    }

    private void hapusBlendedSoalDoc(final String docRef) {
        final CollectionReference ref = blendedCourseRef
                .document(blendedCourseDocId)
                .collection("BlendedMateri")
                .document(docRef)
                .collection("BlendedSoal");

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef2 = ref.document(documentSnapshot.getId());
                            docRef2.delete();
                        }
                        getListThumbnailUrl();
                    }
                });
    }

    private void getListThumbnailUrl() {
        CollectionReference ref = blendedCourseRef.document(blendedCourseDocId)
                .collection("BlendedMateri");

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            MateriModel model = documentSnapshot.toObject(MateriModel.class);
                            listBlendedMateriThumbnailUrl.add(model.getThumbnailUrl());
                        }
                        hapusBlendedMateriDoc();
                    }
                });
    }

    private void hapusBlendedMateriDoc() {
        final CollectionReference ref = blendedCourseRef
                .document(blendedCourseDocId)
                .collection("BlendedMateri");

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = ref.document(documentSnapshot.getId());
                            docRef.delete();
                        }
                        hapusBlendedMateriStorage();
                    }
                });
    }

    private void hapusBlendedMateriStorage() {
        for (String url : listBlendedMateriThumbnailUrl) {
            StorageReference ref = firebaseStorage.getReferenceFromUrl(url);
            ref.delete();
        }
        hapusBlendedCourseDoc();
    }

    private void hapusBlendedCourseDoc() {
        DocumentReference ref = blendedCourseRef.document(blendedCourseDocId);
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hapusBlendedCourseStorage();
                    }
                });
    }

    private void hapusBlendedCourseStorage() {
        StorageReference ref = firebaseStorage.getReferenceFromUrl(thumbnailUrl);
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(OpAddBlendedCourseActivity.this, OpBlendedCourseActivity.class);
                        intent.putExtra("index", index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, DELETE_REQUEST_CODE);
                    }
                });
    }
}