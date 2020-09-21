package com.tamanpelajar.aldy.difacademy.ActivityUser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tamanpelajar.aldy.difacademy.Adapter.VideoBlendedAdapter;
import com.tamanpelajar.aldy.difacademy.Model.MateriModel;
import com.tamanpelajar.aldy.difacademy.Model.OngoingMateriModel;
import com.tamanpelajar.aldy.difacademy.Model.VideoModel;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;

import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.SHARE_PREFS;
import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.USERID_PREFS;

public class UsListVideoBlendedActivity extends AppCompatActivity {
    private static final String TAG = "ListVideoBlendedActivit";
    public static boolean IS_PAID = false;
    private ConstraintLayout clBack;
    private ConstraintLayout clQuiz;
    private RecyclerView rvListVideoCourse;
    private ArrayList<VideoModel> videoModels;
    private VideoBlendedAdapter videoBlendedAdapter;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private String userDocId;
    private SharedPreferences sharedPreferences;
    private MateriModel materiModel;
    private String jenisKelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_list_video_course);
        initView();
        onClick();
        setRecyclerView();
        getUserDocId();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IS_PAID = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IS_PAID = false;
    }

    private void initView() {
        ConstraintLayout clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navCoklat));
        TextView tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Daftar Video");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        rvListVideoCourse = findViewById(R.id.rv_list_video_course);
        clQuiz = findViewById(R.id.cl_list_video_course_quiz);
        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        materiModel = intent.getParcelableExtra("materiModel");
        jenisKelas = intent.getStringExtra("jenisKelas");
        sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_PAID) {
                    Intent intent = new Intent(UsListVideoBlendedActivity.this, UsQuizActivity.class);
                    intent.putExtra("jenisKelas", jenisKelas);
                    intent.putExtra("materiModel", materiModel);
                    startActivity(intent);
                } else {
                    Toast.makeText(UsListVideoBlendedActivity.this, "Anda belum membeli materi ini!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setRecyclerView() {
        videoModels = new ArrayList<>();
        videoBlendedAdapter = new VideoBlendedAdapter(this, videoModels);
        rvListVideoCourse.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvListVideoCourse.setAdapter(videoBlendedAdapter);
    }


    private void loadData() {
        CollectionReference videoRef = firebaseFirestore
                .collection("BlendedCourse")
                .document(materiModel.getCourseId())
                .collection("BlendedMateri")
                .document(materiModel.getDocumentId())
                .collection("BlendedVideo");


        videoRef
                .orderBy("dateCreated", Query.Direction.ASCENDING)
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            VideoModel videoModel = queryDocumentSnapshot.toObject(VideoModel.class);
                            videoModel.setDocumentId(queryDocumentSnapshot.getId());

                            videoModels.add(videoModel);
                        }
                        progressDialog.dismiss();
                        clQuiz.setVisibility(View.VISIBLE);
                        videoBlendedAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void getUserDocId() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();

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
                            userDocId = queryDocumentSnapshot.getId();
                        }

                            checkOngoing(userDocId);

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

    private void checkOngoing(final String userDocId) {
        CollectionReference onGoingRef = firebaseFirestore
                .collection("User")
                .document(userDocId)
                .collection("OngoingBlendedMateri");
        onGoingRef
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            OngoingMateriModel ongoingMateriModel = queryDocumentSnapshot.toObject(OngoingMateriModel.class);
                            if (ongoingMateriModel.getMateriId().equals(materiModel.getDocumentId())) {
                                IS_PAID = true;
                                break;
                            }
                        }
                        loadData();
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
