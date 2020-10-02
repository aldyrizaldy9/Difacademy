package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.GraduationMateriBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.HashMap;
import java.util.Map;

import de.cketti.mailto.EmailIntentBuilder;

import static com.tamanpelajar.aldy.difacademy.Fragment.OpGraduationBlendedFragment.isGradBlendedChanged;

public class OpNotifGradMateriBlendedActivity extends AppCompatActivity {
    private static final String TAG = "OpNotifGradMateriBlende";
    private TextView tvNavBar, tvNama, tvEmail, tvNoWa, tvNamaMateri, tvTulisanLulus;
    private ConstraintLayout clBack;
    private ImageView imgBack;
    private Button btnTandai;
    private GraduationMateriBlendedModel graduationMateriBlendedModel;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_notif_grad);
        isGradBlendedChanged = true;
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
        tvTulisanLulus = findViewById(R.id.tv_op_notif_grad_tulisan_lulus);
        tvNamaMateri = findViewById(R.id.tv_op_notif_grad_nama_kelas);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        btnTandai = findViewById(R.id.btn_op_notif_grad_tandai);
        progressDialog = new ProgressDialog(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setViewWithParcelable() {
        Intent intent = getIntent();
        graduationMateriBlendedModel = intent.getParcelableExtra(CommonMethod.intentGraduationModel);
        tvNama.setText(graduationMateriBlendedModel.getNamaUser());
        tvEmail.setText(graduationMateriBlendedModel.getEmail());
        tvNoWa.setText(graduationMateriBlendedModel.getNoWa());
        tvNamaMateri.setText(graduationMateriBlendedModel.getNamaMateri());
        tvTulisanLulus.setText("Materi yang lulus");
        if (graduationMateriBlendedModel.isDone()) {
            btnTandai.setEnabled(false);
        }
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnTandai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konfirmasiTandai();
            }
        });
        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = EmailIntentBuilder.from(OpNotifGradMateriBlendedActivity.this)
                        .to(graduationMateriBlendedModel.getEmail())
                        .subject("Kelulusan Course Taman Pelajar")
                        .build();
                startActivity(intent);
            }
        });
        tvNoWa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String urlNew = graduationMateriBlendedModel.getNoWa();
                if (urlNew.substring(0, 3).equals("+62")) {
                    urlNew = "http://wa.me/" + urlNew;
                } else {
                    urlNew = "http://wa.me/+62" + urlNew.substring(1);
                }
                intent.setData(Uri.parse(urlNew));
                startActivity(intent);
            }
        });
    }

    private void setSeen() {
        DocumentReference graduationRef = firebaseFirestore.collection(CommonMethod.refGraduationBlended).document(graduationMateriBlendedModel.getDocumentId());
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
                if (!CommonMethod.isInternetAvailable(OpNotifGradMateriBlendedActivity.this)) {
                    return;
                }

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
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        CollectionReference gradRef = firebaseFirestore.collection(CommonMethod.refGraduationBlended);
        gradRef
                .whereEqualTo("materiId", graduationMateriBlendedModel.getMateriId())
                .whereEqualTo("userId", graduationMateriBlendedModel.getUserId())
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
                .collection(CommonMethod.refGraduationBlended)
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
                        Toast.makeText(OpNotifGradMateriBlendedActivity.this,
                                "Kelulusan user "
                                        + graduationMateriBlendedModel.getNamaUser()
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
