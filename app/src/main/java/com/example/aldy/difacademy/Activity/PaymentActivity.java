package com.example.aldy.difacademy.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.R;

public class PaymentActivity extends AppCompatActivity {
    private ConstraintLayout clBack, clContainerBni, clContainerBri, clExpandBni, clExpandBri;
    private ImageView imgBack, imgLogoBni, imgLogoBri, imgExpandBni, imgExpandBri;
    private TextView tvNavBar, tvTataCaraBni, tvTataCaraBri;
    private Button btnBayarBni, btnBayarBri;
    private boolean isBniActive = false, isBriActive = false;

    View.OnClickListener bniClickListener;
    View.OnClickListener briClickListener;

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
                Toast.makeText(PaymentActivity.this, "Bayar bni", Toast.LENGTH_SHORT).show();
            }
        });

        btnBayarBri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PaymentActivity.this, "Bayar bri", Toast.LENGTH_SHORT).show();
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
}
