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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.Model.GraduationModel;
import com.example.aldy.difacademy.Model.PaymentModel;
import com.example.aldy.difacademy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.aldy.difacademy.Activity.LoginActivity.JENIS_USER_PREFS;
import static com.example.aldy.difacademy.Activity.LoginActivity.SHARE_PREFS;
import static com.example.aldy.difacademy.Activity.LoginActivity.USERID_PREFS;

public class OpMainActivity extends AppCompatActivity {
    private static final String TAG = "OpMainActivity";

    private boolean doubleBackToExitPressedOnce = false;
    private ConstraintLayout clLogout, clNotif;
    private ImageView imgLogout, imgNotif;
    private Button btnFree, btnOnline, btnBlended, btnBerita, btnTags;
    private TextView tvNavbar;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference paymentRef, graduationRef;

    static final int PHOTO_PICK_REQUEST_CODE = 1;
    static final int ADD_REQUEST_CODE = 2;
    static final int DELETE_REQUEST_CODE = 3;
    static final int UPDATE_REQUEST_CODE = 4;
    static final int WRITE_PERM_REQUEST_CODE = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_main);

        initView();
        onClick();
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseFirestore = FirebaseFirestore.getInstance();
        paymentRef = firebaseFirestore.collection("Payment");
        graduationRef = firebaseFirestore.collection("Graduation");
        checkIfPaymentExist();
        checkIfGraduationExist();

    }

    private void initView() {
        clLogout = findViewById(R.id.cl_icon3);
        clLogout.setVisibility(View.VISIBLE);
        imgLogout = findViewById(R.id.img_icon3);
        imgLogout.setImageResource(R.drawable.ic_power_settings_new);
        clNotif = findViewById(R.id.cl_icon2);
        clNotif.setVisibility(View.VISIBLE);
        imgNotif = findViewById(R.id.img_icon2);
        imgNotif.setImageResource(R.drawable.ic_notifications);
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
        clNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpMainActivity.this, OpNotifActivity.class);
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

    private void checkIfPaymentExist() {
        paymentRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (queryDocumentSnapshots.isEmpty()) {
                    imgNotif.setImageResource(R.drawable.ic_notifications);
                } else {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        PaymentModel paymentModel = queryDocumentSnapshot.toObject(PaymentModel.class);
                        paymentModel.setPaymentId(queryDocumentSnapshot.getId());
                        if (!paymentModel.isSeen()) {
                            imgNotif.setImageResource(R.drawable.ic_notifications_active);
                            return;
                        } else {
                            imgNotif.setImageResource(R.drawable.ic_notifications);
                        }
                    }
                }
            }
        });
    }

    private void checkIfGraduationExist() {
        graduationRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (queryDocumentSnapshots.isEmpty()) {
                    imgNotif.setImageResource(R.drawable.ic_notifications);
                } else {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        GraduationModel graduationModel = queryDocumentSnapshot.toObject(GraduationModel.class);
                        graduationModel.setGraduationId(queryDocumentSnapshot.getId());

                        if (!graduationModel.isSeen()) {
                            imgNotif.setImageResource(R.drawable.ic_notifications_active);
                            return;
                        } else {
                            imgNotif.setImageResource(R.drawable.ic_notifications);
                        }
                    }
                }
            }
        });
    }
}
