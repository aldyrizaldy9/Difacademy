package com.tamanpelajar.aldy.difacademy.Fragment;


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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tamanpelajar.aldy.difacademy.Adapter.OpGraduationMateriBlendedAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.GraduationMateriBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OpGraduationBlendedFragment extends Fragment {
    private static final String TAG = "OpNotifGraduationFragme";
    public static OpGraduationMateriBlendedAdapter OP_NOTIF_GRADUATION_ADAPTER;
    private View rootView;
    private RecyclerView rvNotifGrad;
    private ArrayList<GraduationMateriBlendedModel> graduationMateriBlendedModels;
    private ProgressDialog progressDialog;
    private CollectionReference graduationRef;

    public OpGraduationBlendedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_op_graduation_blended, container, false);
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
        rvNotifGrad = rootView.findViewById(R.id.rv_op_grad_blended);
        progressDialog = new ProgressDialog(rootView.getContext());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        graduationRef = firebaseFirestore.collection(CommonMethod.refGraduationBlended);
    }

    private void setRecyclerView() {
        graduationMateriBlendedModels = new ArrayList<>();
        OP_NOTIF_GRADUATION_ADAPTER = new OpGraduationMateriBlendedAdapter(rootView.getContext(), graduationMateriBlendedModels);
        rvNotifGrad.setLayoutManager(new LinearLayoutManager(rootView.getContext(), RecyclerView.VERTICAL, false));
        rvNotifGrad.setAdapter(OP_NOTIF_GRADUATION_ADAPTER);
    }

    private void loadData() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        graduationRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        graduationMateriBlendedModels.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            GraduationMateriBlendedModel graduationMateriBlendedModel = queryDocumentSnapshot.toObject(GraduationMateriBlendedModel.class);
                            graduationMateriBlendedModel.setDocumentId(queryDocumentSnapshot.getId());

                            graduationMateriBlendedModels.add(graduationMateriBlendedModel);
                        }
                        progressDialog.dismiss();
                        OP_NOTIF_GRADUATION_ADAPTER.notifyDataSetChanged();
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
