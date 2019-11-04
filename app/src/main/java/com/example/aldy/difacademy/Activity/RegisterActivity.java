package com.example.aldy.difacademy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtNama, edtEmail, edtWa, edtSandi, edtKonfSandi;
    private ImageView imgDaftar;
    private TextView tvMasuk;

    FirebaseAuth auth;

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
        imgDaftar = findViewById(R.id.img_register_tombol_daftar);
        tvMasuk = findViewById(R.id.tv_register_masuk);
    }

    private void onClick() {
        imgDaftar.setOnClickListener(new View.OnClickListener() {
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

    private void register(String nama, String email, String wa, String sandi){
        auth.createUserWithEmailAndPassword(email, sandi)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser == null;

                            String userId = firebaseUser.getUid();

                        }
                    }
                });
    }
}
