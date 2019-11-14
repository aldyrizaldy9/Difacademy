package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private ConstraintLayout clBack;
    private EditText edtEmail;
    private Button btnLupaSandi;
    private ProgressDialog progressDialog;

    private static final String TAG = "ForgetPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initView();
        onClick();
    }

    private void initView() {
        clBack = findViewById(R.id.cl_lupa_sandi_back);
        edtEmail = findViewById(R.id.edt_lupa_sandi_email);
        btnLupaSandi = findViewById(R.id.btn_lupa_sandi_lupa_sandi);
        progressDialog = new ProgressDialog(this);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnLupaSandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmail.getText().length() != 0) {
                    resetPassword(edtEmail.getText().toString());
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetPassword(String email) {
        progressDialog.setMessage("Memproses permintaan lupa password");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgetPasswordActivity.this, "Mohon periksa email anda", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }
}
