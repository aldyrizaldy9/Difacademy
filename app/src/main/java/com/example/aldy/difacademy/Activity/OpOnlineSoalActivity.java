package com.example.aldy.difacademy.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldy.difacademy.Adapter.OpOnlineMateriAdapter;
import com.example.aldy.difacademy.Adapter.OpOnlineSoalAdapter;
import com.example.aldy.difacademy.Model.OnlineMateriModel;
import com.example.aldy.difacademy.Model.OnlineSoalModel;
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

import static com.example.aldy.difacademy.Activity.OpAddOnlineCourseActivity.onlineCourseDocId;
import static com.example.aldy.difacademy.Activity.OpAddOnlineMateriActivity.onlineMateriDocId;
import static com.example.aldy.difacademy.Activity.OpMainActivity.ADD_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.DELETE_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpOnlineSoalActivity extends AppCompatActivity {
    TextView tvNavbar;
    ConstraintLayout clBack, clAdd;
    ImageView imgBack, imgAdd;
    RecyclerView rvOnlineSoal;

    ArrayList<OnlineSoalModel> onlineSoalModels;
    OpOnlineSoalAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference onlineSoalRef = db.collection("OnlineCourse")
            .document(onlineCourseDocId)
            .collection("OnlineMateri")
            .document(onlineMateriDocId)
            .collection("OnlineSoal");

    DocumentSnapshot lastVisible;
    boolean loadbaru;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_online_soal);

        onlineSoalModels = new ArrayList<>();

        initView();
        onClick();
        setRecyclerView();
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        OnlineSoalModel model = intent.getParcelableExtra("online_soal_model");
        int index = intent.getIntExtra("index", -1);

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                onlineSoalModels.add(model);
            }
        } else if (requestCode == DELETE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (index != -1) {
                onlineSoalModels.remove(index);
            }
        } else if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                onlineSoalModels.set(index, model);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initView(){
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Soal Materi Kelas Online");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clAdd = findViewById(R.id.cl_icon3);
        clAdd.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        imgAdd = findViewById(R.id.img_icon3);
        imgAdd.setImageResource(R.drawable.ic_add);
        rvOnlineSoal = findViewById(R.id.rv_op_online_soal);
        pd = new ProgressDialog(this);
    }

    private void onClick(){
        clAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpOnlineSoalActivity.this, OpAddOnlineSoalActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView(){
        final LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvOnlineSoal.setLayoutManager(manager);
        adapter = new OpOnlineSoalAdapter(this, onlineSoalModels);
        rvOnlineSoal.setAdapter(adapter);

        rvOnlineSoal.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (manager.findLastVisibleItemPosition() >= onlineSoalModels.size() - 10 &&
                        lastVisible != null &&
                        loadbaru) {
                    loadbaru = false;
                    Query load = onlineSoalRef
                            .orderBy("dateCreated", Query.Direction.DESCENDING)
                            .startAfter(lastVisible)
                            .limit(20);
                    load.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (queryDocumentSnapshots.size() > 0) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            OnlineSoalModel newModel = documentSnapshot.toObject(OnlineSoalModel.class);
                                            newModel.setDocumentId(documentSnapshot.getId());
                                            onlineSoalModels.add(newModel);
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
                                    Toast.makeText(OpOnlineSoalActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void loadData(){
        pd.setMessage("Memuat...");
        pd.show();

        Query first = onlineSoalRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(20);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        onlineSoalModels.clear();
                        if (queryDocumentSnapshots.size() > 0){
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                OnlineSoalModel newModel = documentSnapshot.toObject(OnlineSoalModel.class);
                                newModel.setDocumentId(documentSnapshot.getId());
                                onlineSoalModels.add(newModel);
                            }

                            if (queryDocumentSnapshots.size() < 20){
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
                        Toast.makeText(OpOnlineSoalActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
