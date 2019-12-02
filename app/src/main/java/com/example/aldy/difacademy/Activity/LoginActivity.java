package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private ImageView imgLogin;
    private EditText edtEmail, edtKataSandi;
    private ConstraintLayout clDaftar, clLupaKataSandi;

    boolean doubleBackToExitPressedOnce = false;
    ProgressDialog pd;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    public static final String SHARE_PREFS = "share_prefs";
    public static final String USERID_PREFS = "userid_prefs";
    public static final String EMAIL_PREFS = "email_prefs";
    public static final String JENIS_USER_PREFS = "jenis_user_prefs";
    public static final String JENIS_USER_ADMIN = "admin";
    public static final String JENIS_USER_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            //udah login
            SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
            String jenisUser = sharedPreferences.getString(JENIS_USER_PREFS, "");
            if (jenisUser.equals(JENIS_USER_ADMIN)) {
                Intent intent = new Intent(LoginActivity.this, OpMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        } else {
            //belum login
            setContentView(R.layout.activity_login);
            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Memuat...");
            pd.setCancelable(false);

            initView();
            onClick();
        }
    }

    private void initView() {
        imgLogin = findViewById(R.id.img_login_tombol_masuk);
        clDaftar = findViewById(R.id.cl_login_daftar);
        clLupaKataSandi = findViewById(R.id.cl_login_lupa_sandi);
        edtEmail = findViewById(R.id.edt_login_email);
        edtKataSandi = findViewById(R.id.edt_login_kata_sandi);
    }

    private void onClick() {
        imgLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmail.getText().length() != 0 || edtKataSandi.getText().length() != 0) {
                    login(edtEmail.getText().toString(), edtKataSandi.getText().toString());
                }
            }
        });
        clDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        clLupaKataSandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(String email, String pass) {
        pd.show();

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        checkJenisUser(auth.getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, "Username dan password yang anda masukkan salah", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkJenisUser(final String userId) {
        if (userId.equals(OpMainActivity.ADMIN_USER_ID)) {
            //admin

            SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(JENIS_USER_PREFS, JENIS_USER_ADMIN);
            editor.putString(USERID_PREFS, userId);
            editor.apply();

            pd.dismiss();
            Intent intent = new Intent(LoginActivity.this, OpMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            //user

            SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(JENIS_USER_PREFS, JENIS_USER_USER);
            editor.putString(USERID_PREFS, userId);
            editor.putString(EMAIL_PREFS, edtEmail.getText().toString());
            editor.apply();

            pd.dismiss();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
