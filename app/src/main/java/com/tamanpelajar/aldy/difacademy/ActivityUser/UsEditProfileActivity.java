package com.tamanpelajar.aldy.difacademy.ActivityUser;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.tamanpelajar.aldy.difacademy.Model.AnggotaKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.AnggotaMateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.GraduationMateriBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.GraduationMateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.OngoingMateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.PaymentKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.PaymentMateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.UserModel;
import com.tamanpelajar.aldy.difacademy.R;

import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.SHARE_PREFS;
import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.USERID_PREFS;

public class UsEditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private ConstraintLayout clBack;
    private ConstraintLayout clSave;
    private EditText edtNama, edtNoWa;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference userRef;
    private DocumentReference userDocRef;
    private UserModel userModel;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_edit_profile);
        initView();
        onClick();
        getUserData();
    }

    private void initView() {
        ConstraintLayout clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navKuning));
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clSave = findViewById(R.id.cl_icon3);
        clSave.setVisibility(View.VISIBLE);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        ImageView imgSave = findViewById(R.id.img_icon3);
        imgSave.setImageResource(R.drawable.ic_check);
        TextView tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Sunting Profil");
        edtNama = findViewById(R.id.edt_edit_profile_nama);
        edtNoWa = findViewById(R.id.edt_edit_profile_nowa);
        progressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konfirmasiSimpan();
            }
        });
    }

    private void getUserData() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String userId = sharedPreferences.getString(USERID_PREFS, "");
        firebaseFirestore = FirebaseFirestore.getInstance();
        userRef = firebaseFirestore.collection(CommonMethod.refUser);
        userRef
                .whereEqualTo(CommonMethod.fieldUserId, userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            userModel = queryDocumentSnapshot.toObject(UserModel.class);
                            userModel.setDocumentId(queryDocumentSnapshot.getId());
                        }
                        edtNama.setText(userModel.getNama());
                        edtNoWa.setText(userModel.getNoTelp());
                        userDocRef = userRef.document(userModel.getDocumentId());
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void updateProfile() {
        progressDialog.setMessage("Menyimpan");
        progressDialog.setCancelable(false);
        progressDialog.show();
        UserModel userModel = new UserModel(this.userModel.getUserId(), edtNama.getText().toString(), this.userModel.getEmail(), edtNoWa.getText().toString());
        userDocRef
                .set(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getOngoingKelasBlended();
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

    private void getOngoingKelasBlended() {
        userDocRef
                .collection(CommonMethod.refOngoingKelasBlended)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            OngoingKelasBlendedModel ongoingKelasBlendedModel = queryDocumentSnapshot.toObject(OngoingKelasBlendedModel.class);
                            getRefAnggotaKelasBlended(ongoingKelasBlendedModel.getKelasId());
                        }
                        getOngoingMateriOnline();
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

    private void getRefAnggotaKelasBlended(String kelasId) {
        CollectionReference anggotaKelasRef = firebaseFirestore
                .collection(CommonMethod.refKelasBlended)
                .document(kelasId)
                .collection(CommonMethod.refAnggota);

        anggotaKelasRef
                .whereEqualTo("userId", userModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            AnggotaKelasBlendedModel anggotaKelasBlendedModel = queryDocumentSnapshot.toObject(AnggotaKelasBlendedModel.class);
                            anggotaKelasBlendedModel.setName(edtNama.getText().toString());

                            setNamaAnggotaKelasBlended(queryDocumentSnapshot.getId(), anggotaKelasBlendedModel);
                        }

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

    private void setNamaAnggotaKelasBlended(String docId, AnggotaKelasBlendedModel anggotaKelasBlendedModel) {
        DocumentReference anggotaKelasRef = firebaseFirestore
                .collection(CommonMethod.refKelasBlended)
                .document(anggotaKelasBlendedModel.getKelasId())
                .collection(CommonMethod.refAnggota)
                .document(docId);
        anggotaKelasRef
                .set(anggotaKelasBlendedModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void getOngoingMateriOnline() {
        userDocRef
                .collection(CommonMethod.refOngoingMateriOnline)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            OngoingMateriOnlineModel ongoingMateriOnlineModel = queryDocumentSnapshot.toObject(OngoingMateriOnlineModel.class);
                            getRefAnggotaMateriOnline(ongoingMateriOnlineModel.getKelasId(), ongoingMateriOnlineModel.getMateriId());
                        }
                        getPaymentKelasBlended();
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

    private void getRefAnggotaMateriOnline(String kelasId, String materiId) {
        CollectionReference anggotaMateriRef = firebaseFirestore
                .collection(CommonMethod.refKelasOnline)
                .document(kelasId)
                .collection(CommonMethod.refMateriOnline)
                .document(materiId)
                .collection(CommonMethod.refAnggota);

        anggotaMateriRef
                .whereEqualTo("userId", userModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            AnggotaMateriOnlineModel anggotaMateriOnlineModel = queryDocumentSnapshot.toObject(AnggotaMateriOnlineModel.class);
                            anggotaMateriOnlineModel.setName(edtNama.getText().toString());

                            setNamaAnggotaMateriOnline(queryDocumentSnapshot.getId(), anggotaMateriOnlineModel);
                        }
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

    private void setNamaAnggotaMateriOnline(String docId, AnggotaMateriOnlineModel anggotaMateriOnlineModel) {
        DocumentReference anggotaMateriRef = firebaseFirestore
                .collection(CommonMethod.refKelasOnline)
                .document(anggotaMateriOnlineModel.getKelasId())
                .collection(CommonMethod.refMateriOnline)
                .document(anggotaMateriOnlineModel.getMateriId())
                .collection(CommonMethod.refAnggota)
                .document(docId);
        anggotaMateriRef
                .set(anggotaMateriOnlineModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void getPaymentKelasBlended() {
        CollectionReference paymentKelasBlendedRef = firebaseFirestore.collection(CommonMethod.refPaymentKelasBlended);
        paymentKelasBlendedRef
                .whereEqualTo("userId", userModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            PaymentKelasBlendedModel paymentKelasBlendedModel = queryDocumentSnapshot.toObject(PaymentKelasBlendedModel.class);
                            paymentKelasBlendedModel.setNamaUser(edtNama.getText().toString());

                            setNamaPaymentKelasBlended(queryDocumentSnapshot.getId(), paymentKelasBlendedModel);
                        }
                        getGraduationBlended();
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

    private void setNamaPaymentKelasBlended(String docId, PaymentKelasBlendedModel paymentKelasBlendedModel) {
        DocumentReference paymentKelasBlendedRef = firebaseFirestore
                .collection(CommonMethod.refPaymentKelasBlended)
                .document(docId);
        paymentKelasBlendedRef
                .set(paymentKelasBlendedModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void getGraduationBlended() {
        CollectionReference graduationBlendedRef = firebaseFirestore.collection(CommonMethod.refGraduationBlended);
        graduationBlendedRef
                .whereEqualTo("userId", userModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            GraduationMateriBlendedModel graduationMateriBlendedModel = queryDocumentSnapshot.toObject(GraduationMateriBlendedModel.class);
                            graduationMateriBlendedModel.setNamaUser(edtNama.getText().toString());

                            setNamaGraduationBlended(queryDocumentSnapshot.getId(), graduationMateriBlendedModel);
                        }
                        getPaymentMateriOnline();
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

    private void setNamaGraduationBlended(String docId, GraduationMateriBlendedModel graduationMateriBlendedModel) {
        DocumentReference graduationBlendedRef = firebaseFirestore
                .collection(CommonMethod.refGraduationBlended)
                .document(docId);
        graduationBlendedRef
                .set(graduationMateriBlendedModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void getPaymentMateriOnline() {
        CollectionReference paymentMateriOnlineRef = firebaseFirestore.collection(CommonMethod.refPaymentMateriOnline);
        paymentMateriOnlineRef
                .whereEqualTo("userId", userModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            PaymentMateriOnlineModel paymentMateriOnlineModel = queryDocumentSnapshot.toObject(PaymentMateriOnlineModel.class);
                            paymentMateriOnlineModel.setNamaUser(edtNama.getText().toString());

                            setNamaPaymentMateriOnline(queryDocumentSnapshot.getId(), paymentMateriOnlineModel);
                        }
                        getGraduationOnline();
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

    private void setNamaPaymentMateriOnline(String docId, PaymentMateriOnlineModel paymentMateriOnlineModel) {
        DocumentReference paymentMateriOnlineRef = firebaseFirestore
                .collection(CommonMethod.refPaymentMateriOnline)
                .document(docId);
        paymentMateriOnlineRef
                .set(paymentMateriOnlineModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void getGraduationOnline() {
        CollectionReference graduationOnlineRef = firebaseFirestore.collection(CommonMethod.refGraduationOnline);
        graduationOnlineRef
                .whereEqualTo("userId", userModel.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            GraduationMateriOnlineModel graduationMateriOnlineModel = queryDocumentSnapshot.toObject(GraduationMateriOnlineModel.class);
                            graduationMateriOnlineModel.setNamaUser(edtNama.getText().toString());

                            setNamaGraduationOnline(queryDocumentSnapshot.getId(), graduationMateriOnlineModel);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(UsEditProfileActivity.this, "Data anda telah disimpan", Toast.LENGTH_SHORT).show();
                        onBackPressed();
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

    private void setNamaGraduationOnline(String docId, GraduationMateriOnlineModel graduationMateriOnlineModel) {
        DocumentReference graduationOnlineRef = firebaseFirestore
                .collection(CommonMethod.refGraduationOnline)
                .document(docId);
        graduationOnlineRef
                .set(graduationMateriOnlineModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void konfirmasiSimpan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menyimpan?");
        builder.setTitle("Simpan");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CommonMethod.isInternetAvailable(UsEditProfileActivity.this)) {
                    if (edtNama.length() == 0 ) {
                        Toast.makeText(UsEditProfileActivity.this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    } else if (edtNoWa.length() < 8) {
                        Toast.makeText(UsEditProfileActivity.this, "Nomor Whatsapp minimal 8 digit", Toast.LENGTH_SHORT).show();
                    } else {
                        updateProfile();
                    }
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
}
