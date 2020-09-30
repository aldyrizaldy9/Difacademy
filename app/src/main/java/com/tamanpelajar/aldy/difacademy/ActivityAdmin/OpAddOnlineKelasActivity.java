package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
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
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.KelasOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.MateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.TagModel;
import com.tamanpelajar.aldy.difacademy.Model.VideoOnlineModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.PHOTO_PICK_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.UPDATE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpOnlineKelasActivity.isKelasOnlineChanged;

public class OpAddOnlineKelasActivity extends AppCompatActivity {
    public static String kelasOnlineDocId = "";

    private TextView tvNavbar;
    private ConstraintLayout clBack, clHapus;
    private ImageView imgBack, imgHapus;

    private ImageView imgThumbnail;
    private Button btnAddMateri, btnSimpan;
    private EditText edtJudul, edtDeskripsi, edtLinkGDrive;
    private Spinner spnTag;
    private ConstraintLayout clAddPhoto;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference refKelasOnline = db.collection(CommonMethod.refKelasOnline);
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private String tagCourse = "";
    private String tagCourseId = "";
    private boolean thereIsData = false;
    private boolean addMateri = false;
    private boolean dataHasChanged;

    private KelasOnlineModel kelasModel, oldKelasModel;
    private Uri imageUri;
    private String thumbnailUrl = "";

    private int index;
    private long dateCreated = 0;
    private ProgressDialog pd;

