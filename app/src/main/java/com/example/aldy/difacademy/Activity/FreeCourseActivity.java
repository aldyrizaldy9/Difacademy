package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Adapter.FreeCourseAdapter;
import com.example.aldy.difacademy.Model.TagModel;
import com.example.aldy.difacademy.Model.VideoFreeModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FreeCourseActivity extends AppCompatActivity {
    private ConstraintLayout clBack, clSearch, clSearchContainer;
    private RecyclerView rvVideo;
    private FreeCourseAdapter freeCourseAdapter;
    private ArrayList<VideoFreeModel> videoFreeModels;
    private ArrayList<String> tags;
    private ProgressDialog progressDialog;
    private Spinner spnTags;

    private FirebaseFirestore firebaseFirestore;
    CollectionReference videoFreeRef;

    private static final String TAG = "FreeCourseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_course);
        initView();
        onClick();
        setRecyclerView();
        loadVideoFreeData(true);
    }

    private void initView() {
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clSearch = findViewById(R.id.cl_icon3);
        clSearch.setVisibility(View.VISIBLE);
        clSearchContainer = findViewById(R.id.cl_free_course_search);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        ImageView imgSearch = findViewById(R.id.img_icon3);
        imgSearch.setImageResource(R.drawable.ic_search);
        TextView tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Free Video");
        rvVideo = findViewById(R.id.rv_free_course_video);
        progressDialog = new ProgressDialog(this);
        spnTags = findViewById(R.id.spn_free_course_search);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clSearchContainer.getVisibility() == View.GONE) {
                    clSearchContainer.setVisibility(View.VISIBLE);
                } else {
                    clSearchContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setRecyclerView() {
        videoFreeModels = new ArrayList<>();
        freeCourseAdapter = new FreeCourseAdapter(this, videoFreeModels);
        rvVideo.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvVideo.setAdapter(freeCourseAdapter);
    }

    private void loadVideoFreeData(final Boolean isEmpty) {
        progressDialog.setMessage("Memuat");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        videoFreeRef = firebaseFirestore.collection("VideoFree");
        videoFreeRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        videoFreeModels.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            VideoFreeModel videoFreeModel = queryDocumentSnapshot.toObject(VideoFreeModel.class);
                            videoFreeModel.setDocumentId(queryDocumentSnapshot.getId());

                            videoFreeModels.add(videoFreeModel);
                        }
                        if (isEmpty) {
                            loadTagsData();
                        } else {
                            progressDialog.dismiss();
                        }
                        freeCourseAdapter.notifyDataSetChanged();
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

    private void loadTagsData() {
        tags = new ArrayList<>();
        tags.add("Tags");
        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference tagsRef = firebaseFirestore.collection("Tags");
        tagsRef
                .orderBy("tag", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            TagModel tagModel = queryDocumentSnapshot.toObject(TagModel.class);
                            tagModel.setTagid(queryDocumentSnapshot.getId());

                            tags.add(tagModel.getTag());
                        }
                        customSpinner();
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

    private void customSpinner() {
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.support_simple_spinner_dropdown_item, tags) {
            @Override
            public boolean isEnabled(int position) {
                return position != -1;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.BLACK);
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnTags.setAdapter(spinnerArrayAdapter);
        spnTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    loadVideoFreeWithSameTag(tags.get(position));
                } else {
                    loadVideoFreeData(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        progressDialog.dismiss();
    }

    private void loadVideoFreeWithSameTag(String tag) {
        progressDialog.setMessage("Memuat");
        progressDialog.setCancelable(false);
        progressDialog.show();
        videoFreeRef
                .whereEqualTo("tag", tag)
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        videoFreeModels.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            VideoFreeModel videoFreeModel = queryDocumentSnapshot.toObject(VideoFreeModel.class);
                            videoFreeModel.setDocumentId(queryDocumentSnapshot.getId());

                            videoFreeModels.add(videoFreeModel);
                        }
                        progressDialog.dismiss();
                        freeCourseAdapter.notifyDataSetChanged();
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
