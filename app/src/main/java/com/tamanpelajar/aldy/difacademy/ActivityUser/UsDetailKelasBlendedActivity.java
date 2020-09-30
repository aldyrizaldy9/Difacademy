package com.tamanpelajar.aldy.difacademy.ActivityUser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.KelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.SHARE_PREFS;
import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.USERID_PREFS;

public class UsDetailKelasBlendedActivity extends AppCompatActivity {
    private static final String TAG = "UsDetailKelasBlendedAct";
    private ImageView imgThumbnail;
    private TextView tvJudul, tvTag, tvDetail;
    private Button btnDaftarMateri, btnLampiran;
    private ProgressDialog progressDialog;
    private boolean isPaid = false;

    private FirebaseFirestore firebaseFirestore;
    private String userDocId;
    private SharedPreferences sharedPreferences;

    private KelasBlendedModel kelasBlendedModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_detail_kelas);
        initView();
        setViewWithParcelable();
        getUserDocId();
        onClick();
    }

    private void initView() {
        imgThumbnail = findViewById(R.id.img_detail_course_thumbnail);
        tvJudul = findViewById(R.id.tv_detail_course_judul);
        tvTag = findViewById(R.id.tv_detail_course_tag);
        tvDetail = findViewById(R.id.tv_detail_course_detail);
        btnLampiran = findViewById(R.id.btn_detail_course_lampiran);
        btnDaftarMateri = findViewById(R.id.btn_detail_course_daftar_materi);
        btnLampiran.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
    }

    private void setViewWithParcelable() {
        Intent intent = getIntent();
        kelasBlendedModel = intent.getParcelableExtra(CommonMethod.intentKelasBlendedModel);
        Glide.with(this).load(kelasBlendedModel.getThumbnailUrl()).into(imgThumbnail);
        tvJudul.setText(kelasBlendedModel.getTitle());
        tvTag.setText(kelasBlendedModel.getTag());
        tvDetail.setText(kelasBlendedModel.getDescription());
    }

    private void onClick() {
        btnDaftarMateri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (isPaid) {
                    intent = new Intent(UsDetailKelasBlendedActivity.this, UsMateriBlendedActivity.class);
                } else {
                    intent = new Intent(UsDetailKelasBlendedActivity.this, UsPaymentBlendedActivity.class);
                }
                intent.putExtra(CommonMethod.intentKelasBlendedModel, kelasBlendedModel);
                startActivity(intent);
            }
        });
    }

    private void getUserDocId() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String userId = sharedPreferences.getString(USERID_PREFS, "");
        firebaseFirestore = FirebaseFirestore.getInstance();

        CollectionReference userRef = firebaseFirestore.collection(CommonMethod.refUser);
        userRef
                .whereEqualTo("userId", userId)
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            userDocId = queryDocumentSnapshot.getId();
                        }

                        checkOngoing(userDocId);

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

    private void checkOngoing(final String userDocId) {
        CollectionReference onGoingRef = firebaseFirestore
                .collection(CommonMethod.refUser)
                .document(userDocId)
                .collection(CommonMethod.refOngoingKelasBlended);
        onGoingRef
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            OngoingKelasBlendedModel ongoingKelasBlendedModel = queryDocumentSnapshot.toObject(OngoingKelasBlendedModel.class);
                            if (ongoingKelasBlendedModel.getKelasId().equals(kelasBlendedModel.getDocumentId())) {
                                isPaid = true;
                                break;
                            }
                        }
                        if (isPaid) {
                            btnDaftarMateri.setText("DAFTAR MATERI");
                        } else {
                            btnDaftarMateri.setText("BELI KELAS");
                        }
                        progressDialog.dismiss();
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
