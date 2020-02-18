package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Adapter.MateriAdapter;
import com.example.aldy.difacademy.Model.MateriModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OnlineMateriActivity extends AppCompatActivity {
    private static final String TAG = "OnlineMateriActivity";
    private DocumentSnapshot lastVisible;
    private boolean loadbaru;
    private ConstraintLayout clBack, clNavbar;
    private RecyclerView rvVideo;
    private MateriAdapter adapter;
    private ArrayList<MateriModel> materiModels;
    private ProgressDialog pd;
    private String courseId;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference materiRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_materi);
        initView();
        onClick();
        setRecyclerView();
        loadMateriData();
    }

    private void initView() {
        clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navCoklat));
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        TextView tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Materi");
        rvVideo = findViewById(R.id.rv_online_materi_materi);
        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");

        pd = new ProgressDialog(this);
        pd.setMessage("Memuat...");
        pd.setCancelable(false);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setRecyclerView() {
        materiModels = new ArrayList<>();
        adapter = new MateriAdapter(this, materiModels);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvVideo.setLayoutManager(layoutManager);
        rvVideo.setAdapter(adapter);

        rvVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (layoutManager.findLastVisibleItemPosition() >= materiModels.size() - 10) {
                    if (lastVisible != null) {
                        if (loadbaru) {
                            loadbaru = false;
                            Query load;
                            load = materiRef
                                    .orderBy("dateCreated", Query.Direction.DESCENDING)
                                    .startAfter(lastVisible)
                                    .limit(20);
                            load.get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (queryDocumentSnapshots.size() > 0) {
                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                    MateriModel materiModel = documentSnapshot.toObject(MateriModel.class);
                                                    materiModel.setDocumentId(documentSnapshot.getId());

                                                    materiModels.add(materiModel);
                                                }

                                                if (queryDocumentSnapshots.size() < 20) {
                                                    lastVisible = null;
                                                } else {
                                                    lastVisible = queryDocumentSnapshots.getDocuments()
                                                            .get(queryDocumentSnapshots.size() - 1);
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                            loadbaru = true;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loadbaru = true;
                                            Toast.makeText(OnlineMateriActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void loadMateriData() {
        pd.show();
        materiRef = firebaseFirestore.collection("OnlineCourse").document(courseId).collection("OnlineMateri");

        Query first = materiRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(20);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        materiModels.clear();
                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                MateriModel materiModel = documentSnapshot.toObject(MateriModel.class);
                                materiModel.setDocumentId(documentSnapshot.getId());

                                materiModels.add(materiModel);
                            }

                            if (queryDocumentSnapshots.size() < 20) {
                                lastVisible = null;
                            } else {
                                lastVisible = queryDocumentSnapshots.getDocuments()
                                        .get(queryDocumentSnapshots.size() - 1);
                            }

                            adapter.notifyDataSetChanged();
                        }
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                    }
                });
    }

}