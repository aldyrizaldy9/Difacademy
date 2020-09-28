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

import com.tamanpelajar.aldy.difacademy.Adapter.UsMateriBlendedAdapter;
import com.tamanpelajar.aldy.difacademy.R;
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

import static com.tamanpelajar.aldy.difacademy.ActivityUser.UsOngoingCourseActivity.USER_DOC_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsOngoingBlendedFragment extends Fragment {

    private static final String TAG = "OngoingBlendedFragment";
    DocumentSnapshot lastVisible;
    boolean loadbaru;
    CollectionReference ongoingMateriRef;
    private RecyclerView rvOngoingBlended;
//    private ArrayList<MateriModel> materiModels;
    private UsMateriBlendedAdapter usMateriBlendedAdapter;
    private ProgressDialog pd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private View rootView;

    public UsOngoingBlendedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_us_ongoing_blended, container, false);
        initView();
        setRecyclerView();
        loadOngoingMateri();
        return rootView;
    }

    private void initView() {
        rvOngoingBlended = rootView.findViewById(R.id.rv_ongoing_blended);
        pd = new ProgressDialog(rootView.getContext());

        ongoingMateriRef = db
                .collection("User")
                .document(USER_DOC_ID)
                .collection("OngoingBlendedMateri");
    }

    private void setRecyclerView() {
//        materiModels = new ArrayList<>();
//        usMateriBlendedAdapter = new UsMateriBlendedAdapter(rootView.getContext(), materiModels);
//
//        final LinearLayoutManager manager = new LinearLayoutManager(rootView.getContext(), RecyclerView.VERTICAL, false);
//        rvOngoingBlended.setLayoutManager(manager);
//        rvOngoingBlended.setAdapter(usMateriBlendedAdapter);
//
//        rvOngoingBlended.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (manager.findLastVisibleItemPosition() >= materiModels.size() - 10 &&
//                        lastVisible != null &&
//                        loadbaru) {
//                    loadbaru = false;
//                    Query load = ongoingMateriRef
//                            .orderBy("dateCreated", Query.Direction.DESCENDING)
//                            .startAfter(lastVisible)
//                            .limit(20);
//
//                    load.get()
//                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                    materiModels.clear();
//                                    if (queryDocumentSnapshots.size() > 0) {
//                                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
//                                            OngoingMateriModel ongoingMateriModel = queryDocumentSnapshot.toObject(OngoingMateriModel.class);
//
//                                            DocumentReference blendedMateriRef = db
//                                                    .collection("BlendedCourse")
//                                                    .document(ongoingMateriModel.getCourseId())
//                                                    .collection("BlendedMateri")
//                                                    .document(ongoingMateriModel.getMateriId());
//
//                                            blendedMateriRef
//                                                    .get()
//                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                                        @Override
//                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                            MateriModel materiModel = documentSnapshot.toObject(MateriModel.class);
//                                                            if (materiModel != null) {
//                                                                materiModel.setDocumentId(documentSnapshot.getId());
//                                                            }
//                                                            materiModels.add(materiModel);
//                                                            usMateriBlendedAdapter.notifyDataSetChanged();
//                                                            loadbaru = true;
//                                                        }
//                                                    })
//                                                    .addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            loadbaru = true;
//                                                            Log.d(TAG, e.toString());
//                                                        }
//                                                    });
//                                        }
//
//                                        if (queryDocumentSnapshots.size() < 20) {
//                                            lastVisible = null;
//                                        } else {
//                                            lastVisible = queryDocumentSnapshots.getDocuments()
//                                                    .get(queryDocumentSnapshots.size() - 1);
//                                        }
//                                    }
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    loadbaru = true;
//                                    Log.d(TAG, e.toString());
//                                }
//                            });
//
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
    }

    private void loadOngoingMateri() {
//        pd.setMessage("Memuat...");
//        pd.setCancelable(false);
//        pd.show();
//
//        Query first = ongoingMateriRef
//                .orderBy("dateCreated", Query.Direction.DESCENDING)
//                .limit(20);
//
//        first.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        materiModels.clear();
//                        if (queryDocumentSnapshots.size() > 0) {
//                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
//                                OngoingMateriModel ongoingMateriModel = queryDocumentSnapshot.toObject(OngoingMateriModel.class);
//                                loadOngoingMateriDetail(ongoingMateriModel.getCourseId(), ongoingMateriModel.getMateriId());
//                            }
//
//                            if (queryDocumentSnapshots.size() < 20) {
//                                lastVisible = null;
//                            } else {
//                                lastVisible = queryDocumentSnapshots.getDocuments()
//                                        .get(queryDocumentSnapshots.size() - 1);
//                            }
//                        }
//                        pd.dismiss();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        pd.dismiss();
//                        Log.d(TAG, e.toString());
//                    }
//                });
//    }
//
//    private void loadOngoingMateriDetail(final String courseId, String materiId) {
//        DocumentReference blendedMateriref = db
//                .collection("BlendedCourse")
//                .document(courseId)
//                .collection("BlendedMateri")
//                .document(materiId);
//        blendedMateriref
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        MateriModel materiModel = documentSnapshot.toObject(MateriModel.class);
//                        if (materiModel != null) {
//                            materiModel.setDocumentId(documentSnapshot.getId());
//                        }
//                        materiModels.add(materiModel);
//                        usMateriBlendedAdapter.notifyDataSetChanged();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        pd.dismiss();
//                        Log.d(TAG, e.toString());
//                    }
//                });
    }

}