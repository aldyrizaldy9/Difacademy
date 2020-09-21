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

import com.tamanpelajar.aldy.difacademy.Adapter.OpMateriAdapter;
import com.tamanpelajar.aldy.difacademy.Adapter.OpMateriBlendedAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.MateriBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.MateriModel;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddBlendedKelasActivity.kelasBlendedDocId;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpBlendedMateriActivity extends AppCompatActivity {
    private TextView tvNavbar;
    private ConstraintLayout clBack, clAdd;
    private ImageView imgBack, imgAdd;
    private RecyclerView rvBlendedMateri;

    private ArrayList<MateriBlendedModel> materiBlendedModels;
    private OpMateriBlendedAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference blendedMateriRef = db.collection(CommonMethod.refKelasBlended)
            .document(kelasBlendedDocId)
            .collection(CommonMethod.refMateriBlended);

    private DocumentSnapshot lastVisible;
    private boolean loadNewData;

    private SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_blended_materi);

        materiBlendedModels = new ArrayList<>();

        initView();
        onClick();
        setRecyclerView();
        getFirstData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        MateriBlendedModel model = intent.getParcelableExtra(CommonMethod.intentMateriBlendedModel);
        int index = intent.getIntExtra(CommonMethod.intentIndex, -1);

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                materiBlendedModels.add(model);
            }
        } else if (requestCode == DELETE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (index != -1) {
                materiBlendedModels.remove(index);
            }
        } else if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                materiBlendedModels.set(index, model);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Materi Kelas Blended");
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
        rvBlendedMateri = findViewById(R.id.rv_op_blended_materi);
        srl = findViewById(R.id.srl_op_blended_materi);

        srl.setRefreshing(true);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                materiBlendedModels.clear();
                adapter.notifyDataSetChanged();
                getFirstData();
            }
        });
    }

    private void onClick() {
        clAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpBlendedMateriActivity.this, OpAddBlendedMateriActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView() {
        final LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvBlendedMateri.setLayoutManager(manager);
        adapter = new OpMateriBlendedAdapter(this, materiBlendedModels);
        rvBlendedMateri.setAdapter(adapter);

        rvBlendedMateri.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (manager.findLastVisibleItemPosition() >= materiBlendedModels.size() - CommonMethod.paginationLoadNewData &&
                        lastVisible != null &&
                        loadNewData) {
                    loadNewData = false;
                    getNewData();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getNewData() {
        Query load = blendedMateriRef
                .orderBy(CommonMethod.fieldDateCreated, Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(CommonMethod.paginationMaxLoad);
        load.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            MateriBlendedModel newModel = documentSnapshot.toObject(MateriBlendedModel.class);
                            newModel.setDocumentId(documentSnapshot.getId());
                            materiBlendedModels.add(newModel);
                        }

                        if (queryDocumentSnapshots.size() >= CommonMethod.paginationMaxLoad) {
                            lastVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                        } else {
                            lastVisible = null;
                        }

                        adapter.notifyDataSetChanged();
                        loadNewData = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadNewData = true;
                        Toast.makeText(OpBlendedMateriActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFirstData() {
        Query first = blendedMateriRef
                .orderBy(CommonMethod.fieldDateCreated, Query.Direction.DESCENDING)
                .limit(CommonMethod.paginationMaxLoad);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            MateriBlendedModel newModel = documentSnapshot.toObject(MateriBlendedModel.class);
                            newModel.setDocumentId(documentSnapshot.getId());
                            materiBlendedModels.add(newModel);
                        }

                        if (queryDocumentSnapshots.size() >= CommonMethod.paginationMaxLoad) {
                            lastVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                        } else {
                            lastVisible = null;
                        }

                        adapter.notifyDataSetChanged();
                        srl.setRefreshing(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        srl.setRefreshing(false);
                        Toast.makeText(OpBlendedMateriActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
