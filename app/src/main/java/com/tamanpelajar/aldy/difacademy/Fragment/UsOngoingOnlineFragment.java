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

import com.tamanpelajar.aldy.difacademy.Adapter.UsMateriOnlineAdapter;
import com.tamanpelajar.aldy.difacademy.Model.MateriOnlineModel;
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

import static com.tamanpelajar.aldy.difacademy.ActivityUser.UsOngoingActivity.USER_DOC_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsOngoingOnlineFragment extends Fragment {

    private static final String TAG = "OngoingOnlineFragment";
    DocumentSnapshot lastVisible;
    boolean loadbaru;
    CollectionReference ongoingMateriRef;
    private RecyclerView rvOngoingOnline;
    private ArrayList<MateriOnlineModel> materiOnlineModels;
    private UsMateriOnlineAdapter usMateriOnlineAdapter;
    private ProgressDialog pd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private View rootView;

    public UsOngoingOnlineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_us_ongoing_online, container, false);
        initView();
        setRecyclerView();
        loadOngoingMateri();
        return rootView;
    }

    private void initView() {
        rvOngoingOnline = rootView.findViewById(R.id.rv_ongoing_online);
        pd = new ProgressDialog(rootView.getContext());

        ongoingMateriRef = db
                .collection("User")
                .document(USER_DOC_ID)
                .collection("OngoingOnlineMateri");
    }

    private void setRecyclerView() {
        materiOnlineModels = new ArrayList<>();
        usMateriOnlineAdapter = new UsMateriOnlineAdapter(rootView.getContext(), materiOnlineModels);

        final LinearLayoutManager manager = new LinearLayoutManager(rootView.getContext(), RecyclerView.VERTICAL, false);
        rvOngoingOnline.setLayoutManager(manager);
        rvOngoingOnline.setAdapter(usMateriOnlineAdapter);

        rvOngoingOnline.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (manager.findLastVisibleItemPosition() >= materiOnlineModels.size() - 10 &&
                        lastVisible != null &&
                        loadbaru) {
                    loadbaru = false;
                    Query load = ongoingMateriRef
                            .orderBy("dateCreated", Query.Direction.DESCENDING)
                            .startAfter(lastVisible)
                            .limit(20);

                    load.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    materiOnlineModels.clear();
                                    if (queryDocumentSnapshots.size() > 0) {
                                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
//                                            OngoingMateriModel ongoingMateriModel = queryDocumentSnapshot.toObject(OngoingMateriModel.class);
//
//                                            DocumentReference onlineMateriRef = db
//                                                    .collection("OnlineCourse")
//                                                    .document(ongoingMateriModel.getCourseId())
//                                                    .collection("OnlineMateri")
//                                                    .document(ongoingMateriModel.getMateriId());
//
//                                            onlineMateriRef
//                                                    .get()
//                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                                        @Override
//                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                            MateriModel materiModel = documentSnapshot.toObject(MateriModel.class);
//                                                            if (materiModel != null) {
//                                                                materiModel.setDocumentId(documentSnapshot.getId());
//                                                            }
//                                                            materiOnlineModels.add(materiModel);
//                                                            usMateriOnlineAdapter.notifyDataSetChanged();
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
                                        }

                                        if (queryDocumentSnapshots.size() < 20) {
                                            lastVisible = null;
                                        } else {
                                            lastVisible = queryDocumentSnapshots.getDocuments()
                                                    .get(queryDocumentSnapshots.size() - 1);
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadbaru = true;
                                    Log.d(TAG, e.toString());
                                }
                            });

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void loadOngoingMateri() {
        pd.setMessage("Memuat...");
        pd.setCancelable(false);
        pd.show();

        Query first = ongoingMateriRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(20);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        materiOnlineModels.clear();
                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
//                                OngoingMateriModel ongoingMateriModel = queryDocumentSnapshot.toObject(OngoingMateriModel.class);
//                                loadOngoingMateriDetail(ongoingMateriModel.getCourseId(), ongoingMateriModel.getMateriId());
                            }

                            if (queryDocumentSnapshots.size() < 20) {
                                lastVisible = null;
                            } else {
                                lastVisible = queryDocumentSnapshots.getDocuments()
                                        .get(queryDocumentSnapshots.size() - 1);
                            }
                        }
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void loadOngoingMateriDetail(final String courseId, String materiId) {
        DocumentReference onlineMateriref = db
                .collection("OnlineCourse")
                .document(courseId)
                .collection("OnlineMateri")
                .document(materiId);
        onlineMateriref
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        MateriModel materiModel = documentSnapshot.toObject(MateriModel.class);
//                        if (materiModel != null) {
//                            materiModel.setDocumentId(documentSnapshot.getId());
//                        }
//                        materiOnlineModels.add(materiModel);
//                        usMateriOnlineAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Log.d(TAG, e.toString());
                    }
                });
    }

}