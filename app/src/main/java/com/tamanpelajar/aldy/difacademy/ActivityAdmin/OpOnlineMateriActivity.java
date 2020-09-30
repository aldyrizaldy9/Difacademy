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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tamanpelajar.aldy.difacademy.Adapter.OpMateriOnlineAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.MateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddOnlineKelasActivity.kelasOnlineDocId;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpOnlineMateriActivity extends AppCompatActivity {
    public static boolean isMateriOnlineChanged;

    private TextView tvNavbar;
    private ConstraintLayout clBack, clAdd;
    private ImageView imgBack, imgAdd;
    private RecyclerView rvOnlineMateri;

    private ArrayList<MateriOnlineModel> materiOnlineModels;
    private OpMateriOnlineAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference onlineMateriRef = db.collection(CommonMethod.refKelasOnline)
            .document(kelasOnlineDocId)
            .collection(CommonMethod.refMateriOnline);

    private SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_online_materi);

        materiOnlineModels = new ArrayList<>();

        initView();
        onClick();
        setRecyclerView();
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMateriOnlineChanged) {
            isMateriOnlineChanged = false;
            srl.setRefreshing(true);
            materiOnlineModels.clear();
            adapter.notifyDataSetChanged();
            getData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        MateriOnlineModel model = intent.getParcelableExtra(CommonMethod.intentMateriOnlineModel);
        int index = intent.getIntExtra(CommonMethod.intentIndex, -1);

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                materiOnlineModels.add(model);
            }
        } else if (requestCode == DELETE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (index != -1) {
                materiOnlineModels.remove(index);
            }
        } else if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                materiOnlineModels.set(index, model);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Materi Kelas Online");
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
        rvOnlineMateri = findViewById(R.id.rv_op_online_materi);
        srl = findViewById(R.id.srl_op_online_materi);

        srl.setRefreshing(true);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                materiOnlineModels.clear();
                adapter.notifyDataSetChanged();
                getData();
            }
        });
    }

    private void onClick() {
        clAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpOnlineMateriActivity.this, OpAddOnlineMateriActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView() {
        final LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvOnlineMateri.setLayoutManager(manager);
        adapter = new OpMateriOnlineAdapter(this, materiOnlineModels);
        rvOnlineMateri.setAdapter(adapter);
    }

    private void getData() {
        Query first = onlineMateriRef
                .orderBy(CommonMethod.fieldDateCreated, Query.Direction.DESCENDING);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            MateriOnlineModel newModel = documentSnapshot.toObject(MateriOnlineModel.class);
                            newModel.setDocumentId(documentSnapshot.getId());
                            materiOnlineModels.add(newModel);
                        }

                        adapter.notifyDataSetChanged();
                        srl.setRefreshing(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        srl.setRefreshing(false);
                        Toast.makeText(OpOnlineMateriActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
