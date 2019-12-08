package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.aldy.difacademy.Model.QuizModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.example.aldy.difacademy.Activity.OpAddBlendedCourseActivity.blendedCourseDocId;
import static com.example.aldy.difacademy.Activity.OpMainActivity.ADD_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.DELETE_REQUEST_CODE;
import static com.example.aldy.difacademy.Activity.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpAddQuizActivity extends AppCompatActivity {
    private static final String TAG = "OpAddQuizActivity";

    public static String quizId = "";

    TextView tvNavbar;
    ConstraintLayout clBack;
    ImageView imgBack;

    EditText edtSoal, edtA, edtB, edtC, edtD, edtE;
    Spinner spnJawaban;
    Button btnHapus, btnSimpan;

    String jawabanBenar = "";
    QuizModel quizModel;
    ArrayList<String> listJawabanBenar;
    boolean thereIsData = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collRef = db.collection("BlendedCourse")
            .document(blendedCourseDocId)
            .collection("Quiz");
    long dateCreated = 0;
    int index;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_quiz);

        initView();
        customSpinner();
        checkIntent();
        onClick();
    }

    private void initView() {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Detail Quiz");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);

        edtSoal = findViewById(R.id.edt_op_add_quiz_soal);
        edtA = findViewById(R.id.edt_op_add_quiz_jawaban_a);
        edtB = findViewById(R.id.edt_op_add_quiz_jawaban_b);
        edtC = findViewById(R.id.edt_op_add_quiz_jawaban_c);
        edtD = findViewById(R.id.edt_op_add_quiz_jawaban_d);
        edtE = findViewById(R.id.edt_op_add_quiz_jawaban_e);
        spnJawaban = findViewById(R.id.spn_op_add_quiz_jawaban);
        btnHapus = findViewById(R.id.btn_op_add_quiz_hapus);
        btnSimpan = findViewById(R.id.btn_op_add_quiz_simpan);
    }

    private void customSpinner() {
        listJawabanBenar = new ArrayList<>();
        listJawabanBenar.add("Pilih");
        listJawabanBenar.add("A");
        listJawabanBenar.add("B");
        listJawabanBenar.add("C");
        listJawabanBenar.add("D");
        listJawabanBenar.add("E");

        ArrayAdapter<String> spnArrayAdapter = new ArrayAdapter<String>(OpAddQuizActivity.this, R.layout.support_simple_spinner_dropdown_item, listJawabanBenar) {
            @Override
            public boolean isEnabled(int position) {
                return true;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                TextView tv = (TextView) v;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                return v;
            }
        };

        spnArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spnJawaban.setAdapter(spnArrayAdapter);
        spnJawaban.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    jawabanBenar = listJawabanBenar.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void checkIntent() {
        Intent intent = getIntent();
        quizModel = intent.getParcelableExtra("quiz_model");
        if (quizModel != null) {
            thereIsData = true;
            btnHapus.setVisibility(View.VISIBLE);
            quizId = quizModel.getDocumentId();
            edtSoal.setText(quizModel.getSoal());
            edtA.setText(quizModel.getJwbA());
            edtB.setText(quizModel.getJwbB());
            edtC.setText(quizModel.getJwbC());
            edtD.setText(quizModel.getJwbD());
            edtE.setText(quizModel.getJwbE());
            dateCreated = quizModel.getDateCreated();
            index = intent.getIntExtra("index", -1);
            for (int i = 1; i < listJawabanBenar.size(); i++) {
                if (listJawabanBenar.get(i).equals(quizModel.getJawabanBenar())) {
                    spnJawaban.setSelection(i);
                    break;
                }
            }
        } else {
            quizId = "";
        }
    }

    private void onClick() {
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHapusDialog();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtSoal.getText().toString().equals("") ||
                        edtA.getText().toString().equals("") ||
                        edtB.getText().toString().equals("") ||
                        edtC.getText().toString().equals("") ||
                        edtD.getText().toString().equals("") ||
                        edtE.getText().toString().equals("") ||
                        jawabanBenar.equals("") || spnJawaban.getSelectedItemPosition() == 0) {
                    Toast.makeText(OpAddQuizActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                } else {
                    showKonfirmasiDialog();
                }
            }
        });
    }

    private void hapus() {
        DocumentReference docRef = collRef.document(quizId);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Intent intent = new Intent(OpAddQuizActivity.this, OpQuizActivity.class);
                        intent.putExtra("index", index);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, DELETE_REQUEST_CODE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddQuizActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void edit() {
        DocumentReference docRef = collRef.document(quizId);
        String soal = edtSoal.getText().toString();
        String jwbA = edtA.getText().toString();
        String jwbB = edtB.getText().toString();
        String jwbC = edtC.getText().toString();
        String jwbD = edtD.getText().toString();
        String jwbE = edtE.getText().toString();

        final QuizModel quizModel = new QuizModel(dateCreated, soal, jwbA, jwbB, jwbC, jwbD, jwbE, jawabanBenar);
        docRef.set(quizModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Intent intent = new Intent(OpAddQuizActivity.this, OpQuizActivity.class);
                        intent.putExtra("index", index);
                        intent.putExtra("quiz_model", quizModel);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, UPDATE_REQUEST_CODE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddQuizActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tambah() {
        try {
            dateCreated = Timestamp.now().getSeconds();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        String soal = edtSoal.getText().toString();
        String jwbA = edtA.getText().toString();
        String jwbB = edtB.getText().toString();
        String jwbC = edtC.getText().toString();
        String jwbD = edtD.getText().toString();
        String jwbE = edtE.getText().toString();

        final QuizModel quizModel = new QuizModel(dateCreated, soal, jwbA, jwbB, jwbC, jwbD, jwbE, jawabanBenar);
        collRef.add(quizModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        pd.dismiss();
                        Intent intent = new Intent(OpAddQuizActivity.this, OpQuizActivity.class);
                        intent.putExtra("quiz_model", quizModel);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, ADD_REQUEST_CODE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddQuizActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showHapusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghapus soal ini?");
        builder.setTitle("Hapus Soal");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                pd.show();
                hapus();
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

    private void showKonfirmasiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menyimpan soal ini?");
        builder.setTitle("Simpan Soal");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                pd.show();
                if (thereIsData) {
                    edit();
                } else {
                    tambah();
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
