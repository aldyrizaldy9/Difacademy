package com.example.aldy.difacademy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.aldy.difacademy.Model.NewsModel;
import com.example.aldy.difacademy.R;

public class DetailNewsActivity extends AppCompatActivity {
    private TextView tvJudul, tvIsi;
    private ImageView imgThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);
        initView();
        setViewWithParcelable();
    }

    private void initView() {
        tvJudul = findViewById(R.id.tv_detail_news_judul);
        tvIsi = findViewById(R.id.tv_detail_news_isi);
        imgThumbnail = findViewById(R.id.img_detail_news_thumbnail);
    }

    private void setViewWithParcelable() {
        Intent intent = getIntent();
        NewsModel newsModel = intent.getParcelableExtra("newsModel");
        tvJudul.setText(newsModel.getJudul());
        tvIsi.setText(newsModel.getIsi());
        Glide.with(this).load(newsModel.getLinkfoto()).into(imgThumbnail);
    }
}
