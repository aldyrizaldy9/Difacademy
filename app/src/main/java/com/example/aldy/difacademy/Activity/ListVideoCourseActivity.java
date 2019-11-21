package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Adapter.BlendedCourseVideoAdapter;
import com.example.aldy.difacademy.Model.BlendedVideoModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListVideoCourseActivity extends AppCompatActivity {
    private TextView tvNavbar;
    private ConstraintLayout clBack;
    private ImageView imgBack;
    private RecyclerView rvListVideoCourse;
    private ArrayList<BlendedVideoModel> blendedVideoModels;
    private BlendedCourseVideoAdapter blendedCourseVideoAdapter;
    private ProgressDialog progressDialog;
    private Button btnQuiz;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference blendedVideoRef;

    private static final String TAG = "ListVideoCourseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video_course);
        initView();
        onClick();
        setRecyclerView();
        loadData();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Daftar Video");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        rvListVideoCourse = findViewById(R.id.rv_list_video_course);
        btnQuiz = findViewById(R.id.btn_list_video_course_quiz);
        progressDialog = new ProgressDialog(this);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = getIntent();
                String courseId = intent2.getStringExtra("blendedCourseId");

                Intent intent = new Intent(ListVideoCourseActivity.this, QuizActivity.class);
                intent.putExtra("blendedCourseId", courseId);
                startActivity(intent);
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
        progressDialog.setMessage("Memuat");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Intent intent = getIntent();
        firebaseFirestore = FirebaseFirestore.getInstance();
        blendedVideoRef = firebaseFirestore
                .collection("BlendedCourse")
                .document(intent.getStringExtra("blendedCourseId"))
                .collection("VideoMateri");
        blendedVideoRef
                .orderBy("dateCreated", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            BlendedVideoModel blendedVideoModel = queryDocumentSnapshot.toObject(BlendedVideoModel.class);
                            blendedVideoModel.setDocumentId(queryDocumentSnapshot.getId());

                            blendedVideoModels.add(blendedVideoModel);
                        }
                        progressDialog.dismiss();
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
}