    private ArrayList<String> listOnlineVideoUrl;
    private ArrayList<String> listOnlineMateriThumbnailUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_online_kelas);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        firebaseStorage.setMaxUploadRetryTimeMillis(60000);
        listOnlineVideoUrl = new ArrayList<>();
        listOnlineMateriThumbnailUrl = new ArrayList<>();

        initView();
        onClick();
        checkIntent();
        loadTags();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (thereIsData && dataHasChanged) {
            isKelasOnlineChanged = true;
        }
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Kelas Online");
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

        imgThumbnail = findViewById(R.id.img_op_add_online_course_thumbnail);
        btnAddMateri = findViewById(R.id.btn_op_add_online_course_add_materi);
        btnSimpan = findViewById(R.id.btn_op_add_online_course_simpan);
        edtJudul = findViewById(R.id.edt_op_add_online_course_judul);
        edtDeskripsi = findViewById(R.id.edt_op_add_online_course_deskripsi);
        edtLinkGDrive = findViewById(R.id.edt_op_add_online_course_link);
        spnTag = findViewById(R.id.spn_op_add_online_course_tag);
        clAddPhoto = findViewById(R.id.cl_op_add_online_course_add_photo);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        kelasModel = intent.getParcelableExtra(CommonMethod.intentKelasOnlineModel);
        if (kelasModel != null) {
            oldKelasModel = kelasModel;
            index = intent.getIntExtra(CommonMethod.intentIndex, -1);
            thereIsData = true;
            thumbnailUrl = kelasModel.getThumbnailUrl();
            clHapus.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(thumbnailUrl)
                    .into(imgThumbnail);
            edtJudul.setText(kelasModel.getTitle());
            edtDeskripsi.setText(kelasModel.getDescription());
            edtLinkGDrive.setText(kelasModel.getGoogleDrive());
            kelasOnlineDocId = kelasModel.getDocumentId();
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

        CollectionReference tagRef = db.collection(CommonMethod.refTags);
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
                            String tagId = kelasModel.getTagId();
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
                        if (title.equals(oldKelasModel.getTitle()) &&
                                description.equals(oldKelasModel.getDescription()) &&
                                googleDrive.equals(oldKelasModel.getGoogleDrive()) &&
                                tag.equals(oldKelasModel.getTag()) &&
                                tagId.equals(oldKelasModel.getTagId()) &&
                                thumbnailUrl.equals(oldKelasModel.getThumbnailUrl()) &&
                                imageUri == null) {
                            Intent intent = new Intent(OpAddOnlineKelasActivity.this, OpOnlineMateriActivity.class);
                            startActivity(intent);
                        } else {
                            showSimpanDialog();
                        }
                    } else {
                        showSimpanDialog();
                    }
                } else {
                    Toast.makeText(OpAddOnlineKelasActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        clHapus.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(OpAddOnlineKelasActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
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

        KelasOnlineModel model = new KelasOnlineModel(title, description, thumbnailUrl, googleDrive, tagId, tag, dateCreated);

        if (thereIsData) {
            editKelas(model);
        } else {
            tambahKelas(model);
        }
    }

    private void editKelas(final KelasOnlineModel model) {
        DocumentReference docRef = refKelasOnline.document(kelasOnlineDocId);
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
                                Intent intent = new Intent(OpAddOnlineKelasActivity.this, OpOnlineMateriActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(OpAddOnlineKelasActivity.this, OpOnlineKelasActivity.class);
                                intent.putExtra(CommonMethod.intentKelasOnlineModel, model);
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
                        Toast.makeText(OpAddOnlineKelasActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tambahKelas(final KelasOnlineModel model) {
        refKelasOnline.add(model)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        kelasOnlineDocId = documentReference.getId();
                        if (addMateri) {
                            thereIsData = true;
                            clHapus.setVisibility(View.VISIBLE);
                            imageUri = null;
                            oldKelasModel = model;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddOnlineKelasActivity.this, OpOnlineMateriActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OpAddOnlineKelasActivity.this, OpOnlineKelasActivity.class);
                            intent.putExtra(CommonMethod.intentKelasOnlineModel, model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, ADD_REQUEST_CODE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddOnlineKelasActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPhotoToFirebase() {
        final StorageReference ref = firebaseStorage.getReference().child(CommonMethod.storageOnlineKelas + UUID.randomUUID().toString());
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
                        Toast.makeText(OpAddOnlineKelasActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deletePhotoInFirebase(final KelasOnlineModel model) {
        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(oldKelasModel.getThumbnailUrl());
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (addMateri) {
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddOnlineKelasActivity.this, OpOnlineMateriActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OpAddOnlineKelasActivity.this, OpOnlineKelasActivity.class);
                            intent.putExtra(CommonMethod.intentKelasOnlineModel, model);
                            intent.putExtra(CommonMethod.intentIndex, index);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, UPDATE_REQUEST_CODE);
                        }
                    }
                });
    }

    private void showSimpanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menyimpan kelas ini?");
        builder.setTitle("Simpan Kelas Online");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!CommonMethod.isInternetAvailable(OpAddOnlineKelasActivity.this)) {
                    return;
                }

                dateCreated = CommonMethod.getTimeStamp();

                dataHasChanged = true;
                pd.show();
                if (imageUri != null) {
                    uploadPhotoToFirebase();
                } else {
                    simpanKelas();
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
        builder.setTitle("Hapus Kelas Online");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!CommonMethod.isInternetAvailable(OpAddOnlineKelasActivity.this)) {
                    return;
                }

                pd.show();
                hapusKelas();
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
        /**
         * ambil link online video di storage
         * hapus online video document
         * hapus online video collection
         * hapus online video storage
         * hapus online soal document
         * hapus online soal collection
         * ambil link online materi thumbnail
         * hapus online materi document
         * hapus online materi collection
         * hapus online materi thumbnail storage
         * ambil link online course thumbnail
         * hapus online course document
         * hapus online course thumbnail storage
         */

        getListVideoUrl();
    }

    private void getListVideoUrl() {
        final CollectionReference ref1 = refKelasOnline
                .document(kelasOnlineDocId)
                .collection(CommonMethod.refMateriOnline);

        ref1.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                MateriOnlineModel model = documentSnapshot.toObject(MateriOnlineModel.class);
                                model.setDocumentId(documentSnapshot.getId());

                                CollectionReference ref2 = ref1.document(model.getDocumentId())
                                        .collection(CommonMethod.refVideoOnline);

                                getListVideoUrl2(ref2, documentSnapshot.getId());
                            }
                        } else {
                            hapusOnlineCourseDoc();
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
                            VideoOnlineModel model = documentSnapshot.toObject(VideoOnlineModel.class);
                            listOnlineVideoUrl.add(model.getVideoUrl());
                        }
                        hapusOnlineVideoDoc(docRef);
                    }
                });
    }

    private void hapusOnlineVideoDoc(final String docRef) {
        final CollectionReference ref = refKelasOnline
                .document(kelasOnlineDocId)
                .collection(CommonMethod.refMateriOnline)
                .document(docRef)
                .collection(CommonMethod.refVideoOnline);

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = ref.document(documentSnapshot.getId());
                            docRef.delete();
                        }
                        hapusOnlineVideoStorage(docRef);
                    }
                });
    }

    private void hapusOnlineVideoStorage(String docRef) {
        for (String url : listOnlineVideoUrl) {
            StorageReference ref = firebaseStorage.getReferenceFromUrl(url);
            ref.delete();
        }
        hapusOnlineSoalDoc(docRef);
    }

    private void hapusOnlineSoalDoc(final String docRef) {
        final CollectionReference ref = refKelasOnline
                .document(kelasOnlineDocId)
                .collection(CommonMethod.refMateriOnline)
                .document(docRef)
                .collection(CommonMethod.refSoalOnline);

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
        CollectionReference ref = refKelasOnline.document(kelasOnlineDocId)
                .collection(CommonMethod.refMateriOnline);

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            MateriOnlineModel model = documentSnapshot.toObject(MateriOnlineModel.class);
                            listOnlineMateriThumbnailUrl.add(model.getThumbnailUrl());
                        }
                        hapusOnlineMateriDoc();
                    }
                });
    }

    private void hapusOnlineMateriDoc() {
        final CollectionReference ref = refKelasOnline
                .document(kelasOnlineDocId)
                .collection(CommonMethod.refMateriOnline);

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = ref.document(documentSnapshot.getId());
                            docRef.delete();
                        }
                        hapusOnlineMateriStorage();
                    }
                });
    }

    private void hapusOnlineMateriStorage() {
        for (String url : listOnlineMateriThumbnailUrl) {
            StorageReference ref = firebaseStorage.getReferenceFromUrl(url);
            ref.delete();
        }
        hapusOnlineCourseDoc();
    }

    private void hapusOnlineCourseDoc() {
        DocumentReference ref = refKelasOnline.document(kelasOnlineDocId);
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hapusOnlineCourseStorage();
                    }
                });
    }

    private void hapusOnlineCourseStorage() {
        StorageReference ref = firebaseStorage.getReferenceFromUrl(thumbnailUrl);
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(OpAddOnlineKelasActivity.this, OpOnlineKelasActivity.class);
                        intent.putExtra(CommonMethod.intentIndex, index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, DELETE_REQUEST_CODE);
                    }
                });
    }
}
