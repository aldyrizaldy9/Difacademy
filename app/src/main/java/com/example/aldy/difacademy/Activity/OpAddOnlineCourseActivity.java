package com.example.aldy.difacademy.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.aldy.difacademy.Adapter.OpOnlineMateriAdapter;
import com.example.aldy.difacademy.Model.OnlineCourseModel;
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
import static com.example.aldy.difacademy.Activity.OpMainActivity.PHOTO_PICK_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpAddOnlineCourseActivity extends AppCompatActivity {

    private static final String TAG = "ganteng";

    public static String onlineCourseDocId = "";

    TextView tvNavbar;
    ConstraintLayout clBack;
    ImageView imgBack;

    ImageView imgThumbnail;
    Button btnAddMateri, btnHapus, btnSimpan;
    EditText edtJudul, edtDeskripsi, edtLinkGDrive, edtHarga;
    Spinner spnTag;
    ConstraintLayout clAddPhoto;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference onlineCourseRef = db.collection("OnlineCourse");
    String tagCourse = "";
    String tagCourseId = "";
    boolean thereIsData = false;
    boolean addMateri = false;

    OnlineCourseModel onlineCourseModel, oldOnlineCourseModel;
    Uri imageUri;
    String thumbnailUrl = "";
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    int index;
    long dateCreated = 0;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_online_course);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        firebaseStorage.setMaxUploadRetryTimeMillis(60000);

        initView();
        onClick();
        checkIntent();
        loadTags();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Tambah Kelas Online");
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

        imgThumbnail = findViewById(R.id.img_op_add_online_course_thumbnail);
        btnAddMateri = findViewById(R.id.btn_op_add_online_course_add_materi);
        btnHapus = findViewById(R.id.btn_op_add_online_course_hapus);
        btnSimpan = findViewById(R.id.btn_op_add_online_course_simpan);
        edtJudul = findViewById(R.id.edt_op_add_online_course_judul);
        edtDeskripsi = findViewById(R.id.edt_op_add_online_course_deskripsi);
        edtLinkGDrive = findViewById(R.id.edt_op_add_online_course_link);
        edtHarga = findViewById(R.id.edt_op_add_online_course_harga);
        spnTag = findViewById(R.id.spn_op_add_online_course_tag);
        clAddPhoto = findViewById(R.id.cl_op_add_online_course_add_photo);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        onlineCourseModel = intent.getParcelableExtra("online_course_model");
        if (onlineCourseModel != null) {

            Log.d(TAG, "checkIntent: intent ada");

            oldOnlineCourseModel = onlineCourseModel;
            index = intent.getIntExtra("index", -1);
            thereIsData = true;
            thumbnailUrl = onlineCourseModel.getThumbnailUrl();
            btnHapus.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(thumbnailUrl)
                    .into(imgThumbnail);
            edtJudul.setText(onlineCourseModel.getTitle());
            edtDeskripsi.setText(onlineCourseModel.getDescription());
            edtLinkGDrive.setText(onlineCourseModel.getGoogleDrive());
            edtHarga.setText(onlineCourseModel.getHarga());
            onlineCourseDocId = onlineCourseModel.getDocumentId();
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
                            String tagId = onlineCourseModel.getTagId();
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
                Log.d(TAG, "onClick: add materi clicked");
                if (isDataComplete()) {
                    Log.d(TAG, "onClick: data complete");
                    addMateri = true;
                    String title = edtJudul.getText().toString();
                    String description = edtDeskripsi.getText().toString();
                    String googleDrive = edtLinkGDrive.getText().toString();
                    String tag = tagCourse;
                    String tagId = tagCourseId;
                    String harga = edtHarga.getText().toString();

                    if (thereIsData) {
                        Log.d(TAG, "onClick: there is data");
                        if (title.equals(oldOnlineCourseModel.getTitle()) &&
                                description.equals(oldOnlineCourseModel.getDescription()) &&
                                googleDrive.equals(oldOnlineCourseModel.getGoogleDrive()) &&
                                tag.equals(oldOnlineCourseModel.getTag()) &&
                                tagId.equals(oldOnlineCourseModel.getTagId()) &&
                                harga.equals(oldOnlineCourseModel.getHarga()) &&
                                thumbnailUrl.equals(oldOnlineCourseModel.getThumbnailUrl()) &&
                                imageUri == null) {
                            Log.d(TAG, "onClick: gaada yang berubah");
                            Intent intent = new Intent(OpAddOnlineCourseActivity.this, OpOnlineMateriActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "onClick: ada yang berubah");
                            showSimpanDialog();
                        }
                    } else {
                        Log.d(TAG, "onClick: there is no data");
                        showSimpanDialog();
                    }
                } else {
                    Toast.makeText(OpAddOnlineCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                hapusKelas();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMateri = false;
                if (isDataComplete()) {
                    showSimpanDialog();
                } else {
                    Toast.makeText(OpAddOnlineCourseActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
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
                    !edtHarga.getText().toString().equals("") &&
                    !tagCourseId.equals("") &&
                    !tagCourse.equals("");
        } else {
            return !edtJudul.getText().toString().equals("") &&
                    !edtDeskripsi.getText().toString().equals("") &&
                    !edtHarga.getText().toString().equals("") &&
                    !tagCourseId.equals("") &&
                    !tagCourse.equals("") &&
                    imageUri != null;
        }
    }

    private void hapusKelas() {

    }

    private void simpanKelas() {
        String title = edtJudul.getText().toString();
        String description = edtDeskripsi.getText().toString();
        String googleDrive = edtLinkGDrive.getText().toString();
        String tag = tagCourse;
        String tagId = tagCourseId;
        String harga = edtHarga.getText().toString();

        Log.d(TAG, "simpanKelas: berjalan");
        Log.d(TAG, "simpanKelas: title : " + title);
        Log.d(TAG, "simpanKelas: desc : " + description);
        Log.d(TAG, "simpanKelas: tag : " + tag);
        Log.d(TAG, "simpanKelas: tagId : " + tagId);
        Log.d(TAG, "simpanKelas: harga : " + harga);

        try {
            dateCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            pd.dismiss();
            Toast.makeText(OpAddOnlineCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        OnlineCourseModel model = new OnlineCourseModel(title, description, thumbnailUrl, googleDrive, tagId, tag, harga, dateCreated);

        if (thereIsData) {
            Log.d(TAG, "simpanKelas: there is data");
            editKelas(model);
        } else {
            Log.d(TAG, "simpanKelas: there is no data");
            tambahKelas(model);
        }
    }

    private void editKelas(final OnlineCourseModel model) {
        Log.d(TAG, "editKelas: berjalan");

        DocumentReference docRef = onlineCourseRef.document(onlineCourseDocId);
        docRef.set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: berhasil set ke firestore");
                        if (imageUri != null){
                            deletePhotoInFirebase(model);
                        } else {
                            if (addMateri) {
                                Log.d(TAG, "onSuccess: addmateri");

                                imageUri = null;
                                pd.dismiss();
                                Intent intent = new Intent(OpAddOnlineCourseActivity.this, OpOnlineMateriActivity.class);
                                startActivity(intent);
                            } else {
                                Log.d(TAG, "onSuccess: tidak addmateri");
                                Intent intent = new Intent(OpAddOnlineCourseActivity.this, OpOnlineCourseActivity.class);
                                intent.putExtra("online_course_model", model);
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
                        Toast.makeText(OpAddOnlineCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tambahKelas(final OnlineCourseModel model) {
        Log.d(TAG, "tambahKelas: berjalan");

        onlineCourseRef.add(model)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: berhasil add ke firestore");

                        onlineCourseDocId = documentReference.getId();
                        if (addMateri) {
                            Log.d(TAG, "onSuccess: addmateri");

                            thereIsData = true;
                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddOnlineCourseActivity.this, OpOnlineMateriActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "onSuccess: tidak addmateri");

                            Intent intent = new Intent(OpAddOnlineCourseActivity.this, OpOnlineCourseActivity.class);
                            intent.putExtra("online_course_model", model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, ADD_REQUEST_CODE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddOnlineCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPhotoToFirebase() {
        Log.d(TAG, "uploadPhotoToFirebase: berjalan");

        final StorageReference ref = firebaseStorage.getReference().child("OnlineCourse/" + UUID.randomUUID().toString());
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        thumbnailUrl = uri.toString();
                                        Log.d(TAG, "onSuccess: thumbnail url yang baru : " + thumbnailUrl);
                                        simpanKelas();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddOnlineCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deletePhotoInFirebase(final OnlineCourseModel model) {
        Log.d(TAG, "deletePhotoInFirebase: berjalan");

        StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(oldOnlineCourseModel.getThumbnailUrl());
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: berhasil hapus foto storage");
                        if (addMateri) {
                            Log.d(TAG, "onSuccess: addmateri");

                            imageUri = null;
                            pd.dismiss();
                            Intent intent = new Intent(OpAddOnlineCourseActivity.this, OpOnlineMateriActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "onSuccess: tidak addmateri");
                            Intent intent = new Intent(OpAddOnlineCourseActivity.this, OpOnlineCourseActivity.class);
                            intent.putExtra("online_course_model", model);
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
        builder.setTitle("Simpan Kelas Online");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d(TAG, "onClick: show simpan dialog");

                if (!isNetworkConnected()) {
                    Toast.makeText(OpAddOnlineCourseActivity.this, "Tidak ada koneksi intenet!", Toast.LENGTH_SHORT).show();
                } else {
                    pd.show();
                    if (imageUri != null) {
                        Log.d(TAG, "onClick: ada foto yang dipilih");
                        uploadPhotoToFirebase();
                    } else {
                        Log.d(TAG, "onClick: tidak ada foto yang dipilih");
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
}
