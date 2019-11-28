package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aldy.difacademy.Adapter.NewsAdapter;
import com.example.aldy.difacademy.Model.BlendedCourseModel;
import com.example.aldy.difacademy.Model.NewsModel;
import com.example.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout clSettings, clOngoing;
    private ImageView imgKelasGratis, imgKelasOnline, imgKelasCampuran, imgOngoing;
    private Button btnBeritaLainnya;
    private TextView tvDiikutiSemua, tvJudulOngoing, tvTagOngoing;
    private RecyclerView rvMainBerita;
    private NewsAdapter newsAdapter;
    private ArrayList<NewsModel> newsModels;
    private FirebaseFirestore firebaseFirestore;
    private BlendedCourseModel blendedCourseModel;
    private ProgressDialog progressDialog;

    private boolean doubleBackToExitPressedOnce = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        onClick();
        setRecyclerView();
        getUserDocId();
        loadNews();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void initView() {
        clSettings = findViewById(R.id.cl_main_settings);
        clOngoing = findViewById(R.id.cl_main_ongoing_container);
        imgKelasGratis = findViewById(R.id.img_main_kelas_gratis);
        imgKelasOnline = findViewById(R.id.img_main_kelas_online);
        imgKelasCampuran = findViewById(R.id.img_main_kelas_campuran);
        imgOngoing = findViewById(R.id.img_main_ongoing_thumbnail);
        btnBeritaLainnya = findViewById(R.id.btn_main_berita_lainnya);
        tvDiikutiSemua = findViewById(R.id.tv_main_diikuti_semua);
        tvJudulOngoing = findViewById(R.id.tv_main_ongoing_judul);
        tvTagOngoing = findViewById(R.id.tv_main_ongoing_tag);
        rvMainBerita = findViewById(R.id.rv_main_berita);
        progressDialog = new ProgressDialog(this);

        rvMainBerita.setNestedScrollingEnabled(false);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setRecyclerView() {
        newsModels = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, newsModels);
        rvMainBerita.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvMainBerita.setAdapter(newsAdapter);
    }

    private void onClick() {
        clSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        clOngoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListVideoCourseActivity.class);
                intent.putExtra("BLENDED_COURSE_ID", blendedCourseModel.getDocumentId());
                startActivity(intent);
            }
        });
        imgKelasGratis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListFreeCourseActivity.class);
                startActivity(intent);
            }
        });
        imgKelasOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Fitur ini belum tersedia, stay tune :)", Toast.LENGTH_SHORT).show();
            }
        });
        imgKelasCampuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListBlendedCourseActivity.class);
                startActivity(intent);
            }
        });
        btnBeritaLainnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListNewsActivity.class);
                startActivity(intent);
            }
        });
        tvDiikutiSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OngoingCourseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getUserDocId() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference userRef = firebaseFirestore.collection("User");
        if (user != null) {
            userRef
                    .whereEqualTo("userId", user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                getOngoingCourseId(queryDocumentSnapshot.getId());
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
    }

    private void getOngoingCourseId(String userDocId) {
        CollectionReference ongoingRef = firebaseFirestore
                .collection("User")
                .document(userDocId)
                .collection("OngoingBlendedCourse");
        ongoingRef
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            OngoingKelasBlendedModel ongoingKelasBlendedModel = queryDocumentSnapshot.toObject(OngoingKelasBlendedModel.class);
                            getOngoingCourse(ongoingKelasBlendedModel.getKelasBlendedId());
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

    private void getOngoingCourse(String courseId) {
        DocumentReference ongoingRef = firebaseFirestore
                .collection("BlendedCourse")
                .document(courseId);
        ongoingRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        blendedCourseModel = documentSnapshot.toObject(BlendedCourseModel.class);
                        if (blendedCourseModel != null) {
                            blendedCourseModel.setDocumentId(documentSnapshot.getId());

                            Glide.with(MainActivity.this).load(blendedCourseModel.getThumbnailUrl()).into(imgOngoing);
                            tvJudulOngoing.setText(blendedCourseModel.getTitle());
                            tvTagOngoing.setText(blendedCourseModel.getTag());

                            progressDialog.dismiss();
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

    private void loadNews() {
        CollectionReference newsRef = firebaseFirestore.collection("News");
        newsRef.orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            NewsModel newsModel = queryDocumentSnapshot.toObject(NewsModel.class);
                            newsModel.setNewsId(queryDocumentSnapshot.getId());

                            newsModels.add(newsModel);
                        }
                        newsAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

}
