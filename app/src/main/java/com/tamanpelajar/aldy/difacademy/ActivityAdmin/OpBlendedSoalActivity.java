package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tamanpelajar.aldy.difacademy.Adapter.OpSoalBlendedAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.SoalBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddBlendedKelasActivity.kelasBlendedDocId;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddBlendedMateriActivity.blendedMateriDocId;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpBlendedSoalActivity extends AppCompatActivity {
    private TextView tvNavbar;
    private ConstraintLayout clBack, clAdd;
    private ImageView imgBack, imgAdd;
    private RecyclerView rvBlendedSoal;

    private ArrayList<SoalBlendedModel> soalBlendedModels;
    private OpSoalBlendedAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference blendedSoalRef = db.collection(CommonMethod.refKelasBlended)
            .document(kelasBlendedDocId)
            .collection(CommonMethod.refMateriBlended)
            .document(blendedMateriDocId)
            .collection(CommonMethod.refSoalBlended);

    private SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_blended_soal);

        soalBlendedModels = new ArrayList<>();

        initView();
        onClick();
        setRecyclerView();
        getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        SoalBlendedModel model = intent.getParcelableExtra(CommonMethod.intentSoalBlendedModel);
        int index = intent.getIntExtra(CommonMethod.intentIndex, -1);

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                soalBlendedModels.add(model);
            }
        } else if (requestCode == DELETE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (index != -1) {
                soalBlendedModels.remove(index);
            }
        } else if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                soalBlendedModels.set(index, model);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Soal Materi Kelas Blended");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clAdd = findViewById(R.id.cl_icon3);
        clAdd.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        imgAdd = findViewById(R.id.img_icon3);
        imgAdd.setImageResource(R.drawable.ic_add);
        rvBlendedSoal = findViewById(R.id.rv_op_blended_soal);
        srl = findViewById(R.id.srl_op_blended_soal);

        srl.setRefreshing(true);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                soalBlendedModels.clear();
                adapter.notifyDataSetChanged();
                getData();
            }
        });
    }

    private void onClick() {
        clAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpBlendedSoalActivity.this, OpAddBlendedSoalActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView() {
        final LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvBlendedSoal.setLayoutManager(manager);
        adapter = new OpSoalBlendedAdapter(this, soalBlendedModels);
        rvBlendedSoal.setAdapter(adapter);
    }

    private void getData() {
        Query first = blendedSoalRef
                .orderBy(CommonMethod.fieldDateCreated, Query.Direction.DESCENDING);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        soalBlendedModels.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            SoalBlendedModel newModel = documentSnapshot.toObject(SoalBlendedModel.class);
                            newModel.setDocumentId(documentSnapshot.getId());
                            soalBlendedModels.add(newModel);
                        }
                        adapter.notifyDataSetChanged();
                        srl.setRefreshing(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        srl.setRefreshing(false);
                        Toast.makeText(OpBlendedSoalActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
