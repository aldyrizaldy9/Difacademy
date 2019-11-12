package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Adapter.OpNewsAdapter;
import com.example.aldy.difacademy.Model.NewsModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OpNewsActivity extends AppCompatActivity {
    private ConstraintLayout clTambah, clBack;
    private RecyclerView rvNews;
    private ArrayList<NewsModel> newsModels;
    private OpNewsAdapter opNewsAdapter;
    private FirebaseFirestore db;
    private CollectionReference newsRef;
    private static final String TAG = "OpNewsActivity";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_news);
        findView();
        onClick();
        setRecyclerView();
        getData();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getData();
    }

    private void findView() {
        TextView tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Berita");
        clTambah = findViewById(R.id.cl_icon3);
        clTambah.setVisibility(View.VISIBLE);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        ImageView imgTambah = findViewById(R.id.img_icon3);
        imgTambah.setImageResource(R.drawable.ic_add);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        rvNews = findViewById(R.id.rv_op_news);
        progressDialog = new ProgressDialog(this);
    }

    private void onClick() {
        clTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpNewsActivity.this, OpAddNewsActivity.class);
                startActivity(intent);
            }
        });
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setRecyclerView() {
        newsModels = new ArrayList<>();
        opNewsAdapter = new OpNewsAdapter(this, newsModels);
        rvNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvNews.setAdapter(opNewsAdapter);
    }

    private void getData() {
        progressDialog.setMessage("Memuat");
        progressDialog.setCancelable(false);
        progressDialog.show();
        db = FirebaseFirestore.getInstance();
        newsRef = db.collection("News");
        newsRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        newsModels.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            NewsModel newsModel = queryDocumentSnapshot.toObject(NewsModel.class);
                            newsModel.setNewsId(queryDocumentSnapshot.getId());

                            newsModels.add(newsModel);
                        }
                        opNewsAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                        progressDialog.dismiss();
                    }
                });
    }

}
