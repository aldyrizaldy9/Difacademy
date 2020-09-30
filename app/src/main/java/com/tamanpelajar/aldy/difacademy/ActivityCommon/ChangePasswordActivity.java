package com.tamanpelajar.aldy.difacademy.ActivityCommon;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.R;

import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.EMAIL_PREFS;
import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.SHARE_PREFS;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";
    private ConstraintLayout clBack, clSave, clNavbar;
    private ImageView imgBack, imgSave;
    private TextView tvNavBar;
    private EditText edtOldPass, edtNewPass, edtNewPassConfirm;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
        onClick();
    }

    private void initView() {
        clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navKuning));
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clSave = findViewById(R.id.cl_icon3);
        clSave.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        imgSave = findViewById(R.id.img_icon3);
        imgSave.setImageResource(R.drawable.ic_check);
        tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Ganti Kata Sandi");
        edtOldPass = findViewById(R.id.edt_change_password_old_pass);
        edtNewPass = findViewById(R.id.edt_change_password_new_pass);
        edtNewPassConfirm = findViewById(R.id.edt_change_password_new_pass_confirm);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konfirmasiSimpan();
            }
        });
    }

    private void updatePassword() {
        progressDialog.setMessage("Menyimpan");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String email = sharedPreferences.getString(EMAIL_PREFS, "");
        if (email != null) {
            firebaseAuth
                    .signInWithEmailAndPassword(email, edtOldPass.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            user = authResult.getUser();
                            if (user != null) {
                                user
                                        .updatePassword(edtNewPass.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(ChangePasswordActivity.this, "Data telah disimpan", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChangePasswordActivity.this, "Password lama salah", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }


    private void konfirmasiSimpan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menyimpan?");
        builder.setTitle("Simpan");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!CommonMethod.isInternetAvailable(ChangePasswordActivity.this)) {
                    return;
                }

                if (!edtNewPass.getText().toString().equals(edtNewPassConfirm.getText().toString())) {
                    Toast.makeText(ChangePasswordActivity.this, "Kata sandi baru tidak cocok", Toast.LENGTH_SHORT).show();
                } else if (edtNewPass.length() < 8) {
                    Toast.makeText(ChangePasswordActivity.this, "Kata sandi baru minimal 8 karakter", Toast.LENGTH_SHORT).show();
                } else {
                    updatePassword();
                }
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
