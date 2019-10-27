package com.example.aldy.difacademy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    ConstraintLayout clBack;
    EditText edtEmail;
    Button btnLupaSandi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        findView();
        onClick();
    }

    private void findView() {
        clBack = findViewById(R.id.cl_lupa_sandi_back);
        edtEmail = findViewById(R.id.edt_lupa_sandi_email);
        btnLupaSandi = findViewById(R.id.btn_lupa_sandi_lupa_sandi);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnLupaSandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
