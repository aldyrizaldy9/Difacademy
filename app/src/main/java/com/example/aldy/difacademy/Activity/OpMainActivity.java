package com.example.aldy.difacademy.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.aldy.difacademy.Activity.LoginActivity.JENIS_USER_PREFS;
import static com.example.aldy.difacademy.Activity.LoginActivity.SHARE_PREFS;
import static com.example.aldy.difacademy.Activity.LoginActivity.USERID_PREFS;

public class OpMainActivity extends AppCompatActivity {
    private static final String TAG = "OpMainActivity";

    boolean doubleBackToExitPressedOnce = false;
    ConstraintLayout clLogout;
    ImageView imgLogout;
    Button btnFree, btnOnline, btnBlended, btnBerita, btnTags;
    TextView tvNavbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_main);

        findView();
        onClick();
    }

    private void findView() {
        clLogout = findViewById(R.id.cl_icon3);
        clLogout.setVisibility(View.VISIBLE);
        imgLogout = findViewById(R.id.img_icon3);
        imgLogout.setImageResource(R.drawable.ic_power_settings_new);
        btnFree = findViewById(R.id.btn_op_main_free_course);
        btnOnline = findViewById(R.id.btn_op_main_online_course);
        btnBlended = findViewById(R.id.btn_op_main_blended_course);
        btnBerita = findViewById(R.id.btn_op_main_news);
        btnTags = findViewById(R.id.btn_op_main_tags);
        tvNavbar = findViewById(R.id.tv_navbar);

        tvNavbar.setText("Welcome, Admin");
    }

    private void onClick() {
        clLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKeluarDialog();
            }
        });
        btnFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpMainActivity.this, OpFreeCourseActivity.class);
                startActivity(intent);
            }
        });
        btnBlended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpMainActivity.this, OpBlendedCourseActivity.class);
                startActivity(intent);
            }
        });
        btnBerita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpMainActivity.this, OpNewsActivity.class);
                startActivity(intent);
            }
        });
        btnTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpMainActivity.this, OpTagsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERID_PREFS, "");
        editor.putString(JENIS_USER_PREFS, "");
        editor.apply();

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(OpMainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showKeluarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OpMainActivity.this);
        builder.setMessage("Apakah anda yakin ingin keluar?");
        builder.setTitle("Keluar");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
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
