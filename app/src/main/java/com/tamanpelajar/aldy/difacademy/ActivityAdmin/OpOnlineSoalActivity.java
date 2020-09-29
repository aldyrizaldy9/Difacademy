package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

import android.app.ProgressDialog;
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

import com.tamanpelajar.aldy.difacademy.Adapter.OpSoalOnlineAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.SoalBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.SoalOnlineModel;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddOnlineKelasActivity.kelasOnlineDocId;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddOnlineMateriActivity.onlineMateriDocId;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpOnlineSoalActivity extends AppCompatActivity {
    private TextView tvNavbar;
    private ConstraintLayout clBack, clAdd;
    private ImageView imgBack, imgAdd;
    private RecyclerView rvOnlineSoal;

    private ArrayList<SoalOnlineModel> soalOnlineModels;
    private OpSoalOnlineAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference onlineSoalRef = db.collection(CommonMethod.refKelasOnline)
            .document(kelasOnlineDocId)
            .collection(CommonMethod.refMateriOnline)
            .document(onlineMateriDocId)
            .collection(CommonMethod.refSoalOnline);

    private SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_online_soal);

        soalOnlineModels = new ArrayList<>();

        initView();
        onClick();
        setRecyclerView();
        getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        SoalOnlineModel model = intent.getParcelableExtra(CommonMethod.intentSoalOnlineModel);
        int index = intent.getIntExtra(CommonMethod.intentIndex, -1);

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                soalOnlineModels.add(model);
            }
        } else if (requestCode == DELETE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (index != -1) {
                soalOnlineModels.remove(index);
            }
        } else if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                soalOnlineModels.set(index, model);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Soal Materi Kelas Online");
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
        rvOnlineSoal = findViewById(R.id.rv_op_online_soal);
        srl = findViewById(R.id.srl_op_online_soal);

        srl.setRefreshing(true);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                soalOnlineModels.clear();
                adapter.notifyDataSetChanged();
                getData();
            }
        });
    }

    private void onClick() {
        clAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpOnlineSoalActivity.this, OpAddOnlineSoalActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView() {
        final LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvOnlineSoal.setLayoutManager(manager);
        adapter = new OpSoalOnlineAdapter(this, soalOnlineModels);
        rvOnlineSoal.setAdapter(adapter);
    }

    private void getData() {
        Query first = onlineSoalRef
                .orderBy(CommonMethod.fieldDateCreated, Query.Direction.DESCENDING);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        soalOnlineModels.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            SoalOnlineModel newModel = documentSnapshot.toObject(SoalOnlineModel.class);
                            newModel.setDocumentId(documentSnapshot.getId());
                            soalOnlineModels.add(newModel);
                        }
                        adapter.notifyDataSetChanged();
                        srl.setRefreshing(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        srl.setRefreshing(false);
                        Toast.makeText(OpOnlineSoalActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
