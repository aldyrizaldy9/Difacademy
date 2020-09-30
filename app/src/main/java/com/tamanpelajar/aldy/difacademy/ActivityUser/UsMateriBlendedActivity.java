package com.tamanpelajar.aldy.difacademy.ActivityUser;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tamanpelajar.aldy.difacademy.Adapter.UsMateriBlendedAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.KelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.MateriBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class UsMateriBlendedActivity extends AppCompatActivity {
    private static final String TAG = "UsMateriBlendedActivity";
    private ConstraintLayout clBack;
    private RecyclerView rvVideo;
    private UsMateriBlendedAdapter adapter;
    private ArrayList<MateriBlendedModel> materiBlendedModels;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private KelasBlendedModel kelasBlendedModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_blended_materi);
        initView();
        onClick();
        setRecyclerView();
        loadMateriData();
    }

    private void initView() {
        ConstraintLayout clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navCoklat));
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        TextView tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Materi");
        rvVideo = findViewById(R.id.rv_blended_materi_materi);
        Intent intent = getIntent();
        kelasBlendedModel = intent.getParcelableExtra(CommonMethod.intentKelasBlendedModel);

        progressDialog = new ProgressDialog(this);
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
        materiBlendedModels = new ArrayList<>();
        adapter = new UsMateriBlendedAdapter(this, materiBlendedModels);
        rvVideo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvVideo.setAdapter(adapter);
    }

    private void loadMateriData() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        CollectionReference materiRef = firebaseFirestore
                .collection(CommonMethod.refKelasBlended)
                .document(kelasBlendedModel.getDocumentId())
                .collection(CommonMethod.refMateriBlended);

        materiRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        materiBlendedModels.clear();
                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                MateriBlendedModel materiBlendedModel = documentSnapshot.toObject(MateriBlendedModel.class);
                                materiBlendedModel.setDocumentId(documentSnapshot.getId());

                                materiBlendedModels.add(materiBlendedModel);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        progressDialog.dismiss();
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