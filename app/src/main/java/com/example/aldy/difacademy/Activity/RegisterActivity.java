package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.Model.UserModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText edtNama, edtEmail, edtWa, edtSandi, edtKonfSandi;
    private ImageView imgDaftar;
    private ConstraintLayout clMasuk;

    FirebaseAuth auth = FirebaseAuth.getInstance();;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("User");

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pd = new ProgressDialog(RegisterActivity.this);

        initView();
        onClick();
    }

    private void initView() {
        edtNama = findViewById(R.id.edt_register_nama);
        edtEmail = findViewById(R.id.edt_register_email);
        edtWa = findViewById(R.id.edt_register_wa);
        edtSandi = findViewById(R.id.edt_register_kata_sandi);
        edtKonfSandi = findViewById(R.id.edt_register_konf_kata_sandi);
        imgDaftar = findViewById(R.id.img_register_tombol_daftar);
        clMasuk = findViewById(R.id.cl_register_masuk);
    }

    private void onClick() {
        imgDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkText()){
                    pd.setCancelable(false);
                    pd.setMessage("Memuat...");
                    pd.show();
                    String nama = edtNama.getText().toString();
                    String email = edtEmail.getText().toString();
                    String wa = edtWa.getText().toString();
                    String sandi = edtSandi.getText().toString();
                    register(nama, email, wa, sandi);
                }
            }
        });
        clMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkText(){
        if (edtNama.getText().length() == 0 ||
                edtSandi.getText().length() == 0 ||
                edtEmail.getText().length() == 0 ||
                edtWa.getText().length() == 0
            ){
            Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtWa.getText().length() < 10){
            Toast.makeText(this, "Nomor telepon minimal 10 karakter", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtSandi.getText().length() < 8){
            Toast.makeText(this, "Kata sandi minimal 8 karakter", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!edtSandi.getText().toString().equals(edtKonfSandi.getText().toString())){
            Toast.makeText(this, "Konfirmasi kata sandi tidak sesuai", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!checkEmail(edtEmail.getText().toString())){
            Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkEmail(String email){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void register(final String nama, final String email, final String wa, String sandi){
        auth.createUserWithEmailAndPassword(email, sandi)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser == null;

                            String userId = firebaseUser.getUid();
                            UserModel userModel = new UserModel(userId, nama, email, wa);
                            userRef.add(userModel)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            auth.signOut();
                                            pd.dismiss();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
