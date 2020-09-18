package com.tamanpelajar.aldy.difacademy.ActivityUser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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

import com.tamanpelajar.aldy.difacademy.Model.UserModel;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.SHARE_PREFS;
import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.USERID_PREFS;

public class UsEditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private ConstraintLayout clBack, clSave, clNavbar;
    private EditText edtNama, edtNoWa;
    private ProgressDialog progressDialog;
    private CollectionReference userRef;
    private DocumentReference userDocRef;
    private UserModel userModel;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initView();
        onClick();
        getUserData();
    }

    private void initView() {
        clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navKuning));
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clSave = findViewById(R.id.cl_icon3);
        clSave.setVisibility(View.VISIBLE);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        ImageView imgSave = findViewById(R.id.img_icon3);
        imgSave.setImageResource(R.drawable.ic_check);
        TextView tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Sunting Profil");
        edtNama = findViewById(R.id.edt_edit_profile_nama);
        edtNoWa = findViewById(R.id.edt_edit_profile_nowa);
        progressDialog = new ProgressDialog(this);
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

    private void getUserData() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String userId = sharedPreferences.getString(USERID_PREFS, "");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        userRef = firebaseFirestore.collection("User");
        userRef
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            userModel = queryDocumentSnapshot.toObject(UserModel.class);
                            userModel.setUserDocId(queryDocumentSnapshot.getId());
                        }
                        edtNama.setText(userModel.getNama());
                        edtNoWa.setText(userModel.getNoTelp());
                        userDocRef = userRef.document(userModel.getUserDocId());
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void updateProfile() {
        progressDialog.setMessage("Menyimpan");
        progressDialog.setCancelable(false);
        progressDialog.show();
        UserModel userModel = new UserModel(this.userModel.getUserId(), edtNama.getText().toString(), this.userModel.getEmail(), edtNoWa.getText().toString());
        userDocRef
                .set(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(UsEditProfileActivity.this, "Data telah disimpan", Toast.LENGTH_SHORT).show();
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


    private void konfirmasiSimpan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menyimpan?");
        builder.setTitle("Simpan");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    Toast.makeText(UsEditProfileActivity.this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
                } else {
                    if (edtNama.length() == 0 || edtNoWa.length() == 0) {
                        Toast.makeText(UsEditProfileActivity.this, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    } else if (edtNoWa.length() < 8) {
                        Toast.makeText(UsEditProfileActivity.this, "Nomor Whatsapp minimal 8 digit", Toast.LENGTH_SHORT).show();
                    } else {
                        updateProfile();
                    }
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
