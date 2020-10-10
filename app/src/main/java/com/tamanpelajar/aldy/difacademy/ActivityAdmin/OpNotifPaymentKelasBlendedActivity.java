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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.AnggotaKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.PaymentKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.UserModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.HashMap;
import java.util.Map;

import de.cketti.mailto.EmailIntentBuilder;

import static com.tamanpelajar.aldy.difacademy.Fragment.OpPaymentBlendedFragment.isPaymentBlendedChanged;

public class OpNotifPaymentKelasBlendedActivity extends AppCompatActivity {
    private static final String TAG = "ganteng";
    private TextView tvNavBar, tvNama, tvEmail, tvNoWa, tvNamaKelas, tvHargaKelas, tvNamaBank;
    private ConstraintLayout clBack;
    private ImageView imgBack;
    private Button btnBukaKelas;
    private PaymentKelasBlendedModel paymentKelasBlendedModel;
    private ProgressDialog pd;
    private UserModel userModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ongoingKelasRef, paymentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_notif_payment);
        isPaymentBlendedChanged = true;
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
    }

    private void checkIntent() {
        Intent intent = getIntent();
        paymentKelasBlendedModel = intent.getParcelableExtra(CommonMethod.intentPaymentModel);
        if (paymentKelasBlendedModel != null) {
            tvNama.setText(paymentKelasBlendedModel.getNamaUser());
            tvEmail.setText(paymentKelasBlendedModel.getEmail());
            tvNoWa.setText(paymentKelasBlendedModel.getNoWa());
            tvNamaKelas.setText(paymentKelasBlendedModel.getNamaKelas());
            String harga = "Rp " + paymentKelasBlendedModel.getHargaKelas();
            tvHargaKelas.setText(harga);
            tvNamaBank.setText(paymentKelasBlendedModel.getNamaBank());
            if (paymentKelasBlendedModel.isPaid()) {
                btnBukaKelas.setVisibility(View.GONE);
            }
            paymentRef = db.collection(CommonMethod.refPaymentKelasBlended);
            checkIfKelasExist(paymentKelasBlendedModel.getKelasId());
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
                Intent intent = EmailIntentBuilder.from(OpNotifPaymentKelasBlendedActivity.this)
                        .to(paymentKelasBlendedModel.getEmail())
                        .subject("Pembayaran Kelas Taman Pelajar")
                        .build();
                startActivity(intent);
            }
        });
        tvNoWa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String urlNew = paymentKelasBlendedModel.getNoWa();
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
        DocumentReference docRef = paymentRef.document(paymentKelasBlendedModel.getDocumentId());
        docRef.update("seen", true);
    }

    private void konfirmasiBukaKelas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin membuka kelas ini?");
        builder.setTitle("Buka kelas");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CommonMethod.isInternetAvailable(OpNotifPaymentKelasBlendedActivity.this)) {
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
        pd.show();
        pd.setMessage("Memproses...");
        pd.setCancelable(false);
        CollectionReference userRef = db.collection(CommonMethod.refUser);
        userRef
                .whereEqualTo(CommonMethod.fieldUserId, paymentKelasBlendedModel.getUserId())
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
                        Toast.makeText(OpNotifPaymentKelasBlendedActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void bukaKelas() {
        long dateCreated = CommonMethod.getTimeStamp();

        OngoingKelasBlendedModel ongoingKelasBlendedModel = new OngoingKelasBlendedModel(paymentKelasBlendedModel.getKelasId(), dateCreated);

        ongoingKelasRef = db
                .collection(CommonMethod.refUser)
                .document(userModel.getDocumentId())
                .collection(CommonMethod.refOngoingKelasBlended);

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
                        Toast.makeText(OpNotifPaymentKelasBlendedActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getPaymentDocuments() {
        paymentRef
                .whereEqualTo(CommonMethod.fieldKelasId, paymentKelasBlendedModel.getKelasId())
                .whereEqualTo(CommonMethod.fieldUserId, paymentKelasBlendedModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        boolean exist = false;

                        if (queryDocumentSnapshots.size() > 0){
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                exist = true;
                                setPaid(queryDocumentSnapshot.getId());
                            }
                        } else {
                            Toast.makeText(OpNotifPaymentKelasBlendedActivity.this, "Maaf, kelas " + paymentKelasBlendedModel.getNamaKelas() + " sudah dihapus", Toast.LENGTH_SHORT).show();
                            btnBukaKelas.setVisibility(View.GONE);
                        }
                        setUserAsAnggotaKelas();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpNotifPaymentKelasBlendedActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpNotifPaymentKelasBlendedActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUserAsAnggotaKelas() {
        CollectionReference anggotaKelasRef = db
                .collection(CommonMethod.refKelasBlended)
                .document(paymentKelasBlendedModel.getKelasId())
                .collection(CommonMethod.refAnggota);

        AnggotaKelasBlendedModel anggotaKelasBlendedModel = new AnggotaKelasBlendedModel(
                paymentKelasBlendedModel.getNamaUser(),
                paymentKelasBlendedModel.getUserId(),
                paymentKelasBlendedModel.getKelasId(),
                paymentKelasBlendedModel.getDateCreated());

        anggotaKelasRef.add(anggotaKelasBlendedModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        pd.dismiss();
                        Toast.makeText(OpNotifPaymentKelasBlendedActivity.this,
                                "Materi telah dibuka untuk user "
                                        + paymentKelasBlendedModel.getNamaUser(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpNotifPaymentKelasBlendedActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfKelasExist(String kelasId) {
        pd.setMessage("Memuat...");
        pd.setCancelable(false);
        pd.show();

        DocumentReference kelasBlendedRef = db
                .collection(CommonMethod.refKelasBlended)
                .document(kelasId);

        kelasBlendedRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            btnBukaKelas.setVisibility(View.GONE);
                        }
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }
}
