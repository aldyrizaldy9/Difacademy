package com.tamanpelajar.aldy.difacademy.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
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

import com.google.firebase.firestore.DocumentSnapshot;
import com.tamanpelajar.aldy.difacademy.Adapter.OpPaymentBlendedAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.PaymentModel;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OpPaymentBlendedFragment extends Fragment {
    private static final String TAG = "OpNotifPaymentFragment";

    private Context context;
    private OpPaymentBlendedAdapter adapter;
    private View rootView;
    private RecyclerView rvPaymentBlended;
    private ArrayList<PaymentModel> paymentModels;
    private SwipeRefreshLayout srl;
    private boolean loadNewData;
    private DocumentSnapshot lastVisible;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference paymentBlendedRef = db.collection(CommonMethod.refPaymentKelasBlended);

    public static boolean isPaymentBlendedChanged;

    public OpPaymentBlendedFragment() {
        // Required empty public constructor
    }

    public OpPaymentBlendedFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_op_payment_blended, container, false);
        initView();
        setRecyclerView();
        getFirstData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPaymentBlendedChanged) {
            srl.setRefreshing(true);
            paymentModels.clear();
            adapter.notifyDataSetChanged();
            getFirstData();
        }
    }

    private void initView() {
        paymentModels = new ArrayList<>();
        rvPaymentBlended = rootView.findViewById(R.id.rv_op_payment_blended);
        srl = rootView.findViewById(R.id.srl_op_payment_blended);
        srl.setRefreshing(true);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                paymentModels.clear();
                adapter.notifyDataSetChanged();
                getFirstData();
            }
        });
    }

    private void setRecyclerView() {
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvPaymentBlended.setLayoutManager(manager);

        adapter = new OpPaymentBlendedAdapter(rootView.getContext(), paymentModels);
        rvPaymentBlended.setAdapter(adapter);

        rvPaymentBlended.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (manager.findLastVisibleItemPosition() >= paymentModels.size() - CommonMethod.paginationLoadNewData &&
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

    private void getNewData(){
        Query load = paymentBlendedRef
                .orderBy(CommonMethod.fieldDateCreated, Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(CommonMethod.paginationMaxLoad);

        load.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            PaymentModel model = documentSnapshot.toObject(PaymentModel.class);
                            model.setDocumentId(documentSnapshot.getId());
                            paymentModels.add(model);
                        }

                        if (queryDocumentSnapshots.size() >= CommonMethod.paginationMaxLoad){
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
                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFirstData(){
        Query first = paymentBlendedRef
                .orderBy(CommonMethod.fieldDateCreated, Query.Direction.DESCENDING)
                .limit(CommonMethod.paginationMaxLoad);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            PaymentModel model = documentSnapshot.toObject(PaymentModel.class);
                            model.setDocumentId(documentSnapshot.getId());
                            paymentModels.add(model);
                        }

                        if (queryDocumentSnapshots.size() >= CommonMethod.paginationMaxLoad){
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
                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
