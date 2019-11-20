package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
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

import com.example.aldy.difacademy.Adapter.NewsAdapter;
import com.example.aldy.difacademy.Model.NewsModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListNewsActivity extends AppCompatActivity {
    private ConstraintLayout clBack;
    private RecyclerView rvNews;
    private ArrayList<NewsModel> newsModels;
    private NewsAdapter newsAdapter;
    private ProgressDialog progressDialog;

    private static final String TAG = "ListNewsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_news);
        initView();
        onClick();
        setRecyclerView();
        loadData();
    }

    private void initView() {
        TextView tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText(R.string.berita);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        rvNews = findViewById(R.id.rv_list_news);
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
        newsModels = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, newsModels);
        rvNews.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvNews.setAdapter(newsAdapter);
    }

    private void loadData() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference newsRef = firestore.collection("News");
        newsRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            NewsModel newsModel = queryDocumentSnapshot.toObject(NewsModel.class);
                            newsModel.setNewsId(queryDocumentSnapshot.getId());

                            newsModels.add(newsModel);
                        }
                        newsAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }
}
