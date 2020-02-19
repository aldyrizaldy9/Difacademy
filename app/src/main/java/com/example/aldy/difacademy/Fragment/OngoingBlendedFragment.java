package com.example.aldy.difacademy.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import static com.example.aldy.difacademy.Activity.MainActivity.JENIS_KELAS;
import static com.example.aldy.difacademy.Activity.OngoingCourseActivity.USER_DOC_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class OngoingBlendedFragment extends Fragment {

    private RecyclerView rvOngoingBlended;
    private ArrayList<CourseModel> courseModels;
    private CourseAdapter adapter;

    private ProgressDialog pd;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ;

    private View rootView;

    private static final String TAG = "OngoingBlendedFragment";

    DocumentSnapshot lastVisible;
    boolean loadbaru;
    CollectionReference ongoingCoursesRef;

    public OngoingBlendedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_ongoing_blended, container, false);
        initView();
        setRecyclerView();
        loadOngoingCourses();
        return rootView;
    }

    private void initView() {
        rvOngoingBlended = rootView.findViewById(R.id.rv_ongoing_blended);
        pd = new ProgressDialog(rootView.getContext());

        ongoingCoursesRef = db
                .collection("User")
                .document(USER_DOC_ID)
                .collection("OngoingBlendedCourse");
    }

    private void setRecyclerView() {
        courseModels = new ArrayList<>();
        adapter = new CourseAdapter(rootView.getContext(), courseModels);

        final LinearLayoutManager manager = new LinearLayoutManager(rootView.getContext(), RecyclerView.VERTICAL, false);
        rvOngoingBlended.setLayoutManager(manager);
        rvOngoingBlended.setAdapter(adapter);

        rvOngoingBlended.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (manager.findLastVisibleItemPosition() >= courseModels.size() - 10 &&
                        lastVisible != null &&
                        loadbaru) {
                    loadbaru = false;
                    Query load = ongoingCoursesRef
                            .orderBy("dateCreated", Query.Direction.DESCENDING)
                            .startAfter(lastVisible)
                            .limit(20);

                    load.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    courseModels.clear();
                                    if (queryDocumentSnapshots.size() > 0) {
                                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                            OngoingKelasBlendedModel ongoingKelasBlendedModel = queryDocumentSnapshot.toObject(OngoingKelasBlendedModel.class);

                                            DocumentReference blendedCourseRef = db
                                                    .collection("BlendedCourse")
                                                    .document(ongoingKelasBlendedModel.getKelasBlendedId());
                                            blendedCourseRef
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            CourseModel courseModel = documentSnapshot.toObject(CourseModel.class);
                                                            if (courseModel != null) {
                                                                courseModel.setDocumentId(documentSnapshot.getId());
                                                            }
                                                            courseModels.add(courseModel);
                                                            adapter.notifyDataSetChanged();
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

    private void loadOngoingCourses() {
        pd.setMessage("Memuat...");
        pd.setCancelable(false);
        pd.show();

        Query first = ongoingCoursesRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(20);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        courseModels.clear();
                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                OngoingKelasBlendedModel ongoingKelasBlendedModel = queryDocumentSnapshot.toObject(OngoingKelasBlendedModel.class);
                                loadOngoingCoursesDetail(ongoingKelasBlendedModel.getKelasBlendedId());
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

    private void loadOngoingCoursesDetail(final String courseId) {
        DocumentReference blendedCourseRef = db
                .collection("BlendedCourse")
                .document(courseId);
        blendedCourseRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        CourseModel courseModel = documentSnapshot.toObject(CourseModel.class);
                        if (courseModel != null) {
                            courseModel.setDocumentId(documentSnapshot.getId());
                        }
                        courseModels.add(courseModel);
                        adapter.notifyDataSetChanged();
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