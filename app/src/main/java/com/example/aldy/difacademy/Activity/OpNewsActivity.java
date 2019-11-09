package com.example.aldy.difacademy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Adapter.OpNewsAdapter;
import com.example.aldy.difacademy.Model.OpNewsModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class OpNewsActivity extends AppCompatActivity {
    private TextView tvNavBar;
    private ConstraintLayout clTambah, clBack;
    private ImageView imgTambah, imgBack;
    private RecyclerView rvNews;
    private ArrayList<OpNewsModel> opNewsModels;
    private OpNewsAdapter opNewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_news);
        findView();
        onClick();
        setRecyclerView();
    }

    private void findView() {
        tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Berita");
        clTambah = findViewById(R.id.cl_icon3);
        clTambah.setVisibility(View.VISIBLE);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgTambah = findViewById(R.id.img_icon3);
        imgTambah.setImageResource(R.drawable.ic_add);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        rvNews = findViewById(R.id.rv_op_news);
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
        opNewsModels = new ArrayList<>();
        opNewsAdapter = new OpNewsAdapter(this, opNewsModels);
        rvNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvNews.setAdapter(opNewsAdapter);
    }

}
