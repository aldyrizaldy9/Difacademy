package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Model.TagModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListBlendedCourseActivity extends AppCompatActivity {
//    private static final String TAG = "ListBlendedCourseActivity";
//    DocumentSnapshot lastVisible;
//    boolean loadbaru;
//    boolean loadFromTag = false;
//    boolean firstClick = true;
//    String tag = "";
//    private ConstraintLayout clBack, clSearch, clSearchContainer, clNavbar;
//    private RecyclerView rvVideo;
//    private BlendedCourseAdapter adapter;
//    private ArrayList<BlendedCourseModel> blendedCourseModels;
//    private ArrayList<String> tags;
//    private ProgressDialog pd;
//    private Spinner spnTags;
//    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//    CollectionReference courseRef = firebaseFirestore.collection("BlendedCourse");
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_list_blended_course);
//        initView();
//        onClick();
//        setRecyclerView();
//        loadTagsData();
//        loadBlendedCourseData();
//    }
//
//    private void initView() {
//        clNavbar = findViewById(R.id.cl_navbar);
//        clNavbar.setBackgroundColor(getResources().getColor(R.color.navCoklat));
//        clBack = findViewById(R.id.cl_icon1);
//        clBack.setVisibility(View.VISIBLE);
//        clSearch = findViewById(R.id.cl_icon3);
//        clSearch.setVisibility(View.VISIBLE);
//        clSearchContainer = findViewById(R.id.cl_blended_course_search);
//        ImageView imgBack = findViewById(R.id.img_icon1);
//        imgBack.setImageResource(R.drawable.ic_arrow_back);
//        ImageView imgSearch = findViewById(R.id.img_icon3);
//        imgSearch.setImageResource(R.drawable.ic_search);
//        TextView tvNavBar = findViewById(R.id.tv_navbar);
//        tvNavBar.setText("Kelas campuran");
//        rvVideo = findViewById(R.id.rv_blended_course_video);
//        spnTags = findViewById(R.id.spn_blended_course_search);
//
//        pd = new ProgressDialog(this);
//        pd.setMessage("Memuat...");
//        pd.setCancelable(false);
//    }
//
//    private void onClick() {
//        clBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        clSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (clSearchContainer.getVisibility() == View.GONE) {
//                    clSearchContainer.setVisibility(View.VISIBLE);
//                } else {
//                    clSearchContainer.setVisibility(View.GONE);
//                }
//            }
//        });
//    }
//
//    private void setRecyclerView() {
//        blendedCourseModels = new ArrayList<>();
//        adapter = new BlendedCourseAdapter(this, blendedCourseModels);
//
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        rvVideo.setLayoutManager(layoutManager);
//        rvVideo.setAdapter(adapter);
//
//        rvVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (layoutManager.findLastVisibleItemPosition() >= blendedCourseModels.size() - 10) {
//                    if (lastVisible != null) {
//                        if (loadbaru) {
//                            loadbaru = false;
//                            Query load;
//
//                            if (loadFromTag) {
//                                load = courseRef
//                                        .whereEqualTo("tag", tag)
//                                        .orderBy("dateCreated", Query.Direction.DESCENDING)
//                                        .startAfter(lastVisible)
//                                        .limit(20);
//                            } else {
//                                load = courseRef
//                                        .orderBy("dateCreated", Query.Direction.DESCENDING)
//                                        .startAfter(lastVisible)
//                                        .limit(20);
//                            }
//
//                            load.get()
//                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                            if (queryDocumentSnapshots.size() > 0) {
//                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                                    BlendedCourseModel blendedCourseModel = documentSnapshot.toObject(BlendedCourseModel.class);
//                                                    blendedCourseModel.setDocumentId(documentSnapshot.getId());
//
//                                                    blendedCourseModels.add(blendedCourseModel);
//                                                }
//
//                                                if (queryDocumentSnapshots.size() < 20) {
//                                                    lastVisible = null;
//                                                } else {
//                                                    lastVisible = queryDocumentSnapshots.getDocuments()
//                                                            .get(queryDocumentSnapshots.size() - 1);
//                                                }
//                                                adapter.notifyDataSetChanged();
//                                            }
//                                            loadbaru = true;
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            loadbaru = true;
//                                            Toast.makeText(ListBlendedCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
//    }
//
//    private void loadBlendedCourseData() {
//        pd.show();
//
//        Query first = courseRef
//                .orderBy("dateCreated", Query.Direction.DESCENDING)
//                .limit(20);
//
//        first.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        blendedCourseModels.clear();
//                        if (queryDocumentSnapshots.size() > 0) {
//                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                BlendedCourseModel blendedCourseModel = documentSnapshot.toObject(BlendedCourseModel.class);
//                                blendedCourseModel.setDocumentId(documentSnapshot.getId());
//
//                                blendedCourseModels.add(blendedCourseModel);
//                            }
//
//                            if (queryDocumentSnapshots.size() < 20) {
//                                lastVisible = null;
//                            } else {
//                                lastVisible = queryDocumentSnapshots.getDocuments()
//                                        .get(queryDocumentSnapshots.size() - 1);
//                            }
//
//                            adapter.notifyDataSetChanged();
//                        }
//                        pd.dismiss();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        pd.dismiss();
//                    }
//                });
//    }
//
//    private void loadBlendedCourseWithTheSameTag() {
//        pd.show();
//
//        Query first = courseRef
//                .whereEqualTo("tag", tag)
//                .orderBy("dateCreated", Query.Direction.DESCENDING)
//                .limit(20);
//
//        first.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        blendedCourseModels.clear();
//                        if (queryDocumentSnapshots.size() > 0) {
//                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                BlendedCourseModel blendedCourseModel = documentSnapshot.toObject(BlendedCourseModel.class);
//                                blendedCourseModel.setDocumentId(documentSnapshot.getId());
//
//                                blendedCourseModels.add(blendedCourseModel);
//                            }
//
//                            if (queryDocumentSnapshots.size() < 20) {
//                                lastVisible = null;
//                            } else {
//                                lastVisible = queryDocumentSnapshots.getDocuments()
//                                        .get(queryDocumentSnapshots.size() - 1);
//                            }
//
//                        }
//                        pd.dismiss();
//                        adapter.notifyDataSetChanged();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        pd.dismiss();
//                    }
//                });
//
//    }
//
//    private void loadTagsData() {
//        tags = new ArrayList<>();
//        tags.add("Tags");
//        firebaseFirestore = FirebaseFirestore.getInstance();
//        CollectionReference tagsRef = firebaseFirestore.collection("Tags");
//        tagsRef.orderBy("tag", Query.Direction.ASCENDING)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
//                            TagModel tagModel = queryDocumentSnapshot.toObject(TagModel.class);
//                            tagModel.setTagid(queryDocumentSnapshot.getId());
//
//                            tags.add(tagModel.getTag());
//                        }
//                        customSpinner();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                    }
//                });
//    }
//
//    private void customSpinner() {
//        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
//                this, R.layout.support_simple_spinner_dropdown_item, tags) {
//            @Override
//            public boolean isEnabled(int position) {
//                return position != -1;
//            }
//
//            @Override
//            public View getDropDownView(int position, View convertView,
//                                        ViewGroup parent) {
//                View view = super.getDropDownView(position, convertView, parent);
//                TextView tv = (TextView) view;
//                tv.setTextColor(Color.BLACK);
//                return view;
//            }
//        };
//        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        spnTags.setAdapter(spinnerArrayAdapter);
//        spnTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                if (position != 0) {
//                    tag = tags.get(position);
//                    clSearchContainer.setVisibility(View.GONE);
//                    loadFromTag = true;
//                    if (!isNetworkConnected()) {
//                        Toast.makeText(ListBlendedCourseActivity.this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        loadBlendedCourseWithTheSameTag();
//                    }
//                } else if (firstClick) {
//                    firstClick = false;
//                } else {
//                    tag = "";
//                    clSearchContainer.setVisibility(View.GONE);
//                    loadFromTag = false;
//                    if (!isNetworkConnected()) {
//                        Toast.makeText(ListBlendedCourseActivity.this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        loadBlendedCourseData();
//                    }
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }
//
//    private boolean isNetworkConnected() {
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
//    }
}
