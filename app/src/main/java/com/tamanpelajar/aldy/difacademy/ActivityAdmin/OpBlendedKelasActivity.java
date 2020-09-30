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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tamanpelajar.aldy.difacademy.Adapter.OpKelasBlendedAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.KelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.ADD_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.DELETE_REQUEST_CODE;
import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.UPDATE_REQUEST_CODE;

public class OpBlendedKelasActivity extends AppCompatActivity {
    public static boolean isKelasBlendedChanged;

    private TextView tvNavbar;
    private ConstraintLayout clBack, clAdd;
    private ImageView imgBack, imgAdd;
    private RecyclerView rvBlendedKelas;

    private ArrayList<KelasBlendedModel> kelasBlendedModels;
    private OpKelasBlendedAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference blendedKelasRef = db.collection(CommonMethod.refKelasBlended);
    private DocumentSnapshot lastVisible;
    private boolean loadNewData;
    private SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_blended_kelas);

        kelasBlendedModels = new ArrayList<>();

        initView();
        onClick();
        setRecyclerView();
        getFirstData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isKelasBlendedChanged) {
            isKelasBlendedChanged = false;
            srl.setRefreshing(true);
            kelasBlendedModels.clear();
            adapter.notifyDataSetChanged();
            getFirstData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        KelasBlendedModel model = intent.getParcelableExtra(CommonMethod.intentKelasBlendedModel);
        int index = intent.getIntExtra(CommonMethod.intentIndex, -1);

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                kelasBlendedModels.add(model);
            }
        } else if (requestCode == DELETE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (index != -1) {
                kelasBlendedModels.remove(index);
            }
        } else if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (model != null) {
                kelasBlendedModels.set(index, model);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Kelas Blended");
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
        rvBlendedKelas = findViewById(R.id.rv_op_blended_course);
        srl = findViewById(R.id.srl_op_blended_kelas);

        srl.setRefreshing(true);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                kelasBlendedModels.clear();
                adapter.notifyDataSetChanged();
                getFirstData();
            }
        });
    }

    private void onClick() {
        clAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpBlendedKelasActivity.this, OpAddBlendedKelasActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView() {
        final LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvBlendedKelas.setLayoutManager(manager);

        adapter = new OpKelasBlendedAdapter(this, kelasBlendedModels);
        rvBlendedKelas.setAdapter(adapter);

        rvBlendedKelas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (manager.findLastVisibleItemPosition() >= kelasBlendedModels.size() - CommonMethod.paginationLoadNewData &&
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
        Query load = blendedKelasRef
                .orderBy(CommonMethod.fieldDateCreated, Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(CommonMethod.paginationMaxLoad);
        load.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            KelasBlendedModel model = documentSnapshot.toObject(KelasBlendedModel.class);
                            model.setDocumentId(documentSnapshot.getId());
                            kelasBlendedModels.add(model);
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
                        Toast.makeText(OpBlendedKelasActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFirstData() {
        Query first = blendedKelasRef
                .orderBy(CommonMethod.fieldDateCreated, Query.Direction.DESCENDING)
                .limit(CommonMethod.paginationMaxLoad);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        kelasBlendedModels.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            KelasBlendedModel newModel = documentSnapshot.toObject(KelasBlendedModel.class);
                            newModel.setDocumentId(documentSnapshot.getId());
                            kelasBlendedModels.add(newModel);
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
                        Toast.makeText(OpBlendedKelasActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
