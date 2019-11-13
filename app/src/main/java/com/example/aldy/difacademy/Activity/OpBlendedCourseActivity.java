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

import com.example.aldy.difacademy.Adapter.OpBlendedCourseAdapter;
import com.example.aldy.difacademy.Model.BlendedCourseModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OpBlendedCourseActivity extends AppCompatActivity {
    private static final String TAG = "OpBlendedCourseActivity";

    TextView tvNavbar;
    ConstraintLayout clBack, clAdd;
    ImageView imgBack, imgAdd;
    RecyclerView rvBlended;

    ArrayList<BlendedCourseModel> blendedCourseModels;
    OpBlendedCourseAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference blendedCourseRef = db.collection("BlendedCourse");

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_blended_course);

        blendedCourseModels = new ArrayList<>();

        initView();
        onClick();
        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Blended Course");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        clAdd = findViewById(R.id.cl_icon3);
        clAdd.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        imgAdd = findViewById(R.id.img_icon3);
        imgAdd.setImageResource(R.drawable.ic_add);
        rvBlended = findViewById(R.id.rv_op_blended);

        progressDialog = new ProgressDialog(this);
    }

    private void onClick() {
        clAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpBlendedCourseActivity.this, OpAddBlendedCourseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView() {
        rvBlended.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new OpBlendedCourseAdapter(this, blendedCourseModels);
        rvBlended.setAdapter(adapter);
    }

    private void loadData() {
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Memuat");
        blendedCourseRef.orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        blendedCourseModels.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            BlendedCourseModel newBlendedCourseModel = documentSnapshot.toObject(BlendedCourseModel.class);
                            newBlendedCourseModel.setDocumentId(documentSnapshot.getId());
                            blendedCourseModels.add(newBlendedCourseModel);
                        }
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(OpBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
