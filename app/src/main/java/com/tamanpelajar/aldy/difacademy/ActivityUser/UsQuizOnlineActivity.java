package com.tamanpelajar.aldy.difacademy.ActivityUser;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.GraduationMateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.MateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.SoalOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.UserModel;
import com.tamanpelajar.aldy.difacademy.Notification.APIService;
import com.tamanpelajar.aldy.difacademy.Notification.Data;
import com.tamanpelajar.aldy.difacademy.Notification.MyResponse;
import com.tamanpelajar.aldy.difacademy.Notification.Sender;
import com.tamanpelajar.aldy.difacademy.Notification.Token;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.SHARE_PREFS;
import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.USERID_PREFS;
import static com.tamanpelajar.aldy.difacademy.BuildConfig.ADMIN_USER_ID;

public class UsQuizOnlineActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";

    ArrayList<SoalOnlineModel> soalOnlineModels;
    ArrayList<String> jawabanBenar;
    ArrayList<String> jawabanSaya;

    int nilai = 0;

    int nomor = 0;
    int totalNomor = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView tvWaktu, tvNomor, tvSoal;
    RadioGroup radioGroup;
    RadioButton rbJawabanA, rbJawabanB, rbJawabanC, rbJawabanD, rbJawabanE;
    ImageView imgPrev, imgNextOrFinish;

    CountDownTimer countDownTimer;

    ProgressDialog pd;
    String userId, namaUser, noWa, email, namaMateri;
    SharedPreferences sharedPreferences;

    MateriOnlineModel materiOnlineModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_quiz);

        soalOnlineModels = new ArrayList<>();
        jawabanBenar = new ArrayList<>();
        jawabanSaya = new ArrayList<>();
        pd = new ProgressDialog(UsQuizOnlineActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        initView();
        loadData();
    }

    private void initView() {
        tvWaktu = findViewById(R.id.tv_quiz_waktu);
        tvWaktu.setText("10 : 00");
        tvNomor = findViewById(R.id.tv_quiz_nomor);
        tvSoal = findViewById(R.id.tv_quiz_soal);
        radioGroup = findViewById(R.id.rg_quiz);
        rbJawabanA = findViewById(R.id.rb_quiz_jawaban_a);
        rbJawabanB = findViewById(R.id.rb_quiz_jawaban_b);
        rbJawabanC = findViewById(R.id.rb_quiz_jawaban_c);
        rbJawabanD = findViewById(R.id.rb_quiz_jawaban_d);
        rbJawabanE = findViewById(R.id.rb_quiz_jawaban_e);
        imgPrev = findViewById(R.id.img_quiz_prev);
        imgNextOrFinish = findViewById(R.id.img_quiz_next_or_finish);
        sharedPreferences = getSharedPreferences(SHARE_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getString(USERID_PREFS, "");
        Intent intent = getIntent();
        materiOnlineModel = intent.getParcelableExtra(CommonMethod.intentMateriOnlineModel);
    }

    private void onClick() {
        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quizBack();
            }
        });
        imgNextOrFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nomor == totalNomor) {
                    quizFinish();
                } else {
                    quizNext();
                }
            }
        });
    }

    private void loadData() {
        CollectionReference colRef = db
                .collection(CommonMethod.refKelasOnline)
                .document(materiOnlineModel.getKelasId())
                .collection(CommonMethod.refMateriOnline)
                .document(materiOnlineModel.getDocumentId())
                .collection(CommonMethod.refSoalOnline);


        colRef.get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            SoalOnlineModel soalOnlineModel = documentSnapshot.toObject(SoalOnlineModel.class);
                            soalOnlineModel.setDocumentId(documentSnapshot.getId());

                            soalOnlineModels.add(soalOnlineModel);
                        }
                        pd.dismiss();
                        mulaiQuiz();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(UsQuizOnlineActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
    }

    private void mulaiQuiz() {

        countDownTimer = new CountDownTimer(600000, 1000) {
            @Override
            public void onTick(long l) {
                int menit = (int) ((l / (1000 * 60)) % 60);
                int detik = (int) (l / 1000) % 60;

                if (menit < 1) {
                    if (detik < 10) {
                        tvWaktu.setText("00 : 0" + detik);
                    } else {
                        tvWaktu.setText("00 : " + detik);
                    }
                } else if (menit < 10) {
                    if (detik < 10) {
                        tvWaktu.setText("0" + menit + " : 0" + detik);
                    } else {
                        tvWaktu.setText("0" + menit + " : " + detik);
                    }
                } else if (menit > 10) {
                    if (detik < 10) {
                        tvWaktu.setText(menit + " : 0" + detik);
                    } else {
                        tvWaktu.setText(menit + " : " + detik);
                    }
                }
            }

            @Override
            public void onFinish() {
                quizFinish();
            }
        }.start();

        totalNomor = soalOnlineModels.size();
        if (totalNomor > 0) {
            nomor = 1;

            for (SoalOnlineModel soalOnlineModel : soalOnlineModels) {
                jawabanBenar.add(soalOnlineModel.getJawabanBenar());
                jawabanSaya.add("");
            }
        } else {
            Toast.makeText(this, "Belum ada soal", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return;
        }

        loadSoal(nomor);
        onClick();
    }

    private void loadSoal(int nomor) {
        tvNomor.setText(nomor + "/" + totalNomor);
        SoalOnlineModel soalOnlineModel = soalOnlineModels.get(nomor - 1);
        tvSoal.setText(soalOnlineModel.getSoal());
        rbJawabanA.setText(soalOnlineModel.getJwbA());
        rbJawabanB.setText(soalOnlineModel.getJwbB());
        rbJawabanC.setText(soalOnlineModel.getJwbC());
        rbJawabanD.setText(soalOnlineModel.getJwbD());
        rbJawabanE.setText(soalOnlineModel.getJwbE());

        radioGroup.clearCheck();

        if (!jawabanSaya.get(nomor - 1).equals("")) {
            switch (jawabanSaya.get(nomor - 1)) {
                case "A":
                    rbJawabanA.setChecked(true);
                    break;
                case "B":
                    rbJawabanB.setChecked(true);
                    break;
                case "C":
                    rbJawabanC.setChecked(true);
                    break;
                case "D":
                    rbJawabanD.setChecked(true);
                    break;
                case "E":
                    rbJawabanE.setChecked(true);
                    break;
            }
        }

        if (nomor == totalNomor) {
            imgPrev.setVisibility(View.INVISIBLE);
            imgNextOrFinish.setImageResource(R.drawable.ic_check_white);
        } else {
            imgNextOrFinish.setImageResource(R.drawable.ic_arrow_forward_white);
        }

        if (nomor == 1) {
            imgPrev.setVisibility(View.INVISIBLE);
        }

        if (nomor > 1) {
            imgPrev.setVisibility(View.VISIBLE);
        }
    }

    private void setJawaban() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rb_quiz_jawaban_a:
                jawabanSaya.set(nomor - 1, "A");
                break;
            case R.id.rb_quiz_jawaban_b:
                jawabanSaya.set(nomor - 1, "B");
                break;
            case R.id.rb_quiz_jawaban_c:
                jawabanSaya.set(nomor - 1, "C");
                break;
            case R.id.rb_quiz_jawaban_d:
                jawabanSaya.set(nomor - 1, "D");
                break;
            case R.id.rb_quiz_jawaban_e:
                jawabanSaya.set(nomor - 1, "E");
                break;
        }
    }

    private void quizNext() {
        setJawaban();
        nomor++;
        loadSoal(nomor);
    }

    private void quizBack() {
        setJawaban();
        nomor--;
        loadSoal(nomor);
    }

    private void quizFinish() {
        setJawaban();

        int benar = 0;
        for (String jawabanBenar : jawabanBenar) {
            for (String jawabanSaya : jawabanSaya) {
                if (jawabanSaya.equals(jawabanBenar))
                    benar++;
            }
        }

        nilai = (benar / totalNomor) * 100;

        String message = "";
        String title = "";

        if (nilai >= 80) {
            message = "Selamat anda lulus materi ini tunggu pemberitahuan dari admin ya :)";
            title = "ANDA LULUS";
        } else {
            message = "Sayang sekali anda belum dapat mengikuti workshop ya.. Semangat!!! :)";
            title = "BELUM LULUS";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (nilai >= 80) {
                    getUserData();
                } else {
                    onBackPressed();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getUserData() {
        pd.setMessage("Memuat...");
        pd.setCancelable(false);
        pd.show();

        CollectionReference userRef = db.collection(CommonMethod.refUser);
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
                        getMateriData();
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

    private void getMateriData() {
        DocumentReference materiRef = db
                .collection(CommonMethod.refKelasOnline)
                .document(materiOnlineModel.getKelasId())
                .collection(CommonMethod.refMateriOnline)
                .document(materiOnlineModel.getDocumentId());

        materiRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        MateriOnlineModel materiOnlineModel = documentSnapshot.toObject(MateriOnlineModel.class);
                        if (materiOnlineModel != null) {
                            namaMateri = materiOnlineModel.getTitle();
                        }
                        sendGraduationDetailsToAdmin();
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

    private void sendGraduationDetailsToAdmin() {

        long dateCreated = Timestamp.now().getSeconds();
        GraduationMateriOnlineModel graduationMateriOnlineModel = new GraduationMateriOnlineModel(userId, namaUser, email, noWa, materiOnlineModel.getDocumentId(), namaMateri, dateCreated, false, false);

        CollectionReference gradRef = db.collection(CommonMethod.refGraduationOnline);
        gradRef
                .add(graduationMateriOnlineModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        sendOpNotification();
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

    private void sendOpNotification() {
        DocumentReference docRef = db.collection(CommonMethod.refTokens).document(ADMIN_USER_ID);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Token token = documentSnapshot.toObject(Token.class);
                        String tokenAdmin = token.getToken();
                        Data data = new Data(userId, R.mipmap.ic_launcher, "Ketuk untuk menuju ke daftar kelulusan", "Kelulusan peserta", ADMIN_USER_ID);
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
                                        pd.dismiss();
                                        onBackPressed();
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                    }
                                });
                    }
                });
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer.onFinish();
            } else {
                quizFinish();
            }
        } catch (Exception e) {

        }
    }

}
