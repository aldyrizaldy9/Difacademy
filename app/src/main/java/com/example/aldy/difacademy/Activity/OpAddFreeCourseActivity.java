package com.example.aldy.difacademy.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aldy.difacademy.R;

public class OpAddFreeCourseActivity extends AppCompatActivity {
    private static final String TAG = "OpAddFreeCourseActivity";

    TextView tvNavber;
    ConstraintLayout clIcon1;
    ImageView imgIcon1;

    Button btnSave, btnHapus;
    EditText edtLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_free_course);

        initView();
        onClick();
    }

    private void initView(){
        tvNavber = findViewById(R.id.tv_navbar);
        tvNavber.setText("Video Gratis");

        clIcon1 = findViewById(R.id.cl_icon1);
        clIcon1.setVisibility(View.VISIBLE);
        clIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imgIcon1 = findViewById(R.id.img_icon1);
        imgIcon1.setImageResource(R.drawable.ic_arrow_back);

        btnSave = findViewById(R.id.btn_simpan);
        btnHapus = findViewById(R.id.btn)
    }

    private void onClick(){

    }
}
