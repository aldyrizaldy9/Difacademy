package com.example.aldy.difacademy.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.aldy.difacademy.Activity.LoginActivity.JENIS_USER_PREFS;
import static com.example.aldy.difacademy.Activity.LoginActivity.SHARE_PREFS;
import static com.example.aldy.difacademy.Activity.LoginActivity.USERID_PREFS;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout clSettings, clOngoing;
    ImageView imgKelasGratis, imgKelasOnline, imgKelasCampuran;
    Button btnBeritaLainnya;
    TextView tvDiikutiSemua;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        onClick();
    }

    private void findView() {
        clSettings = findViewById(R.id.cl_main_settings);
        clOngoing = findViewById(R.id.cl_main_ongoing_container);
        imgKelasGratis = findViewById(R.id.img_main_kelas_gratis);
        imgKelasOnline = findViewById(R.id.img_main_kelas_online);
        imgKelasCampuran = findViewById(R.id.img_main_kelas_campuran);
        btnBeritaLainnya = findViewById(R.id.btn_main_berita_lainnya);
        tvDiikutiSemua = findViewById(R.id.tv_main_diikuti_semua);
    }

    private void onClick() {
        clSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        clOngoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListVideoCourseActivity.class);
                startActivity(intent);
            }
        });
        imgKelasGratis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FreeCourseActivity.class);
                startActivity(intent);
            }
        });
        imgKelasOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Fitur ini belum tersedia, stay tune :)", Toast.LENGTH_SHORT).show();
            }
        });
        imgKelasCampuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BlendedCourseActivity.class);
                startActivity(intent);
            }
        });
        btnBeritaLainnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListNewsActivity.class);
                startActivity(intent);
            }
        });
        tvDiikutiSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OngoingCourseActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
