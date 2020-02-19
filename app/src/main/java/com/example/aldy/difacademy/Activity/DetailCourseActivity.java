package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.aldy.difacademy.Model.CourseModel;
import com.example.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.example.aldy.difacademy.Model.OngoingKelasOnlineModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import static com.example.aldy.difacademy.Activity.LoginActivity.SHARE_PREFS;
import static com.example.aldy.difacademy.Activity.LoginActivity.USERID_PREFS;
import static com.example.aldy.difacademy.Activity.MainActivity.JENIS_KELAS;

public class DetailCourseActivity extends AppCompatActivity {
    private ImageView imgThumbnail;
    private TextView tvJudul, tvTag, tvDetail, tvLampiran;
    private Button btnBeli;
    private ProgressDialog progressDialog;

    private CourseModel courseModel;

    private FirebaseFirestore firebaseFirestore;

    private String docId;

    private SharedPreferences sharedPreferences;

    private  boolean isPaid = false;
    private static final String TAG = "DetailCourseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_course);
        initView();
        setViewWithParcelable();
        getUserDocId();
    }

    private void initView() {
        imgThumbnail = findViewById(R.id.img_detail_course_thumbnail);
        tvJudul = findViewById(R.id.tv_detail_course_judul);
        tvTag = findViewById(R.id.tv_detail_course_tag);
        tvDetail = findViewById(R.id.tv_detail_course_detail);
        tvLampiran = findViewById(R.id.tv_detail_course_lampiran);
        btnBeli = findViewById(R.id.btn_detail_course_beli);
        sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);

    }

    private void setViewWithParcelable() {
        Intent intent = getIntent();
        courseModel = intent.getParcelableExtra("courseModel");
        Glide.with(this).load(courseModel.getThumbnailUrl()).into(imgThumbnail);
        tvJudul.setText(courseModel.getTitle());
        tvTag.setText(courseModel.getTag());
        tvDetail.setText(courseModel.getDescription());
    }

    private void onClick() {
        if (courseModel.getGoogleDrive() == null || courseModel.getGoogleDrive().equals("")) {
            tvLampiran.setVisibility(View.GONE);
        } else {
            tvLampiran.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(courseModel.getGoogleDrive()));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String urlNew = "http://" + courseModel.getGoogleDrive();
                        intent.setData(Uri.parse(urlNew));
                        startActivity(intent);
                    }
                }
            });
        }

        if (isPaid) {
            btnBeli.setText("LIHAT MATERI");
            btnBeli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (JENIS_KELAS.equalsIgnoreCase("online")) {
                        intent = new Intent(DetailCourseActivity.this, OnlineMateriActivity.class);

                    } else {
                        intent = new Intent(DetailCourseActivity.this, BlendedMateriActivity.class);

                    }
                    intent.putExtra("courseId", courseModel.getDocumentId());
                    startActivity(intent);
                }
            });
        } else {
            btnBeli.setText("BELI COURSE");
            btnBeli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailCourseActivity.this, PaymentActivity.class);
                    intent.putExtra("courseId", courseModel.getDocumentId());
                    startActivity(intent);
                }
            });
        }
    }

    private void getUserDocId() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d("ASUW", JENIS_KELAS);
        String userId = sharedPreferences.getString(USERID_PREFS, "");
        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference userRef = firebaseFirestore.collection("User");
        userRef
                .whereEqualTo("userId", userId)
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            docId = queryDocumentSnapshot.getId();
                        }
                        if (JENIS_KELAS.equalsIgnoreCase("online")) {
                            checkOngoingOnline(docId);
                        } else {
                            checkOngoingBlended(docId);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void checkOngoingOnline(final String docId) {
        CollectionReference onGoingRef = firebaseFirestore
                .collection("User")
                .document(docId)
                .collection("OngoingOnlineCourse");
        onGoingRef
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            OngoingKelasOnlineModel ongoingKelasOnlineModel = queryDocumentSnapshot.toObject(OngoingKelasOnlineModel.class);
                            if (ongoingKelasOnlineModel.getKelasOnlineId().equals(courseModel.getDocumentId())) {
                                isPaid = true;
                                break;
                            }
                        }
                        onClick();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void checkOngoingBlended(String docId) {
        CollectionReference onGoingRef = firebaseFirestore
                .collection("User")
                .document(docId)
                .collection("OngoingBlendedCourse");
        onGoingRef
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            OngoingKelasBlendedModel ongoingKelasBlendedModel = queryDocumentSnapshot.toObject(OngoingKelasBlendedModel.class);
                            if (ongoingKelasBlendedModel.getKelasBlendedId().equals(courseModel.getDocumentId())) {
                                isPaid = true;
                                break;
                            }
                        }
                        onClick();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }
}
