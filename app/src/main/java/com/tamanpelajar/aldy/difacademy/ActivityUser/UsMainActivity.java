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
import com.bumptech.glide.request.RequestOptions;
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
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.KelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.KelasOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.MateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.NewsModel;
import com.tamanpelajar.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.OngoingMateriOnlineModel;
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
    private String userDocId;
    private OngoingKelasBlendedModel ongoingKelasBlendedModel;
    private OngoingMateriOnlineModel ongoingMateriOnlineModel;
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
        DocumentReference docRef = firebaseFirestore.collection(CommonMethod.refBannerPhotoUrl).document("bannerphotourl");
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
        CollectionReference userRef = firebaseFirestore.collection(CommonMethod.refUser);
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
                            getOngoingKelasBlended();
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

    private void getOngoingKelasBlended() {
        CollectionReference ongoingRef = firebaseFirestore
                .collection(CommonMethod.refUser)
                .document(userDocId)
                .collection(CommonMethod.refOngoingKelasBlended);

        ongoingRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            ongoingKelasBlendedModel = queryDocumentSnapshot.toObject(OngoingKelasBlendedModel.class);
                        }
                        getOngoingMateriOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void getOngoingMateriOnline() {
        CollectionReference ongoingRef = firebaseFirestore
                .collection(CommonMethod.refUser)
                .document(userDocId)
                .collection(CommonMethod.refOngoingMateriOnline);

        ongoingRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            ongoingMateriOnlineModel = queryDocumentSnapshot.toObject(OngoingMateriOnlineModel.class);
                        }
                        compareOngoing();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void compareOngoing() {
        if (ongoingMateriOnlineModel != null) {
            if (ongoingKelasBlendedModel == null) {
                setOngoingMateriOnline(ongoingMateriOnlineModel.getKelasId(), ongoingMateriOnlineModel.getMateriId());
            } else if (ongoingMateriOnlineModel.getDateCreated() > ongoingKelasBlendedModel.getDateCreated()) {
                setOngoingMateriOnline(ongoingMateriOnlineModel.getKelasId(), ongoingMateriOnlineModel.getMateriId());
            } else {
                setOngoingKelasBlended(ongoingKelasBlendedModel.getKelasId());
            }
        } else {
            setOngoingKelasBlended(ongoingKelasBlendedModel.getKelasId());
        }
    }

    private void setOngoingMateriOnline(String courseId, final String materiId) {
        DocumentReference ongoingRef;
        ongoingRef = firebaseFirestore
                .collection(CommonMethod.refKelasOnline)
                .document(courseId)
                .collection(CommonMethod.refMateriOnline)
                .document(materiId);
        setOngoingTagOnline(courseId);
        final Intent intent = new Intent(UsMainActivity.this, UsListVideoOnlineActivity.class);

        ongoingRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final MateriOnlineModel materiOnlineModel = documentSnapshot.toObject(MateriOnlineModel.class);
                        if (materiOnlineModel != null) {
                            materiOnlineModel.setDocumentId(documentSnapshot.getId());

                            Glide.with(UsMainActivity.this)
                                    .load(materiOnlineModel.getThumbnailUrl())
                                    .apply(new RequestOptions().centerCrop())
                                    .into(imgOngoing);
                            tvJudulOngoing.setText(materiOnlineModel.getTitle());
                            clOngoing.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    intent.putExtra(CommonMethod.intentMateriOnlineModel, materiOnlineModel);
                                    startActivity(intent);
                                }
                            });

                            setOngoingView();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void setOngoingKelasBlended(String courseId) {
        DocumentReference ongoingRef;
        ongoingRef = firebaseFirestore
                .collection(CommonMethod.refKelasBlended)
                .document(courseId);
        setOngoingTagBlended(courseId);
        final Intent intent = new Intent(UsMainActivity.this, UsMateriBlendedActivity.class);
        ongoingRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final KelasBlendedModel kelasBlendedModel = documentSnapshot.toObject(KelasBlendedModel.class);
                        if (kelasBlendedModel != null) {
                            kelasBlendedModel.setDocumentId(documentSnapshot.getId());

                            Glide.with(UsMainActivity.this)
                                    .load(kelasBlendedModel.getThumbnailUrl())
                                    .apply(new RequestOptions().centerCrop())
                                    .into(imgOngoing);
                            tvJudulOngoing.setText(kelasBlendedModel.getTitle());
                            clOngoing.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    intent.putExtra(CommonMethod.intentKelasBlendedModel, kelasBlendedModel);
                                    startActivity(intent);
                                }
                            });

                            setOngoingView();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void setOngoingTagOnline(String courseId) {
        DocumentReference ongoingRef = firebaseFirestore
                .collection(CommonMethod.refKelasOnline)
                .document(courseId);
        ongoingRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        KelasOnlineModel kelasOnlineModel = documentSnapshot.toObject(KelasOnlineModel.class);
                        if (kelasOnlineModel != null) {
                            tvTagOngoing.setText(kelasOnlineModel.getTag());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void setOngoingTagBlended(String courseId) {
        DocumentReference ongoingRef = firebaseFirestore
                .collection(CommonMethod.refKelasBlended)
                .document(courseId);
        ongoingRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        KelasBlendedModel kelasBlendedModel = documentSnapshot.toObject(KelasBlendedModel.class);
                        if (kelasBlendedModel != null) {
                            tvTagOngoing.setText(kelasBlendedModel.getTag());
                        }
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
                intent.putExtra(CommonMethod.intentUserDocId, userDocId);
                startActivity(intent);
            }
        });

    }

    private void loadNews() {
        CollectionReference newsRef = firebaseFirestore.collection(CommonMethod.refNews);
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
