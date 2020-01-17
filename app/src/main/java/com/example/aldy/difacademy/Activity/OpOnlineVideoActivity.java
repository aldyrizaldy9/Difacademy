package com.example.aldy.difacademy.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aldy.difacademy.Adapter.OpBlendedCourseVideoAdapter;
import com.example.aldy.difacademy.Adapter.OpOnlineVideoAdapter;
import com.example.aldy.difacademy.Model.BlendedVideoModel;
import com.example.aldy.difacademy.Model.OnlineVideoModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.aldy.difacademy.Activity.OpAddBlendedCourseActivity.blendedCourseDocId;
import static com.example.aldy.difacademy.Activity.OpAddOnlineCourseActivity.onlineCourseDocId;
import static com.example.aldy.difacademy.Activity.OpAddOnlineMateriActivity.onlineMateriDocId;
import static com.example.aldy.difacademy.Activity.OpMainActivity.ADD_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.DELETE_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpOnlineVideoActivity extends AppCompatActivity {
    TextView tvNavbar;
    ConstraintLayout clBack, clAdd;
    ImageView imgBack, imgAdd;

    RecyclerView rvOnlineVideo;
    ArrayList<OnlineVideoModel> onlineVideoModels;
    OpOnlineVideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_online_video);

        onlineVideoModels = new ArrayList<>();

        initView();
        setRecyclerView();
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        OnlineVideoModel model = intent.getParcelableExtra("online_video_model");
        int index = intent.getIntExtra("index", -1);

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                onlineVideoModels.add(model);
            }
        } else if (requestCode == DELETE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (index != -1) {
                onlineVideoModels.remove(index);
            }
        } else if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                onlineVideoModels.set(index, model);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Add Video Blended");
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
        clAdd = findViewById(R.id.cl_icon3);
        clAdd.setVisibility(View.VISIBLE);
        clAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpOnlineVideoActivity.this, OpAddOnlineVideoActivity.class);
                startActivity(intent);
            }
        });
        imgAdd = findViewById(R.id.img_icon3);
        imgAdd.setImageResource(R.drawable.ic_add);

        rvOnlineVideo = findViewById(R.id.rv_op_online_video);
    }

    private void loadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collRef = db.collection("OnlineCourse")
                .document(onlineCourseDocId)
                .collection("OnlineMateri")
                .document(onlineMateriDocId)
                .collection("OnlineVideo");

        collRef.orderBy("dateCreated", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        onlineVideoModels.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            OnlineVideoModel model = documentSnapshot.toObject(OnlineVideoModel.class);
                            model.setDocumentId(documentSnapshot.getId());
                            onlineVideoModels.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void setRecyclerView() {
        rvOnlineVideo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new OpOnlineVideoAdapter(this, onlineVideoModels);
        rvOnlineVideo.setAdapter(adapter);
    }
}
