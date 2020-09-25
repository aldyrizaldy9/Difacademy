package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

import android.app.ProgressDialog;
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

import com.tamanpelajar.aldy.difacademy.Adapter.OpFreeCourseAdapter;
import com.tamanpelajar.aldy.difacademy.Model.VideoFreeModel;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpFreeCourseActivity extends AppCompatActivity {

//    private static final String TAG = "OpFreeCourseActivity";
//
//    TextView tvNavbar;
//    ConstraintLayout clBack, clAdd;
//    ImageView imgBack, imgAdd;
//    RecyclerView rvFree;
//
//    ArrayList<VideoFreeModel> videoFreeModels;
//    OpFreeCourseAdapter adapter;
//
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    CollectionReference videoFreeRef = db.collection("VideoFree");
//
//    ProgressDialog pd;
//
//    DocumentSnapshot lastVisible;
//    boolean loadbaru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_free_course);
//
//        videoFreeModels = new ArrayList<>();
//
//        initView();
//        onClick();
//        setRecyclerView();
//        loadData();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Intent intent = getIntent();
//        VideoFreeModel videoFreeModel = intent.getParcelableExtra("videoFreeModel");
//        int index = intent.getIntExtra("index", -1);
//
//        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
//            if (videoFreeModel != null) {
//                videoFreeModels.add(videoFreeModel);
//            }
//        } else if (requestCode == DELETE_REQUEST_CODE && resultCode == RESULT_OK) {
//            if (index != -1) {
//                videoFreeModels.remove(index);
//            }
//        } else if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
//            if (videoFreeModel != null) {
//                videoFreeModels.set(index, videoFreeModel);
//            }
//        }
//        adapter.notifyDataSetChanged();
//    }
//
//    private void initView() {
//        tvNavbar = findViewById(R.id.tv_navbar);
//        tvNavbar.setText("Short Course");
//        clBack = findViewById(R.id.cl_icon1);
//        clBack.setVisibility(View.VISIBLE);
//        clAdd = findViewById(R.id.cl_icon3);
//        clAdd.setVisibility(View.VISIBLE);
//        imgBack = findViewById(R.id.img_icon1);
//        imgBack.setImageResource(R.drawable.ic_arrow_back);
//        imgAdd = findViewById(R.id.img_icon3);
//        imgAdd.setImageResource(R.drawable.ic_add);
//        rvFree = findViewById(R.id.rv_op_free);
//        pd = new ProgressDialog(this);
//    }
//
//    private void onClick() {
//        clBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//        clAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(OpFreeCourseActivity.this, OpAddFreeCourseActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//
//    private void setRecyclerView() {
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        rvFree.setLayoutManager(layoutManager);
//        adapter = new OpFreeCourseAdapter(this, videoFreeModels);
//        rvFree.setAdapter(adapter);
//
//        loadbaru = true;
//
//        rvFree.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (layoutManager.findLastVisibleItemPosition() >= videoFreeModels.size() - 10) {
//                    if (lastVisible != null) {
//                        if (loadbaru) {
//                            loadbaru = false;
//                            Query load = videoFreeRef
//                                    .orderBy("dateCreated", Query.Direction.DESCENDING)
//                                    .startAfter(lastVisible)
//                                    .limit(20);
//                            load.get()
//                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                            if (queryDocumentSnapshots.size() > 0) {
//                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                                    VideoFreeModel newVideoFreeModel = documentSnapshot.toObject(VideoFreeModel.class);
//                                                    newVideoFreeModel.setDocumentId(documentSnapshot.getId());
//                                                    videoFreeModels.add(newVideoFreeModel);
//                                                }
//
//                                                if (queryDocumentSnapshots.size() < 20) {
//                                                    lastVisible = null;
//                                                } else {
//                                                    lastVisible = queryDocumentSnapshots.getDocuments()
//                                                            .get(queryDocumentSnapshots.size() - 1);
//                                                }
//                                                adapter.notifyDataSetChanged();
//                                            }
//                                            loadbaru = true;
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            loadbaru = true;
//                                            Toast.makeText(OpFreeCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
//    }
//
//    private void loadData() {
//        pd.setMessage("Memuat...");
//        pd.setCancelable(false);
//        pd.show();
//
//        Query first = videoFreeRef
//                .orderBy("dateCreated", Query.Direction.DESCENDING)
//                .limit(20);
//
//        first.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        videoFreeModels.clear();
//                        if (queryDocumentSnapshots.size() > 0) {
//                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                VideoFreeModel newVideoFreeModel = documentSnapshot.toObject(VideoFreeModel.class);
//                                newVideoFreeModel.setDocumentId(documentSnapshot.getId());
//                                videoFreeModels.add(newVideoFreeModel);
//                            }
//
//                            if (queryDocumentSnapshots.size() < 20) {
//                                lastVisible = null;
//                            } else {
//                                lastVisible = queryDocumentSnapshots.getDocuments()
//                                        .get(queryDocumentSnapshots.size() - 1);
//                            }
//
//                            adapter.notifyDataSetChanged();
//                        }
//                        pd.dismiss();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        pd.dismiss();
//                        Toast.makeText(OpFreeCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

}
