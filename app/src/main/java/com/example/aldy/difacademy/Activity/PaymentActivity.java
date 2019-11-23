package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.aldy.difacademy.Model.PaymentModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.aldy.difacademy.Activity.LoginActivity.SHARE_PREFS;
import static com.example.aldy.difacademy.Activity.LoginActivity.USERID_PREFS;

public class PaymentActivity extends AppCompatActivity {
    private ConstraintLayout clBack, clContainerBni, clContainerBri, clExpandBni, clExpandBri;
    private ImageView imgBack, imgLogoBni, imgLogoBri, imgExpandBni, imgExpandBri;
    private TextView tvNavBar, tvTataCaraBni, tvTataCaraBri;
    private Button btnBayarBni, btnBayarBri;
    private boolean isBniActive = false, isBriActive = false;
    private String blendedCourseId;
    private ProgressDialog progressDialog;

    private SharedPreferences sharedPreferences;

    View.OnClickListener bniClickListener;
    View.OnClickListener briClickListener;

    private FirebaseFirestore firebaseFirestore;

    private static final String TAG = "PaymentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initView();
        createOnClick();
        onClick();
    }

    private void initView() {
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clContainerBni = findViewById(R.id.cl_payment_container_bni);
        clContainerBri = findViewById(R.id.cl_payment_container_bri);
        clExpandBni = findViewById(R.id.cl_payment_expand_bni);
        clExpandBri = findViewById(R.id.cl_payment_expand_bri);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        imgLogoBni = findViewById(R.id.img_payment_logo_bni);
//        imgLogoBni.setImageResource();
        imgLogoBri = findViewById(R.id.img_payment_logo_bri);
//        imgLogoBri.setImageResource();
        imgExpandBni = findViewById(R.id.img_payment_expand_bni);
        imgExpandBni.setImageResource(R.drawable.ic_expand_more);
        imgExpandBri = findViewById(R.id.img_payment_expand_bri);
        imgExpandBri.setImageResource(R.drawable.ic_expand_more);
        tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Cara Pembayaran");
        tvTataCaraBni = findViewById(R.id.tv_payment_tata_cara_bni);
        tvTataCaraBri = findViewById(R.id.tv_payment_tata_cara_bri);
        btnBayarBni = findViewById(R.id.btn_payment_bayar_bni);
        btnBayarBri = findViewById(R.id.btn_payment_bayar_bri);
        Intent intent = getIntent();
        blendedCourseId = intent.getStringExtra("blendedCourseId");
        sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
    }

    private void createOnClick() {
        bniClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility("bni", isBniActive, imgLogoBni, imgExpandBni, tvTataCaraBni, btnBayarBni);
            }
        };
        briClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility("bri", isBriActive, imgLogoBri, imgExpandBri, tvTataCaraBri, btnBayarBri);
            }
        };

    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clContainerBni.setOnClickListener(bniClickListener);
        clContainerBri.setOnClickListener(briClickListener);
        clExpandBni.setOnClickListener(bniClickListener);
        clExpandBri.setOnClickListener(briClickListener);
        btnBayarBni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konfirmasiPembayaran("BNI");
            }
        });

        btnBayarBri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konfirmasiPembayaran("BRI");
            }
        });
    }

    private void setVisibility(String bank, Boolean isActive, ImageView imgLogo, ImageView imgExpand, TextView tvTataCara, Button btnBayar) {
        if (!isActive) {
            imgLogo.setVisibility(View.GONE);
            imgExpand.setImageResource(R.drawable.ic_expand_less);
            tvTataCara.setVisibility(View.VISIBLE);
            btnBayar.setVisibility(View.VISIBLE);
            if (bank.equals("bni")) {
                isBniActive = true;
                clContainerBni.setOnClickListener(null);
            } else {
                isBriActive = true;
                clContainerBri.setOnClickListener(null);
            }
        } else {
            imgLogo.setVisibility(View.VISIBLE);
            imgExpand.setImageResource(R.drawable.ic_expand_more);
            tvTataCara.setVisibility(View.GONE);
            btnBayar.setVisibility(View.GONE);
            if (bank.equals("bni")) {
                isBniActive = false;
                clContainerBni.setOnClickListener(bniClickListener);
            } else {
                isBriActive = false;
                clContainerBri.setOnClickListener(briClickListener);
            }
        }
    }

    private void konfirmasiPembayaran(final String bankName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin membeli course ini?");
        builder.setTitle("Beli");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                sentPaymentDetailsToAdmin(bankName);
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

    private void sentPaymentDetailsToAdmin(String bankName) {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        long dateCreated = Timestamp.now().getSeconds();
        String userId = sharedPreferences.getString(USERID_PREFS, "");
        PaymentModel paymentModel = new PaymentModel(userId, blendedCourseId, bankName, dateCreated, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference paymentRef = firebaseFirestore.collection("Payment");
        paymentRef
                .add(paymentModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast.makeText(PaymentActivity.this, "Detail pembayaran telah dikirim ke operator", Toast.LENGTH_SHORT).show();
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
