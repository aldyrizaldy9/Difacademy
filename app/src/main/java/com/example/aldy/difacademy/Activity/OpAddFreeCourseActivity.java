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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class OpAddFreeCourseActivity extends AppCompatActivity {
    private static final String TAG = "OpAddFreeCourseActivity";

    TextView tvNavber;
    ConstraintLayout clBack;
    ImageView imgBack;

    Button btnSave, btnHapus;
    EditText edtLink;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference videoFreeRef = db.collection("Videofree");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_free_course);

        initView();
        onClick();
    }

    private void initView() {
        tvNavber = findViewById(R.id.tv_navbar);
        tvNavber.setText("Video Gratis");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        btnSave = findViewById(R.id.btn_op_add_free_simpan);
        btnHapus = findViewById(R.id.btn_op_add_free_hapus);
        edtLink = findViewById(R.id.edt_op_add_free_link);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapus();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = edtLink.getText().toString();
                if (link.length() != 0) {
                    simpan(link);
                }
            }
        });
    }

    private void hapus() {

    }

    private void simpan(String link) {

    }
}