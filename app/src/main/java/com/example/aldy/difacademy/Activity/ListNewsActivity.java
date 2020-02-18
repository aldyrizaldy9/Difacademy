package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
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

import com.example.aldy.difacademy.Adapter.NewsAdapter;
import com.example.aldy.difacademy.Model.NewsModel;
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

public class ListNewsActivity extends AppCompatActivity {
    private static final String TAG = "ListNewsActivity";
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference newsRef = firestore.collection("News");
    DocumentSnapshot lastVisible;
    boolean loadbaru;
    private ConstraintLayout clBack, clNavbar;
    private RecyclerView rvNews;
    private ArrayList<NewsModel> newsModels;
    private NewsAdapter adapter;
    private ProgressDialog progressDialog;

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
        clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navKuning));
        tvNavbar.setText(R.string.artikel);
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
        adapter = new NewsAdapter(this, newsModels);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvNews.setLayoutManager(layoutManager);
        rvNews.setAdapter(adapter);

        rvNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (layoutManager.findLastVisibleItemPosition() >= newsModels.size() - 10) {
                    if (lastVisible != null) {
                        if (loadbaru) {
                            loadbaru = false;
                            Query load = newsRef
                                    .orderBy("dateCreated", Query.Direction.DESCENDING)
                                    .startAfter(lastVisible)
                                    .limit(20);
                            load.get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (queryDocumentSnapshots.size() > 0) {
                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                    NewsModel newsModel = documentSnapshot.toObject(NewsModel.class);
                                                    newsModel.setNewsId(documentSnapshot.getId());
                                                    newsModels.add(newsModel);
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
                                            Toast.makeText(ListNewsActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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

    private void loadData() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Query first = newsRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(20);
        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        newsModels.clear();

                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                NewsModel newsModel = queryDocumentSnapshot.toObject(NewsModel.class);
                                newsModel.setNewsId(queryDocumentSnapshot.getId());
                                newsModels.add(newsModel);
                            }

                            if (queryDocumentSnapshots.size() < 20) {
                                lastVisible = null;
                            } else {
                                lastVisible = queryDocumentSnapshots.getDocuments()
                                        .get(queryDocumentSnapshots.size() - 1);
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
