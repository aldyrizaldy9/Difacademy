package com.example.aldy.difacademy.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aldy.difacademy.R;

public class OpBlendedCourseActivity extends AppCompatActivity {
    private static final String TAG = "OpBlendedCourseActivity";

    TextView tvNavbar;
    ConstraintLayout clBack, clAdd;
    ImageView imgBack, imgAdd;
    RecyclerView rvBlended;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_blended_course);

        initView();
        onClick();
        setRecyclerView();
    }

    private void initView(){
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
    }

    private void onClick(){
        clAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpBlendedCourseActivity.this, OpAddBlendedCourseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView(){

    }
}
