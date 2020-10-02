package com.tamanpelajar.aldy.difacademy.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.tamanpelajar.aldy.difacademy.Adapter.OpGraduationMateriOnlineAdapter;
import com.tamanpelajar.aldy.difacademy.Adapter.OpPaymentOnlineAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.GraduationMateriBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.GraduationMateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.PaymentMateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OpGraduationOnlineFragment extends Fragment {
    public static boolean isGradOnlineChanged;
    private OpGraduationMateriOnlineAdapter adapter;
    private View rootView;
    private RecyclerView rvNotifGrad;
    private ArrayList<GraduationMateriOnlineModel> graduationMateriOnlineModels;
    private SwipeRefreshLayout srl;
    private boolean loadNewData;
    private DocumentSnapshot lastVisible;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference graduationRef = db.collection(CommonMethod.refGraduationOnline);

    public OpGraduationOnlineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_op_graduation_online, container, false);
        initView();
        setRecyclerView();
        getFirstData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isGradOnlineChanged) {
            isGradOnlineChanged = false;
            srl.setRefreshing(true);
            graduationMateriOnlineModels.clear();
            adapter.notifyDataSetChanged();
            getFirstData();
        }
    }

    private void initView() {
        graduationMateriOnlineModels = new ArrayList<>();

        rvNotifGrad = rootView.findViewById(R.id.rv_op_grad_online);
        srl = rootView.findViewById(R.id.srl_op_grad_online);
        srl.setRefreshing(true);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                graduationMateriOnlineModels.clear();
                adapter.notifyDataSetChanged();
                getFirstData();
            }
        });
    }

    private void setRecyclerView() {
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvNotifGrad.setLayoutManager(manager);

        adapter = new OpGraduationMateriOnlineAdapter(getContext(), graduationMateriOnlineModels);
        rvNotifGrad.setAdapter(adapter);

        rvNotifGrad.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (manager.findLastVisibleItemPosition() >= graduationMateriOnlineModels.size() - CommonMethod.paginationLoadNewData &&
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
        Query load = graduationRef
                .orderBy(CommonMethod.fieldDateCreated, Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(CommonMethod.paginationMaxLoad);

        load.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            GraduationMateriOnlineModel model = documentSnapshot.toObject(GraduationMateriOnlineModel.class);
                            model.setDocumentId(documentSnapshot.getId());
                            graduationMateriOnlineModels.add(model);
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
                        Toast.makeText(rootView.getContext(), rootView.getContext().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFirstData() {
        Query first = graduationRef
                .orderBy(CommonMethod.fieldDateCreated, Query.Direction.DESCENDING)
                .limit(CommonMethod.paginationMaxLoad);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        graduationMateriOnlineModels.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            GraduationMateriOnlineModel model = documentSnapshot.toObject(GraduationMateriOnlineModel.class);
                            model.setDocumentId(documentSnapshot.getId());
                            graduationMateriOnlineModels.add(model);
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
                        Toast.makeText(rootView.getContext(), rootView.getContext().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
