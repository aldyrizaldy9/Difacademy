package com.tamanpelajar.aldy.difacademy.ActivityUser;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.UserModel;
import com.tamanpelajar.aldy.difacademy.Notification.APIService;
import com.tamanpelajar.aldy.difacademy.Notification.Data;
import com.tamanpelajar.aldy.difacademy.Notification.MyResponse;
import com.tamanpelajar.aldy.difacademy.Notification.Sender;
import com.tamanpelajar.aldy.difacademy.Notification.Token;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.SHARE_PREFS;
import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.USERID_PREFS;
import static com.tamanpelajar.aldy.difacademy.BuildConfig.ADMIN_USER_ID;

public class UsPaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";
    View.OnClickListener bniClickListener;
    View.OnClickListener briClickListener;
    private ConstraintLayout clBack, clContainerBni, clContainerBri, clExpandBni, clExpandBri;
    private ImageView imgLogoBni, imgLogoBri, imgExpandBni, imgExpandBri;
    private TextView tvTataCaraBni, tvTataCaraBri;
    private Button btnBayarBni, btnBayarBri;
    private boolean isBniActive = false, isBriActive = false;

    private String userId, namaUser, email, noWa, namaMateri, hargaMateri, namaBank;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
//    private VideoModel videoModel;
    private String jenisKelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_payment);
        initView();
        createOnClick();
        onClick();
    }

    private void initView() {
        ConstraintLayout clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navKuning));
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clContainerBni = findViewById(R.id.cl_payment_container_bni);
        clContainerBri = findViewById(R.id.cl_payment_container_bri);
        clExpandBni = findViewById(R.id.cl_payment_expand_bni);
        clExpandBri = findViewById(R.id.cl_payment_expand_bri);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        imgLogoBni = findViewById(R.id.img_payment_logo_bni);
        imgLogoBri = findViewById(R.id.img_payment_logo_bri);
        imgExpandBni = findViewById(R.id.img_payment_expand_bni);
        imgExpandBni.setImageResource(R.drawable.ic_expand_more);
        imgExpandBri = findViewById(R.id.img_payment_expand_bri);
        imgExpandBri.setImageResource(R.drawable.ic_expand_more);
        TextView tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Cara Pembayaran");
        tvTataCaraBni = findViewById(R.id.tv_payment_tata_cara_bni);
        tvTataCaraBri = findViewById(R.id.tv_payment_tata_cara_bri);
        btnBayarBni = findViewById(R.id.btn_payment_bayar_bni);
        btnBayarBri = findViewById(R.id.btn_payment_bayar_bri);
        Intent intent = getIntent();
//        videoModel = intent.getParcelableExtra("videoModel");
        jenisKelas = intent.getStringExtra("jenisKelas");
        SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getString(USERID_PREFS, "");
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
                namaBank = "BNI";
                konfirmasiPembayaran();
            }
        });

        btnBayarBri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namaBank = "BRI";
                konfirmasiPembayaran();
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

    private void konfirmasiPembayaran() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin membeli course ini?");
        builder.setTitle("Beli");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!CommonMethod.isInternetAvailable(UsPaymentActivity.this)) {
                    return;
                }

                getUserData();
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

    private void sendPaymentDetailsToAdmin() {

//        long dateCreated = Timestamp.now().getSeconds();
//        //String userId, String namaUser, String email, String noWa, String kelasId, String namaKelas, String hargaKelas, String namaBank, long dateCreated, boolean isSeen, boolean isPaid
//        PaymentModel paymentModel = new PaymentModel(userId, namaUser, email, noWa, jenisKelas, videoModel.getKelasId(), videoModel.getMateriId(), namaMateri, hargaMateri, namaBank, dateCreated, false, false);
//
//        CollectionReference paymentRef = firebaseFirestore.collection("Payment");
//        paymentRef
//                .add(paymentModel)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        progressDialog.dismiss();
//                        sendNotificationPayment();
//                        Toast.makeText(UsPaymentActivity.this, "Detail pembelian telah dikirim ke admin", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Log.d(TAG, e.toString());
//                    }
//                });
    }

    private void sendNotificationPayment() {
        DocumentReference docRef = firebaseFirestore.collection("Tokens").document(ADMIN_USER_ID);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Token token = documentSnapshot.toObject(Token.class);
                        String tokenAdmin = "";
                        if (token != null) {
                            tokenAdmin = token.getToken();
                        }
                        Data data = new Data(userId, R.mipmap.ic_launcher, "Ketuk untuk menuju ke daftar pembelian", "Pembelian materi", ADMIN_USER_ID);
                        Sender sender = new Sender(data, tokenAdmin);
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://fcm.googleapis.com/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        APIService apiService = retrofit.create(APIService.class);
                        apiService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (!response.isSuccessful()) {
                                            return;
                                        }
//                                        onBackPressed();
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                    }
                                });
                    }
                });
    }

    private void getUserData() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference userRef = firebaseFirestore.collection("User");
        userRef
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        UserModel userModel = new UserModel();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            userModel = queryDocumentSnapshot.toObject(UserModel.class);
                            userModel.setDocumentId(queryDocumentSnapshot.getId());
                        }
                        namaUser = userModel.getNama();
                        email = userModel.getEmail();
                        noWa = userModel.getNoTelp();
//                        getMateriData();
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

//    private void getMateriData() {
//        DocumentReference courseRef;
//        if (jenisKelas.equalsIgnoreCase("online")) {
//            courseRef = firebaseFirestore.collection("OnlineCourse").document(videoModel.getKelasId())
//                    .collection("OnlineMateri").document(videoModel.getMateriId());
//        } else {
//            courseRef = firebaseFirestore.collection("BlendedCourse").document(videoModel.getKelasId())
//                    .collection("BlendedMateri").document(videoModel.getMateriId());
//        }
//        courseRef
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        MateriModel materiModel = documentSnapshot.toObject(MateriModel.class);
//                        if (materiModel != null) {
//                            namaMateri = materiModel.getTitle();
//                            hargaMateri = materiModel.getHarga();
//                        }
//                        sendPaymentDetailsToAdmin();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Log.d(TAG, e.toString());
//                    }
//                });
//    }
}
