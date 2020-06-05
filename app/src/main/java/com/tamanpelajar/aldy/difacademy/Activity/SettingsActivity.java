package com.tamanpelajar.aldy.difacademy.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tamanpelajar.aldy.difacademy.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.tamanpelajar.aldy.difacademy.Activity.LoginActivity.JENIS_USER_PREFS;
import static com.tamanpelajar.aldy.difacademy.Activity.LoginActivity.SHARE_PREFS;
import static com.tamanpelajar.aldy.difacademy.Activity.LoginActivity.USERID_PREFS;

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout clSunting, clKeluar, clBack, clGantiPass, clNavbar;
    private ImageView imgBack;
    private TextView tvNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
        onClick();
    }

    private void initView() {
        clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navCream));
        clSunting = findViewById(R.id.cl_settings_sunting);
        clGantiPass = findViewById(R.id.cl_settings_ganti_kata_sandi);
        clKeluar = findViewById(R.id.cl_settings_keluar);
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Pengaturan");
    }

    private void onClick() {
        clSunting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
        clGantiPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        clKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeluarDialog();
            }
        });
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showKeluarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setMessage("Apakah anda yakin ingin keluar?");
        builder.setTitle("Keluar");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(USERID_PREFS, "");
                editor.putString(JENIS_USER_PREFS, "");
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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
}
