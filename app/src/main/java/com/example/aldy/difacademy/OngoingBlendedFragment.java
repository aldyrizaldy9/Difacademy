package com.example.aldy.difacademy;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aldy.difacademy.Activity.OngoingCourseActivity;
import com.example.aldy.difacademy.Adapter.BlendedCourseAdapter;
import com.example.aldy.difacademy.Model.BlendedCourseModel;
import com.example.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OngoingBlendedFragment extends Fragment {
    private static final String TAG = "ganteng";

    ProgressDialog pd;
    View v;
    RecyclerView rvBlended;
    ArrayList<BlendedCourseModel> blendedCourseModels;
    BlendedCourseAdapter adapter;

    public OngoingBlendedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_ongoing_blended, container, false);

        blendedCourseModels = new ArrayList<>();
        rvBlended = v.findViewById(R.id.rv_ongoing_blended);
        pd = new ProgressDialog(v.getContext());

        setRecyclerView();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void setRecyclerView(){
        adapter = new BlendedCourseAdapter(v.getContext(), blendedCourseModels);
        rvBlended.setLayoutManager(new LinearLayoutManager(v.getContext(), RecyclerView.VERTICAL, false));
        rvBlended.setAdapter(adapter);
    }

    private void loadData(){
        pd.setMessage("Memuat...");
        pd.show();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("User")
                .document(OngoingCourseActivity.userDocId)
                .collection("OngoingBlendedCourse");

        colRef.orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        blendedCourseModels.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            OngoingKelasBlendedModel ongoingKelasBlendedModel = documentSnapshot.toObject(OngoingKelasBlendedModel.class);

                            DocumentReference blendedRef = db.collection("BlendedCourse")
                                    .document(ongoingKelasBlendedModel.getKelasBlendedId());
                            blendedRef.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            BlendedCourseModel blendedCourseModel = documentSnapshot.toObject(BlendedCourseModel.class);
                                            blendedCourseModel.setDocumentId(documentSnapshot.getId());
                                            blendedCourseModels.add(blendedCourseModel);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }
}
