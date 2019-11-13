package com.example.aldy.difacademy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Adapter.OpFreeCourseAdapter;
import com.example.aldy.difacademy.Model.VideoFreeModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.aldy.difacademy.Activity.OpAddFreeCourseActivity.ADD_FREE_COURSE_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpAddFreeCourseActivity.DELETE_FREE_COURSE_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpAddFreeCourseActivity.UPDATE_FREE_COURSE_REQUEST_CODE;

public class OpFreeCourseActivity extends AppCompatActivity {

    private static final String TAG = "OpFreeCourseActivity";

    TextView tvNavbar;
    ConstraintLayout clBack, clAdd;
    ImageView imgBack, imgAdd;
    RecyclerView rvFree;

    ArrayList<VideoFreeModel> videoFreeModels;
    OpFreeCourseAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference videoFreeRef = db.collection("VideoFree");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_free_course);

        videoFreeModels = new ArrayList<>();

        initView();
        onClick();
        setRecyclerView();
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        VideoFreeModel videoFreeModel = intent.getParcelableExtra("videoFreeModel");
        int index = intent.getIntExtra("index", -1);

        if (requestCode == ADD_FREE_COURSE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (videoFreeModel != null) {
                videoFreeModels.add(videoFreeModel);
            }
        } else if (requestCode == DELETE_FREE_COURSE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (index != -1) {
                videoFreeModels.remove(index);
            }
        } else if (requestCode == UPDATE_FREE_COURSE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (videoFreeModel != null) {
                videoFreeModels.set(index, videoFreeModel);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Free Course");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clAdd = findViewById(R.id.cl_icon3);
        clAdd.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        imgAdd = findViewById(R.id.img_icon3);
        imgAdd.setImageResource(R.drawable.ic_add);
        rvFree = findViewById(R.id.rv_op_free);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        clAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpFreeCourseActivity.this, OpAddFreeCourseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView() {
        rvFree.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new OpFreeCourseAdapter(this, videoFreeModels);
        rvFree.setAdapter(adapter);
    }

    private void loadData() {
        videoFreeRef.orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        videoFreeModels.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            VideoFreeModel newVideoFreeModel = documentSnapshot.toObject(VideoFreeModel.class);
                            newVideoFreeModel.setDocumentId(documentSnapshot.getId());
                            videoFreeModels.add(newVideoFreeModel);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        videoFreeModels.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(OpFreeCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
