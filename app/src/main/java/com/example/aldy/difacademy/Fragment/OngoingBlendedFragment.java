package com.example.aldy.difacademy.Fragment;


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

import com.example.aldy.difacademy.Activity.OngoingCourseActivity;
import com.example.aldy.difacademy.Adapter.CourseAdapter;
import com.example.aldy.difacademy.Model.CourseModel;
import com.example.aldy.difacademy.Model.OngoingKelasBlendedModel;
import com.example.aldy.difacademy.R;
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

import static com.example.aldy.difacademy.Activity.OngoingCourseActivity.USER_DOC_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class OngoingBlendedFragment extends Fragment {
    private static final String TAG = "ganteng";

    ProgressDialog pd;
    View v;
    RecyclerView rvBlended;
    ArrayList<CourseModel> courseModels;
    CourseAdapter courseAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentSnapshot lastVisible;
    boolean loadbaru;

    public OngoingBlendedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_ongoing_blended, container, false);

        courseModels = new ArrayList<>();
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

    private void setRecyclerView() {
        courseAdapter = new CourseAdapter(v.getContext(), courseModels);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(), RecyclerView.VERTICAL, false);
        rvBlended.setLayoutManager(layoutManager);
        rvBlended.setAdapter(courseAdapter);

        rvBlended.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (layoutManager.findLastVisibleItemPosition() >= courseModels.size() - 10 &&
                        lastVisible != null &&
                        loadbaru) {

                    loadbaru = false;
                    CollectionReference colRef = db.collection("User")
                            .document(USER_DOC_ID)
                            .collection("OngoingBlendedCourse");

                    Query load = colRef
                            .orderBy("dateCreated", Query.Direction.DESCENDING)
                            .startAfter(lastVisible)
                            .limit(20);

                    load.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    courseModels.clear();

                                    if (queryDocumentSnapshots.size() > 0) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            OngoingKelasBlendedModel ongoingModel = documentSnapshot.toObject(OngoingKelasBlendedModel.class);

                                            DocumentReference blendedRef = db.collection("BlendedCourse")
                                                    .document(ongoingModel.getKelasBlendedId());
                                            blendedRef.get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            CourseModel courseModel = documentSnapshot.toObject(CourseModel.class);
                                                            courseModel.setDocumentId(documentSnapshot.getId());
                                                            courseModels.add(courseModel);
                                                            courseAdapter.notifyDataSetChanged();
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
                                    loadbaru = true;
                                    pd.dismiss();
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

    private void loadData() {
        pd.setMessage("Memuat...");
        pd.show();

        CollectionReference colRef = db.collection("User")
                .document(USER_DOC_ID)
                .collection("OngoingBlendedCourse");

        Query first = colRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(20);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        courseModels.clear();

                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                OngoingKelasBlendedModel ongoingModel = documentSnapshot.toObject(OngoingKelasBlendedModel.class);

                                DocumentReference blendedRef = db.collection("BlendedCourse")
                                        .document(ongoingModel.getKelasBlendedId());
                                blendedRef.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                CourseModel courseModel = documentSnapshot.toObject(CourseModel.class);
                                                courseModel.setDocumentId(documentSnapshot.getId());
                                                courseModels.add(courseModel);
                                                courseAdapter.notifyDataSetChanged();
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
