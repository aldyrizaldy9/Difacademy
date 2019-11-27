package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.Model.GraduationModel;
import com.example.aldy.difacademy.Model.UserModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class OpNotifGradActivity extends AppCompatActivity {
    private TextView tvNavBar, tvNama, tvEmail, tvNoWa, tvNamaKelas;
    private ConstraintLayout clBack;
    private ImageView imgBack;
    private Button btnHubungi, btnTandai;
    private GraduationModel graduationModel;
    private ProgressDialog progressDialog;
    private UserModel userModel;

    private FirebaseFirestore firebaseFirestore;

    private static final String TAG = "OpNotifPaymentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_notif_grad);
        initView();
        setViewWithParcelable();
        onClick();
        setSeen();
    }

    private void initView() {
        tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Kelulusan");
        tvNama = findViewById(R.id.tv_op_notif_grad_nama);
        tvEmail = findViewById(R.id.tv_op_notif_grad_email);
        tvNoWa = findViewById(R.id.tv_op_notif_grad_nowa);
        tvNamaKelas = findViewById(R.id.tv_op_notif_grad_nama_kelas);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        btnHubungi = findViewById(R.id.btn_op_notif_grad_hubungi);
        btnTandai = findViewById(R.id.btn_op_notif_grad_tandai);
        progressDialog = new ProgressDialog(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setViewWithParcelable() {
        Intent intent = getIntent();
        graduationModel = intent.getParcelableExtra("graduationModel");
        tvNama.setText(graduationModel.getNamaUser());
        tvEmail.setText(graduationModel.getEmail());
        tvNoWa.setText(graduationModel.getNoWa());
        tvNamaKelas.setText(graduationModel.getNamaKelas());
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnHubungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String urlNew = "http://wa.me/" + "0" + graduationModel.getNoWa().substring(1);
                intent.setData(Uri.parse(urlNew));
                startActivity(intent);
            }
        });
        btnTandai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konfirmasiTandai();
            }
        });
    }

    private void setSeen() {
        DocumentReference graduationRef = firebaseFirestore.collection("Graduation").document(graduationModel.getGraduationId());
        graduationRef
                .update("seen", true)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void konfirmasiTandai() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin mendandai sudah dihubungi?");
        builder.setTitle("Tandai");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                getGraduationDocuments();
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

    private void getGraduationDocuments() {
        CollectionReference gradRef = firebaseFirestore.collection("Graduation");
        gradRef
                .whereEqualTo("blendedCourseId", graduationModel.getBlendedCourseId())
                .whereEqualTo("userId", graduationModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            setDone(queryDocumentSnapshot);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });

    }

    private void setDone(QueryDocumentSnapshot queryDocumentSnapshot) {
        DocumentReference gradRef = firebaseFirestore
                .collection("Graduation")
                .document(queryDocumentSnapshot.getId());
        Map<String, Object> grad = new HashMap<>();
        grad.put("done", true);
        grad.put("seen", true);
        gradRef
                .update(grad)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        onBackPressed();
                        Toast.makeText(OpNotifGradActivity.this,
                                "Kelulusan user "
                                        + graduationModel.getNamaUser()
                                        + " sudah ditandai", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });

    }

}
