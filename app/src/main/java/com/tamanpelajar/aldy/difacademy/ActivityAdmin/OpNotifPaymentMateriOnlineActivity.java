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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.OngoingMateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.PaymentMateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.UserModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.HashMap;
import java.util.Map;

import de.cketti.mailto.EmailIntentBuilder;

public class OpNotifPaymentMateriOnlineActivity extends AppCompatActivity {
    private TextView tvNavBar, tvNama, tvEmail, tvNoWa, tvNamaMateri, tvHargaMateri, tvNamaBank, tvTulisanNamaMateri, tvTulisanHargaMateri;
    private ConstraintLayout clBack;
    private ImageView imgBack;
    private Button btnBukaMateri;
    private PaymentMateriOnlineModel paymentMateriOnlineModel;
    private ProgressDialog pd;
    private UserModel userModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ongoingMateriRef, paymentRef;

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
        tvNamaMateri = findViewById(R.id.tv_op_notif_pay_nama_kelas);
        tvHargaMateri = findViewById(R.id.tv_op_notif_pay_harga_kelas);
        tvTulisanHargaMateri = findViewById(R.id.tv_op_notif_pay_tulisan_harga_kelas);
        tvTulisanHargaMateri.setText("Harga materi");
        tvTulisanNamaMateri = findViewById(R.id.tv_op_notif_pay_tulisan_nama_kelas);
        tvTulisanNamaMateri.setText("Materi yang ingin dibeli");
        tvNamaBank = findViewById(R.id.tv_op_notif_pay_nama_bank);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        btnBukaMateri = findViewById(R.id.btn_op_notif_pay_buka_kelas);

        pd = new ProgressDialog(this);
        pd.setMessage("Memproses...");
        pd.setCancelable(false);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        paymentMateriOnlineModel = intent.getParcelableExtra(CommonMethod.intentPaymentModel);
        if (paymentMateriOnlineModel != null) {
            tvNama.setText(paymentMateriOnlineModel.getNamaUser());
            tvEmail.setText(paymentMateriOnlineModel.getEmail());
            tvNoWa.setText(paymentMateriOnlineModel.getNoWa());
            tvNamaMateri.setText(paymentMateriOnlineModel.getNamaMateri());
            String harga = "Rp " + paymentMateriOnlineModel.getHargaMateri();
            tvHargaMateri.setText(harga);
            tvNamaBank.setText(paymentMateriOnlineModel.getNamaBank());
            if (paymentMateriOnlineModel.isPaid()) {
                //Kalo sudah bayar tombol buka materi jadi disabled
                btnBukaMateri.setEnabled(false);
                btnBukaMateri.setText("sudah dibuka");
            }
            paymentRef = db.collection(CommonMethod.refPaymentMateriOnline);
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
                konfirmasiBukaMateri();
            }
        });
        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = EmailIntentBuilder.from(OpNotifPaymentMateriOnlineActivity.this)
                        .to(paymentMateriOnlineModel.getEmail())
                        .subject("Pembayaran Materi Taman Pelajar")
                        .build();
                startActivity(intent);
            }
        });
        tvNoWa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String urlNew = paymentMateriOnlineModel.getNoWa();
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
        DocumentReference docRef = paymentRef.document(paymentMateriOnlineModel.getDocumentId());
        docRef.update("seen", true);
    }

    private void konfirmasiBukaMateri() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin membuka materi ini?");
        builder.setTitle("Buka materi");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!CommonMethod.isInternetAvailable(OpNotifPaymentMateriOnlineActivity.this)) {
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
                .whereEqualTo(CommonMethod.fieldUserId, paymentMateriOnlineModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            userModel = queryDocumentSnapshot.toObject(UserModel.class);
                            userModel.setDocumentId(queryDocumentSnapshot.getId());
                        }
                        bukaMateri();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpNotifPaymentMateriOnlineActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void bukaMateri() {
        long dateCreated = CommonMethod.getTimeStamp();

        OngoingMateriOnlineModel ongoingMateriOnlineModel = new OngoingMateriOnlineModel(paymentMateriOnlineModel.getKelasId(), paymentMateriOnlineModel.getMateriId(), dateCreated);

        ongoingMateriRef = db
                .collection(CommonMethod.refUser)
                .document(userModel.getDocumentId())
                .collection(CommonMethod.refOngoingMateriOnline);

        ongoingMateriRef
                .add(ongoingMateriOnlineModel)
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
                        Toast.makeText(OpNotifPaymentMateriOnlineActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getPaymentDocuments() {
        paymentRef
                .whereEqualTo(CommonMethod.fieldMateriId, paymentMateriOnlineModel.getMateriId())
                .whereEqualTo(CommonMethod.fieldUserId, paymentMateriOnlineModel.getUserId())
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
                        Toast.makeText(OpNotifPaymentMateriOnlineActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(OpNotifPaymentMateriOnlineActivity.this,
                                "Materi telah dibuka untuk user "
                                        + paymentMateriOnlineModel.getNamaUser(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpNotifPaymentMateriOnlineActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
