package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
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

import com.example.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.example.aldy.difacademy.Model.PaymentModel;
import com.example.aldy.difacademy.Model.UserModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import de.cketti.mailto.EmailIntentBuilder;

public class OpNotifPaymentActivity extends AppCompatActivity {
    private static final String TAG = "OpNotifPaymentActivity";
    private TextView tvNavBar, tvNama, tvEmail, tvNoWa, tvNamaKelas, tvHargaKelas, tvNamaBank;
    private ConstraintLayout clBack;
    private ImageView imgBack;
    private Button btnBukaKelas;
    private PaymentModel paymentModel;
    private ProgressDialog progressDialog;
    private UserModel userModel;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_notif_payment);
        initView();
        setViewWithParcelable();
        onClick();
        setSeen();
    }

    private void initView() {
        tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Pembelian");
        tvNama = findViewById(R.id.tv_op_notif_pay_nama);
        tvEmail = findViewById(R.id.tv_op_notif_pay_email);
        tvNoWa = findViewById(R.id.tv_op_notif_pay_nowa);
        tvNamaKelas = findViewById(R.id.tv_op_notif_pay_nama_kelas);
        tvHargaKelas = findViewById(R.id.tv_op_notif_pay_harga_kelas);
        tvNamaBank = findViewById(R.id.tv_op_notif_pay_nama_bank);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        btnBukaKelas = findViewById(R.id.btn_op_notif_pay_buka_kelas);
        progressDialog = new ProgressDialog(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setViewWithParcelable() {
        Intent intent = getIntent();
        paymentModel = intent.getParcelableExtra("paymentModel");
        tvNama.setText(paymentModel.getNamaUser());
        tvEmail.setText(paymentModel.getEmail());
        tvNoWa.setText(paymentModel.getNoWa());
        tvNamaKelas.setText(paymentModel.getNamaKelas());
        String harga = "Rp" + paymentModel.getHargaKelas();
        tvHargaKelas.setText(harga);
        tvNamaBank.setText(paymentModel.getNamaBank());
        if (paymentModel.isPaid()) {
            //Kalo sudah bayar tombol buka kelas jadi disabled
            btnBukaKelas.setEnabled(false);
        }
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnBukaKelas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konfirmasiBukaKelas();
            }
        });
        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = EmailIntentBuilder.from(OpNotifPaymentActivity.this)
                        .to(paymentModel.getEmail())
                        .subject("Pembayaran Course Taman Pelajar")
                        .build();
                startActivity(intent);
            }
        });
        tvNoWa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String urlNew = "http://wa.me/" + "+62" + paymentModel.getNoWa().substring(1);
                intent.setData(Uri.parse(urlNew));
                startActivity(intent);
            }
        });
    }

    private void setSeen() {
        DocumentReference paymentRef = firebaseFirestore.collection("Payment").document(paymentModel.getPaymentId());
        paymentRef
                .update("seen", true)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void konfirmasiBukaKelas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin membuka course ini?");
        builder.setTitle("Buka kelas");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isNetworkConnected()) {
                    Toast.makeText(OpNotifPaymentActivity.this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
                } else {
                    getUserDocId();
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

    private void getUserDocId() {
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        CollectionReference userRef = firebaseFirestore.collection("User");
        userRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            userModel = queryDocumentSnapshot.toObject(UserModel.class);
                            userModel.setUserDocId(queryDocumentSnapshot.getId());
                        }
                        bukaKelas();
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

    private void bukaKelas() {
        CollectionReference onGoingRef = firebaseFirestore
                .collection("User")
                .document(userModel.getUserDocId())
                .collection("OngoingBlendedCourse");

        long dateCreated = Timestamp.now().getSeconds();
        OngoingKelasBlendedModel ongoingKelasBlendedModel = new OngoingKelasBlendedModel(paymentModel.getBlendedCourseId(), dateCreated);

        onGoingRef
                .add(ongoingKelasBlendedModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        getPaymentDocuments();
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

    private void getPaymentDocuments() {
        CollectionReference paymentRef = firebaseFirestore.collection("Payment");
        paymentRef
                .whereEqualTo("blendedCourseId", paymentModel.getBlendedCourseId())
                .whereEqualTo("userId", paymentModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            setPaid(queryDocumentSnapshot);
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

    private void setPaid(QueryDocumentSnapshot queryDocumentSnapshot) {
        DocumentReference paymentRef = firebaseFirestore
                .collection("Payment")
                .document(queryDocumentSnapshot.getId());
        Map<String, Object> payment = new HashMap<>();
        payment.put("paid", true);
        payment.put("seen", true);
        paymentRef
                .update(payment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        onBackPressed();
                        Toast.makeText(OpNotifPaymentActivity.this,
                                "Kelas telah dibuka untuk user "
                                        + paymentModel.getNamaUser(), Toast.LENGTH_SHORT).show();
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
