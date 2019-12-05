package com.example.aldy.difacademy;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Adapter.OpNotifPaymentAdapter;
import com.example.aldy.difacademy.Model.PaymentModel;
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
public class OpNotifPaymentFragment extends Fragment {
    private static final String TAG = "OpNotifPaymentFragment";
    public static OpNotifPaymentAdapter OP_NOTIF_PAYMENT_ADAPTER;
    private View rootView;
    private RecyclerView rvNotifPayment;
    private ArrayList<PaymentModel> paymentModels;
    private ProgressDialog progressDialog;
    private CollectionReference paymentRef;

    public OpNotifPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_op_notif_payment, container, false);
        initView();
        setRecyclerView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initView() {
        rvNotifPayment = rootView.findViewById(R.id.rv_op_notif_payment);
        progressDialog = new ProgressDialog(rootView.getContext());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        paymentRef = firebaseFirestore.collection("Payment");
    }

    private void setRecyclerView() {
        paymentModels = new ArrayList<>();
        OP_NOTIF_PAYMENT_ADAPTER = new OpNotifPaymentAdapter(rootView.getContext(), paymentModels);
        rvNotifPayment.setLayoutManager(new LinearLayoutManager(rootView.getContext(), RecyclerView.VERTICAL, false));
        rvNotifPayment.setAdapter(OP_NOTIF_PAYMENT_ADAPTER);
    }

    private void loadData() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        paymentRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        paymentModels.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            PaymentModel paymentModel = queryDocumentSnapshot.toObject(PaymentModel.class);
                            paymentModel.setPaymentId(queryDocumentSnapshot.getId());

                            paymentModels.add(paymentModel);
                        }
                        progressDialog.dismiss();
                        OP_NOTIF_PAYMENT_ADAPTER.notifyDataSetChanged();
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

}
