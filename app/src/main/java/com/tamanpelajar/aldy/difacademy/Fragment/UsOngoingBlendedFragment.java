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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tamanpelajar.aldy.difacademy.Adapter.UsKelasBlendedAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.KelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

import static com.tamanpelajar.aldy.difacademy.ActivityUser.UsOngoingActivity.USER_DOC_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsOngoingBlendedFragment extends Fragment {
    private static final String TAG = "UsOngoingBlendedFragmen";
    DocumentSnapshot lastVisible;
    boolean loadbaru;
    CollectionReference ongoingKelasRef;
    private RecyclerView rvOngoingBlended;
    private ArrayList<KelasBlendedModel> kelasBlendedModels;
    private UsKelasBlendedAdapter usKelasBlendedAdapter;
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
        loadOngoingKelas();
        return rootView;
    }

    private void initView() {
        rvOngoingBlended = rootView.findViewById(R.id.rv_ongoing_blended);
        pd = new ProgressDialog(rootView.getContext());

        ongoingKelasRef = db
                .collection(CommonMethod.refUser)
                .document(USER_DOC_ID)
                .collection(CommonMethod.refOngoingKelasBlended);
    }

    private void setRecyclerView() {
        kelasBlendedModels = new ArrayList<>();
        usKelasBlendedAdapter = new UsKelasBlendedAdapter(rootView.getContext(), kelasBlendedModels);

        final LinearLayoutManager manager = new LinearLayoutManager(rootView.getContext(), RecyclerView.VERTICAL, false);
        rvOngoingBlended.setLayoutManager(manager);
        rvOngoingBlended.setAdapter(usKelasBlendedAdapter);

        rvOngoingBlended.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (manager.findLastVisibleItemPosition() >= kelasBlendedModels.size() - 10 &&
                        lastVisible != null &&
                        loadbaru) {
                    loadbaru = false;
                    Query load = ongoingKelasRef
                            .orderBy("dateCreated", Query.Direction.DESCENDING)
                            .startAfter(lastVisible)
                            .limit(20);

                    load.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    kelasBlendedModels.clear();
                                    if (queryDocumentSnapshots.size() > 0) {
                                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                            OngoingKelasBlendedModel ongoingKelasBlendedModel = queryDocumentSnapshot.toObject(OngoingKelasBlendedModel.class);

                                            DocumentReference kelasBlendedRef = db
                                                    .collection(CommonMethod.refKelasBlended)
                                                    .document(ongoingKelasBlendedModel.getKelasId());

                                            kelasBlendedRef
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            KelasBlendedModel kelasBlendedModel = documentSnapshot.toObject(KelasBlendedModel.class);
                                                            if (kelasBlendedModel != null) {
                                                                kelasBlendedModel.setDocumentId(documentSnapshot.getId());
                                                            }
                                                            kelasBlendedModels.add(kelasBlendedModel);
                                                            usKelasBlendedAdapter.notifyDataSetChanged();
                                                            loadbaru = true;
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

    private void loadOngoingKelas() {
        pd.setMessage("Memuat...");
        pd.setCancelable(false);
        pd.show();

        Query first = ongoingKelasRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(20);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        kelasBlendedModels.clear();
                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                OngoingKelasBlendedModel ongoingKelasBlendedModel = queryDocumentSnapshot.toObject(OngoingKelasBlendedModel.class);
                                loadOngoingKelasDetail(ongoingKelasBlendedModel.getKelasId());
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

    private void loadOngoingKelasDetail(final String courseId) {
        DocumentReference kelasBlendedRef = db
                .collection(CommonMethod.refKelasBlended)
                .document(courseId);
        kelasBlendedRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        KelasBlendedModel kelasBlendedModel = documentSnapshot.toObject(KelasBlendedModel.class);
                        if (kelasBlendedModel != null) {
                            kelasBlendedModel.setDocumentId(documentSnapshot.getId());
                        }
                        kelasBlendedModels.add(kelasBlendedModel);
                        usKelasBlendedAdapter.notifyDataSetChanged();
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