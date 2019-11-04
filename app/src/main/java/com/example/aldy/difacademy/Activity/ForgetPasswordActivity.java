package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    ConstraintLayout clBack;
    EditText edtEmail;
    Button btnLupaSandi;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        pd = new ProgressDialog(ForgetPasswordActivity.this);
        pd.setMessage("Loading...");

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
                if (edtEmail.getText().length() != 0){
                    pd.show();
                    resetPassword(edtEmail.getText().toString());
                }
            }
        });
    }

    private void resetPassword(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            pd.dismiss();
                            Toast.makeText(ForgetPasswordActivity.this, "Mohon periksa email anda", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
