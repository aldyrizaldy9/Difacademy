package com.example.aldy.difacademy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aldy.difacademy.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtNama, edtEmail, edtWa, edtSandi, edtKonfSandi;
    private ImageView ivDaftar;
    private TextView tvMasuk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findView();
        onClick();
    }

    private void findView() {
        edtNama = findViewById(R.id.edt_register_nama);
        edtEmail = findViewById(R.id.edt_register_email);
        edtWa = findViewById(R.id.edt_register_wa);
        edtSandi = findViewById(R.id.edt_register_kata_sandi);
        edtKonfSandi = findViewById(R.id.edt_register_konf_kata_sandi);
        ivDaftar = findViewById(R.id.iv_register_tombol_daftar);
        tvMasuk = findViewById(R.id.tv_register_masuk);
    }

    private void onClick() {
        ivDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        tvMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
