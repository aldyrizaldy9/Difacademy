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

import com.tamanpelajar.aldy.difacademy.Adapter.OpNotifGraduationAdapter;
import com.tamanpelajar.aldy.difacademy.Model.GraduationModel;
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
public class OpNotifGraduationFragment extends Fragment {
    private static final String TAG = "OpNotifGraduationFragme";
    public static OpNotifGraduationAdapter OP_NOTIF_GRADUATION_ADAPTER;
    private View rootView;
    private RecyclerView rvNotifGrad;
    private ArrayList<GraduationModel> graduationModels;
    private ProgressDialog progressDialog;
    private CollectionReference graduationRef;

    public OpNotifGraduationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_op_notif_graduation, container, false);
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
        rvNotifGrad = rootView.findViewById(R.id.rv_op_notif_grad);
        progressDialog = new ProgressDialog(rootView.getContext());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        graduationRef = firebaseFirestore.collection("Graduation");
    }

    private void setRecyclerView() {
        graduationModels = new ArrayList<>();
        OP_NOTIF_GRADUATION_ADAPTER = new OpNotifGraduationAdapter(rootView.getContext(), graduationModels);
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
                        graduationModels.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            GraduationModel graduationModel = queryDocumentSnapshot.toObject(GraduationModel.class);
                            graduationModel.setGraduationId(queryDocumentSnapshot.getId());

                            graduationModels.add(graduationModel);
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
