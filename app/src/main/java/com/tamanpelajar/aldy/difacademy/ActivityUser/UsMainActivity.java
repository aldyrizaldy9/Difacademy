package com.tamanpelajar.aldy.difacademy.ActivityUser;

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
import com.tamanpelajar.aldy.difacademy.Adapter.UsNewsAdapter;
import com.tamanpelajar.aldy.difacademy.Model.NewsModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class UsMainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ConstraintLayout clSettings, clOngoing;
    private ImageView imgKelasGratis, imgKelasOnline, imgKelasCampuran, imgOngoing, imgBanner;
    private Button btnBeritaLainnya;
    private TextView tvDiikutiSemua, tvJudulOngoing, tvTagOngoing;
    private RecyclerView rvMainBerita;
    private UsNewsAdapter usNewsAdapter;
    private ArrayList<NewsModel> newsModels;
    private FirebaseFirestore firebaseFirestore;
//    private MateriModel materiModel;
    private String userDocId;
//    private OngoingMateriModel ongoingOnlineMateri;
//    private OngoingMateriModel ongoingBlendedMateri;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_main);
        initView();
        setBanner();
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
        clOngoing = findViewById(R.id.cl_main_ongoing_class);
        imgKelasGratis = findViewById(R.id.img_main_kelas_gratis);
        imgKelasOnline = findViewById(R.id.img_main_kelas_online);
        imgKelasCampuran = findViewById(R.id.img_main_kelas_campuran);
        imgOngoing = findViewById(R.id.img_main_ongoing_thumbnail);
        imgOngoing.setClipToOutline(true);
        imgBanner = findViewById(R.id.img_main_banner);
        btnBeritaLainnya = findViewById(R.id.btn_main_berita_lainnya);
        tvDiikutiSemua = findViewById(R.id.tv_main_diikuti_semua);
        tvJudulOngoing = findViewById(R.id.tv_main_ongoing_judul);
        tvTagOngoing = findViewById(R.id.tv_main_ongoing_tag);
        rvMainBerita = findViewById(R.id.rv_main_berita);

        rvMainBerita.setNestedScrollingEnabled(false);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setBanner() {
        DocumentReference docRef = firebaseFirestore.collection("BannerPhotoUrl").document("bannerphotourl");
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            String url = documentSnapshot.getString("url");
                            Glide.with(UsMainActivity.this)
                                    .load(url)
                                    .into(imgBanner);
                        }
                    }
                });
    }

    private void setRecyclerView() {
        newsModels = new ArrayList<>();
        usNewsAdapter = new UsNewsAdapter(this, newsModels);
        rvMainBerita.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvMainBerita.setAdapter(usNewsAdapter);
    }

    private void onClick() {
        clSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsMainActivity.this, UsSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        imgKelasGratis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(UsMainActivity.this, UsListFreeCourseActivity.class);
//                startActivity(intent);
                Toast.makeText(UsMainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        imgKelasOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsMainActivity.this, UsKelasOnlineActivity.class);
                startActivity(intent);
            }
        });
        imgKelasCampuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsMainActivity.this, UsKelasBlendedActivity.class);
                startActivity(intent);
            }
        });
        btnBeritaLainnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsMainActivity.this, UsListNewsActivity.class);
                startActivity(intent);
            }
        });
        tvDiikutiSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UsMainActivity.this, "Anda belum memiliki course", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserDocId() {

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
                                userDocId = queryDocumentSnapshot.getId();
                            }
                            getOngoingBlendedMateri();
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

    private void getOngoingBlendedMateri() {
        CollectionReference ongoingRef = firebaseFirestore
                .collection("User")
                .document(userDocId)
                .collection("OngoingBlendedMateri");

        ongoingRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
//                            ongoingBlendedMateri = queryDocumentSnapshot.toObject(OngoingMateriModel.class);
                        }
                        getOngoingOnlineMateri();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void getOngoingOnlineMateri() {
        CollectionReference ongoingRef = firebaseFirestore
                .collection("User")
                .document(userDocId)
                .collection("OngoingOnlineMateri");

        ongoingRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
//                            ongoingOnlineMateri = queryDocumentSnapshot.toObject(OngoingMateriModel.class);
                        }
                        compareOngoingMateri();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void compareOngoingMateri() {
//        if (ongoingBlendedMateri != null) {
//            if (ongoingOnlineMateri == null) {
//                setOngoingMateri("blended", ongoingBlendedMateri.getCourseId(), ongoingBlendedMateri.getMateriId());
//            } else if (ongoingBlendedMateri.getDateCreated() > ongoingOnlineMateri.getDateCreated()) {
//                setOngoingMateri("blended", ongoingBlendedMateri.getCourseId(), ongoingBlendedMateri.getMateriId());
//            } else {
//                setOngoingMateri("online", ongoingOnlineMateri.getCourseId(), ongoingOnlineMateri.getMateriId());
//            }
//        } else if (ongoingOnlineMateri != null) {
//            setOngoingMateri("online", ongoingOnlineMateri.getCourseId(), ongoingOnlineMateri.getMateriId());
//        }
    }

    private void setOngoingMateri(final String jenisKelas, String courseId, final String materiId) {
        DocumentReference ongoingRef;
        final Intent intent;
        if (jenisKelas.equalsIgnoreCase("online")) {
            ongoingRef = firebaseFirestore
                    .collection("OnlineCourse")
                    .document(courseId)
                    .collection("OnlineMateri")
                    .document(materiId);
            setOngoingTag("OnlineCourse", courseId);
            intent = new Intent(UsMainActivity.this, UsListVideoOnlineActivity.class);
        } else {
            ongoingRef = firebaseFirestore
                    .collection("BlendedCourse")
                    .document(courseId)
                    .collection("BlendedMateri")
                    .document(materiId);
            setOngoingTag("BlendedCourse", courseId);
            intent = new Intent(UsMainActivity.this, UsListVideoBlendedActivity.class);
        }

        ongoingRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        materiModel = documentSnapshot.toObject(MateriModel.class);
//                        if (materiModel != null) {
//                            materiModel.setDocumentId(documentSnapshot.getId());
//
//                            Glide.with(UsMainActivity.this)
//                                    .load(materiModel.getThumbnailUrl())
//                                    .apply(new RequestOptions().centerCrop())
//                                    .into(imgOngoing);
//                            tvJudulOngoing.setText(materiModel.getTitle());
//                            clOngoing.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    intent.putExtra("jenisKelas", jenisKelas);
//                                    intent.putExtra("materiModel", materiModel);
//                                    startActivity(intent);
//                                }
//                            });
//
//                            setOngoingView();
//                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void setOngoingTag(String jenisKelas, String courseId) {
        DocumentReference ongoingRef = firebaseFirestore
                .collection(jenisKelas)
                .document(courseId);
        ongoingRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        CourseModel courseModel = documentSnapshot.toObject(CourseModel.class);
//                        if (courseModel != null) {
//                            tvTagOngoing.setText(courseModel.getTag());
//                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });

    }

    private void setOngoingView() {
        clOngoing.setVisibility(View.VISIBLE);
        tvDiikutiSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsMainActivity.this, UsOngoingActivity.class);
                intent.putExtra("userDocId", userDocId);
                startActivity(intent);
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
                            newsModel.setDocumentId(queryDocumentSnapshot.getId());

                            newsModels.add(newsModel);
                        }
                        usNewsAdapter.notifyDataSetChanged();
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
