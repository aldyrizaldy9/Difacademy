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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.tamanpelajar.aldy.difacademy.Adapter.UsVideoBlendedAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.MateriBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.VideoBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.SHARE_PREFS;
import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.USERID_PREFS;

public class UsListVideoBlendedActivity extends AppCompatActivity {
    private static final String TAG = "UsListVideoBlendedActiv";
    private ConstraintLayout clBack;
    private ConstraintLayout clQuiz;
    private RecyclerView rvListVideoCourse;
    private ArrayList<VideoBlendedModel> videoBlendedModels;
    private UsVideoBlendedAdapter usVideoBlendedAdapter;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private MateriBlendedModel materiBlendedModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_list_video_course);
        initView();
        onClick();
        setRecyclerView();
        loadData();
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
        materiBlendedModel = intent.getParcelableExtra(CommonMethod.intentMateriBlendedModel);
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
                    Intent intent = new Intent(UsListVideoBlendedActivity.this, UsQuizBlendedActivity.class);
                    intent.putExtra(CommonMethod.intentMateriBlendedModel, materiBlendedModel);
                    startActivity(intent);

            }
        });

    }

    private void setRecyclerView() {
        videoBlendedModels = new ArrayList<>();
        usVideoBlendedAdapter = new UsVideoBlendedAdapter(this, videoBlendedModels);
        rvListVideoCourse.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvListVideoCourse.setAdapter(usVideoBlendedAdapter);
    }


    private void loadData() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference videoRef = firebaseFirestore
                .collection(CommonMethod.refKelasBlended)
                .document(materiBlendedModel.getKelasId())
                .collection(CommonMethod.refMateriBlended)
                .document(materiBlendedModel.getDocumentId())
                .collection(CommonMethod.refVideoBlended);


        videoRef
                .orderBy("dateCreated", Query.Direction.ASCENDING)
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            VideoBlendedModel videoBlendedModel = queryDocumentSnapshot.toObject(VideoBlendedModel.class);
                            videoBlendedModel.setDocumentId(queryDocumentSnapshot.getId());

                            videoBlendedModels.add(videoBlendedModel);
                        }
                        progressDialog.dismiss();
                        clQuiz.setVisibility(View.VISIBLE);
                        usVideoBlendedAdapter.notifyDataSetChanged();
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
