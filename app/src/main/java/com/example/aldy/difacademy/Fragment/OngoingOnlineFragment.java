package com.example.aldy.difacademy.Fragment;


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

import com.example.aldy.difacademy.Adapter.CourseAdapter;
import com.example.aldy.difacademy.Model.CourseModel;
import com.example.aldy.difacademy.Model.OngoingKelasOnlineModel;
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
public class OngoingOnlineFragment extends Fragment {

    private RecyclerView rvOngoingOnline;
    private ArrayList<CourseModel> courseModels;
    private CourseAdapter courseAdapter;

    private ProgressDialog progressDialog;

    private FirebaseFirestore firebaseFirestore;

    private View rootView;

    private static final String TAG = "OngoingOnlineFragment";

    public OngoingOnlineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_ongoing_online, container, false);
        initView();
        setRecyclerView();
        loadOngoingCourses();
        return rootView;
    }

    private void initView() {
        JENIS_KELAS = "online";
        rvOngoingOnline = rootView.findViewById(R.id.rv_ongoing_online);
        progressDialog = new ProgressDialog(rootView.getContext());
    }

    private void setRecyclerView() {
        courseModels = new ArrayList<>();
        courseAdapter = new CourseAdapter(rootView.getContext(), courseModels);
        rvOngoingOnline.setLayoutManager(new LinearLayoutManager(rootView.getContext(), RecyclerView.VERTICAL, false));
        rvOngoingOnline.setAdapter(courseAdapter);
    }

    private void loadOngoingCourses() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference ongoingCoursesRef = firebaseFirestore
                .collection("User")
                .document(USER_DOC_ID)
                .collection("OngoingOnlineCourse");

        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ongoingCoursesRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            OngoingKelasOnlineModel ongoingKelasOnlineModel = queryDocumentSnapshot.toObject(OngoingKelasOnlineModel.class);
                            loadOngoingCoursesDetail(ongoingKelasOnlineModel.getKelasOnlineId());
                        }
                        progressDialog.dismiss();
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

    private void loadOngoingCoursesDetail(final String courseId) {
        DocumentReference onlineCourseRef = firebaseFirestore
                .collection("OnlineCourse")
                .document(courseId);
        onlineCourseRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        CourseModel courseModel = documentSnapshot.toObject(CourseModel.class);
                        if (courseModel != null) {
                            courseModel.setDocumentId(documentSnapshot.getId());
                        }
                        courseModels.add(courseModel);
                        courseAdapter.notifyDataSetChanged();
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