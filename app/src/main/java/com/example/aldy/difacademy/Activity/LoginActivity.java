package com.example.aldy.difacademy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aldy.difacademy.R;

public class LoginActivity extends AppCompatActivity {

    private ImageView imgLogin;
    private TextView tvDaftar, tvLupaKataSandi;
    private EditText edtEmail, edtKataSandi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
        onClick();
    }

    private void findView() {
        imgLogin = findViewById(R.id.img_login_tombol_masuk);
        tvDaftar = findViewById(R.id.tv_login_daftar);
        tvLupaKataSandi = findViewById(R.id.tv_login_lupa_sandi);
        edtEmail = findViewById(R.id.edt_login_email);
        edtKataSandi = findViewById(R.id.edt_login_kata_sandi);
    }

    private void onClick() {
        imgLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        tvDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        tvLupaKataSandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
