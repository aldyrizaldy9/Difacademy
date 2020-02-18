package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Adapter.VideoCourseAdapter;
import com.example.aldy.difacademy.Model.VideoModel;
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

import static com.example.aldy.difacademy.Activity.BlendedMateriActivity.BLENDED_COURSE_ID;
import static com.example.aldy.difacademy.Activity.MainActivity.JENIS_KELAS;
import static com.example.aldy.difacademy.Activity.OnlineMateriActivity.ONLINE_COURSE_ID;

public class ListVideoCourseActivity extends AppCompatActivity {
    private static final String TAG = "ListVideoCourseActivity";
    private TextView tvNavbar;
    private ConstraintLayout clBack, clNavbar, clQuiz;
    private ImageView imgBack;
    private RecyclerView rvListVideoCourse;
    private ArrayList<VideoModel> videoModels;
    private VideoCourseAdapter videoCourseAdapter;
    private ProgressDialog progressDialog;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference videoRef;
    private String materiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video_course);
        initView();
        onClick();
        setRecyclerView();
        loadData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        Intent intent = getIntent();
        materiId = intent.getStringExtra("materiId");
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
                Intent intent = new Intent(ListVideoCourseActivity.this, QuizActivity.class);
                intent.putExtra("materiId", materiId);
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView() {
        videoModels = new ArrayList<>();
        videoCourseAdapter = new VideoCourseAdapter(this, videoModels);
        rvListVideoCourse.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvListVideoCourse.setAdapter(videoCourseAdapter);
    }

    private void loadData() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseFirestore = FirebaseFirestore.getInstance();
        if (JENIS_KELAS.equalsIgnoreCase("online")) {
            videoRef = firebaseFirestore
                    .collection("OnlineCourse")
                    .document(ONLINE_COURSE_ID)
                    .collection("OnlineMateri")
                    .document(materiId)
                    .collection("OnlineVideo");
        }else{
            videoRef = firebaseFirestore
                    .collection("BlendedCourse")
                    .document(BLENDED_COURSE_ID)
                    .collection("BlendedMateri")
                    .document(materiId)
                    .collection("BlendedVideo");
        }

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
                        videoCourseAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

}
