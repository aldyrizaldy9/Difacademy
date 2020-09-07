package com.tamanpelajar.aldy.difacademy.Activity;

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

import com.tamanpelajar.aldy.difacademy.Model.OngoingMateriModel;
import com.tamanpelajar.aldy.difacademy.Model.PaymentModel;
import com.tamanpelajar.aldy.difacademy.Model.UserModel;
import com.tamanpelajar.aldy.difacademy.R;
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
    private static final String TAG = "MANTAP";
    private TextView tvNavBar, tvNama, tvEmail, tvNoWa, tvNamaMateri, tvHargaMateri, tvNamaBank;
    private ConstraintLayout clBack;
    private ImageView imgBack;
    private Button btnBukaMateri;
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
        tvNamaMateri = findViewById(R.id.tv_op_notif_pay_nama_materi);
        tvHargaMateri = findViewById(R.id.tv_op_notif_pay_harga_materi);
        tvNamaBank = findViewById(R.id.tv_op_notif_pay_nama_bank);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        btnBukaMateri = findViewById(R.id.btn_op_notif_pay_buka_materi);
        progressDialog = new ProgressDialog(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setViewWithParcelable() {
        Intent intent = getIntent();
        paymentModel = intent.getParcelableExtra("paymentModel");
        tvNama.setText(paymentModel.getNamaUser());
        tvEmail.setText(paymentModel.getEmail());
        tvNoWa.setText(paymentModel.getNoWa());
        tvNamaMateri.setText(paymentModel.getNamaMateri());
        String harga = "Rp " + paymentModel.getHargaMateri();
        tvHargaMateri.setText(harga);
        tvNamaBank.setText(paymentModel.getNamaBank());
        if (paymentModel.isPaid()) {
            //Kalo sudah bayar tombol buka kelas jadi disabled
            btnBukaMateri.setEnabled(false);
        }
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnBukaMateri.setOnClickListener(new View.OnClickListener() {
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
                        .subject("Pembayaran Materi Taman Pelajar")
                        .build();
                startActivity(intent);
            }
        });
        tvNoWa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String urlNew = paymentModel.getNoWa();
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
        builder.setMessage("Apakah anda yakin ingin membuka materi ini?");
        builder.setTitle("Buka materi");
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
                .whereEqualTo("userId", paymentModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "get userdoc id success");
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            userModel = queryDocumentSnapshot.toObject(UserModel.class);
                            userModel.setUserDocId(queryDocumentSnapshot.getId());
                        }
                        bukaMateri();
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

    private void bukaMateri() {
        long dateCreated = Timestamp.now().getSeconds();
        CollectionReference onGoingRef;
        if (paymentModel.getJenisKelas().equalsIgnoreCase("online")) {
            onGoingRef = firebaseFirestore
                    .collection("User")
                    .document(userModel.getUserDocId())
                    .collection("OngoingOnlineMateri");
        } else {
            onGoingRef = firebaseFirestore
                    .collection("User")
                    .document(userModel.getUserDocId())
                    .collection("OngoingBlendedMateri");
        }
        OngoingMateriModel ongoingMateriModel = new OngoingMateriModel(paymentModel.getCourseId(), paymentModel.getMateriId(), dateCreated);

        onGoingRef
                .add(ongoingMateriModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "buka materi success");
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
                .whereEqualTo("materiId", paymentModel.getMateriId())
                .whereEqualTo("userId", paymentModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            setPaid(queryDocumentSnapshot);
                        }
                        Log.d(TAG, "get payment doc sucess");
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
                        Log.d(TAG, "set paid success");
                        progressDialog.dismiss();
                        Toast.makeText(OpNotifPaymentActivity.this,
                                "Materi telah dibuka untuk user "
                                        + paymentModel.getNamaUser(), Toast.LENGTH_SHORT).show();
//                        onBackPressed();
                        finish();
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

    @Override
    protected void onStop() {
        super.onStop();
    }
}
