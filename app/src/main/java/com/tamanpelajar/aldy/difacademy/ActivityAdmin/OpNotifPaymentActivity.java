package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.PaymentModel;
import com.tamanpelajar.aldy.difacademy.Model.UserModel;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import de.cketti.mailto.EmailIntentBuilder;

public class OpNotifPaymentActivity extends AppCompatActivity {
    private TextView tvNavBar, tvNama, tvEmail, tvNoWa, tvNamaKelas, tvHargaKelas, tvNamaBank;
    private ConstraintLayout clBack;
    private ImageView imgBack;
    private Button btnBukaKelas;
    private PaymentModel paymentModel;
    private ProgressDialog pd;
    private UserModel userModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ongoingKelasRef, paymentRef;

    private String jenisKelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_notif_payment);
        initView();
        checkIntent();
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

        pd = new ProgressDialog(this);
        pd.setMessage("Memproses...");
        pd.setCancelable(false);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        paymentModel = intent.getParcelableExtra(CommonMethod.intentPaymentModel);
        if (paymentModel != null) {
            tvNama.setText(paymentModel.getNamaUser());
            tvEmail.setText(paymentModel.getEmail());
            tvNoWa.setText(paymentModel.getNoWa());
            tvNamaKelas.setText(paymentModel.getNamaKelas());
            String harga = "Rp " + paymentModel.getHargaKelas();
            tvHargaKelas.setText(harga);
            tvNamaBank.setText(paymentModel.getNamaBank());
            if (paymentModel.isPaid()) {
                //Kalo sudah bayar tombol buka kelas jadi disabled
                btnBukaKelas.setEnabled(false);
                btnBukaKelas.setText("sudah dibuka");
            }

            jenisKelas = intent.getStringExtra(CommonMethod.intentJenisKelas);

            if (jenisKelas.equals("blended")) {
                paymentRef = db.collection(CommonMethod.refPaymentKelasBlended);
            } else {
                paymentRef = db.collection(CommonMethod.refPaymentKelasOnline);
            }

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
                        .subject("Pembayaran Kelas Taman Pelajar")
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
        DocumentReference docRef = paymentRef.document(paymentModel.getDocumentId());
        docRef.update("seen", true);
    }

    private void konfirmasiBukaKelas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin membuka materi ini?");
        builder.setTitle("Buka materi");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!CommonMethod.isInternetAvailable(OpNotifPaymentActivity.this)) {
                    return;
                }

                pd.show();
                getUserDocId();
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
        CollectionReference userRef = db.collection(CommonMethod.refUser);
        userRef
                .whereEqualTo(CommonMethod.fieldUserId, paymentModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            userModel = queryDocumentSnapshot.toObject(UserModel.class);
                            userModel.setDocumentId(queryDocumentSnapshot.getId());
                        }
                        bukaKelas();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpNotifPaymentActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void bukaKelas() {
        long dateCreated = CommonMethod.getTimeStamp();

        OngoingKelasBlendedModel ongoingKelasBlendedModel = new OngoingKelasBlendedModel(paymentModel.getKelasId(), dateCreated);

        if (jenisKelas.equals("blended")){
            ongoingKelasRef = db.collection(CommonMethod.refUser)
                    .document(userModel.getDocumentId())
                    .collection(CommonMethod.refOngoingKelasBlended);
        } else {
            ongoingKelasRef = db.collection(CommonMethod.refUser)
                    .document(userModel.getDocumentId())
                    .collection(CommonMethod.refOngoingKelasBlended);
        }

        ongoingKelasRef
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
                        pd.dismiss();
                        Toast.makeText(OpNotifPaymentActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getPaymentDocuments() {
        paymentRef
                .whereEqualTo(CommonMethod.fieldKelasId, paymentModel.getKelasId())
                .whereEqualTo(CommonMethod.fieldUserId, paymentModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            setPaid(queryDocumentSnapshot.getId());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpNotifPaymentActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setPaid(String docId) {
        DocumentReference docRef = paymentRef
                .document(docId);

        Map<String, Object> payment = new HashMap<>();
        payment.put("paid", true);
        payment.put("seen", true);
        docRef
                .update(payment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(OpNotifPaymentActivity.this,
                                "Materi telah dibuka untuk user "
                                        + paymentModel.getNamaUser(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpNotifPaymentActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
