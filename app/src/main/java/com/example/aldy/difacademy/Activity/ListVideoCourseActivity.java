package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.example.aldy.difacademy.Adapter.BlendedCourseVideoAdapter;
import com.example.aldy.difacademy.Model.BlendedVideoModel;
import com.example.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;

import static com.example.aldy.difacademy.Activity.LoginActivity.SHARE_PREFS;
import static com.example.aldy.difacademy.Activity.LoginActivity.USERID_PREFS;

public class ListVideoCourseActivity extends AppCompatActivity {
    private static final String TAG = "ListVideoCourseActivity";
    public static String BLENDED_COURSE_ID;
    public static boolean ISPAID = false;
    private TextView tvNavbar;
    private ConstraintLayout clBack, clNavbar, clQuiz;
    private ImageView imgBack;
    private RecyclerView rvListVideoCourse;
    private ArrayList<BlendedVideoModel> blendedVideoModels;
    private BlendedCourseVideoAdapter blendedCourseVideoAdapter;
    private ProgressDialog progressDialog;
    private String docId;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference blendedVideoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video_course);
        initView();
        onClick();
        setRecyclerView();
        getUserDocId();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ISPAID = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ISPAID = false;
    }

    private void initView() {
        clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navCoklat));
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Daftar Video");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        rvListVideoCourse = findViewById(R.id.rv_list_video_course);
        clQuiz = findViewById(R.id.cl_list_video_course_quiz);
        progressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
        Intent intent = getIntent();
        BLENDED_COURSE_ID = intent.getStringExtra("BLENDED_COURSE_ID");
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
                if (ISPAID) {
                    Intent intent = new Intent(ListVideoCourseActivity.this, QuizActivity.class);
                    intent.putExtra("BLENDED_COURSE_ID", BLENDED_COURSE_ID);
                    startActivity(intent);
                } else {
                    Toast.makeText(ListVideoCourseActivity.this, "Anda belum membeli course ini", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setRecyclerView() {
        blendedVideoModels = new ArrayList<>();
        blendedCourseVideoAdapter = new BlendedCourseVideoAdapter(this, blendedVideoModels);
        rvListVideoCourse.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvListVideoCourse.setAdapter(blendedCourseVideoAdapter);
    }

    private void loadData() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        blendedVideoRef = firebaseFirestore
                .collection("BlendedCourse")
                .document(BLENDED_COURSE_ID)
                .collection("VideoMateri");
        blendedVideoRef
                .orderBy("dateCreated", Query.Direction.ASCENDING)
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            BlendedVideoModel blendedVideoModel = queryDocumentSnapshot.toObject(BlendedVideoModel.class);
                            blendedVideoModel.setDocumentId(queryDocumentSnapshot.getId());

                            blendedVideoModels.add(blendedVideoModel);
                        }
                        progressDialog.dismiss();
                        clQuiz.setVisibility(View.VISIBLE);
                        blendedCourseVideoAdapter.notifyDataSetChanged();
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
                        checkPayment(docId);
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

    private void checkPayment(String docId) {
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
                            if (ongoingKelasBlendedModel.getKelasBlendedId().equals(BLENDED_COURSE_ID)) {
                                ISPAID = true;
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
