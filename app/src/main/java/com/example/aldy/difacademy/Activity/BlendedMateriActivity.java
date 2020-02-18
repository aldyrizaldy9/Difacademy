package com.example.aldy.difacademy.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.R;

public class BlendedMateriActivity extends AppCompatActivity {
    private TextView tvNavBar;
    private ImageView imgBack;
    private ConstraintLayout clBack,clNavBar;
    private RecyclerView rvMateri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blended_materi);
        initView();
        setOnClick();
        setRecyclerView();
    }

    private void initView() {
        tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Daftar Materi");
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clNavBar = findViewById(R.id.cl_navbar);
        clNavBar.setBackgroundColor(getResources().getColor(R.color.navCoklat));
        rvMateri = findViewById(R.id.rv_blended_materi_materi);
    }

    private void setOnClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void setRecyclerView(){

    }
}
