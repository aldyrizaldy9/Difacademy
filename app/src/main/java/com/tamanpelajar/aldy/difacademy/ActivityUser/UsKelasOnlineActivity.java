package com.tamanpelajar.aldy.difacademy.ActivityUser;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tamanpelajar.aldy.difacademy.Adapter.UsKelasOnlineAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.KelasOnlineModel;
import com.tamanpelajar.aldy.difacademy.Model.TagModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class UsKelasOnlineActivity extends AppCompatActivity {
    private static final String TAG = "OnlineKelasActivity";
    private boolean firstClick = true;
    private String tag = "";
    private ConstraintLayout clBack;
    private ConstraintLayout clSearch;
    private ConstraintLayout clSearchContainer;
    private RecyclerView rvKelas;
    private UsKelasOnlineAdapter adapter;
    private ArrayList<KelasOnlineModel> kelasOnlineModels;
    private ArrayList<String> tags;
    private ProgressDialog pd;
    private Spinner spnTags;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference kelasRef = firebaseFirestore.collection(CommonMethod.refKelasOnline);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_online_kelas);
        initView();
        onClick();
        setRecyclerView();
        loadTagsData();
        loadKelasData();
    }

    private void initView() {
        ConstraintLayout clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navCoklat));
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clSearch = findViewById(R.id.cl_icon3);
        clSearch.setVisibility(View.VISIBLE);
        clSearchContainer = findViewById(R.id.cl_online_course_search);
        ImageView imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        ImageView imgSearch = findViewById(R.id.img_icon3);
        imgSearch.setImageResource(R.drawable.ic_search);
        TextView tvNavBar = findViewById(R.id.tv_navbar);
        tvNavBar.setText("Kelas Online");
        rvKelas = findViewById(R.id.rv_online_course_course);
        spnTags = findViewById(R.id.spn_online_course_search);

        pd = new ProgressDialog(this);
        pd.setMessage("Memuat...");
        pd.setCancelable(false);
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
        kelasOnlineModels = new ArrayList<>();
        adapter = new UsKelasOnlineAdapter(this, kelasOnlineModels);
        rvKelas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvKelas.setAdapter(adapter);
    }

    private void loadKelasData() {
        pd.show();

        kelasRef
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        kelasOnlineModels.clear();
                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                KelasOnlineModel courseModel = documentSnapshot.toObject(KelasOnlineModel.class);
                                courseModel.setDocumentId(documentSnapshot.getId());

                                kelasOnlineModels.add(courseModel);
                            }
                        }
                        adapter.notifyDataSetChanged();
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

    private void loadCourseWithTheSameTag() {
        pd.show();

        kelasRef
                .whereEqualTo("tag", tag)
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        kelasOnlineModels.clear();
                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                KelasOnlineModel kelasOnlineModel = documentSnapshot.toObject(KelasOnlineModel.class);
                                kelasOnlineModel.setDocumentId(documentSnapshot.getId());
                                kelasOnlineModels.add(kelasOnlineModel);
                            }
                        }
                        adapter.notifyDataSetChanged();
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

    private void loadTagsData() {
        tags = new ArrayList<>();
        tags.add("Tags");
        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference tagsRef = firebaseFirestore.collection("Tags");
        tagsRef.orderBy("tag", Query.Direction.ASCENDING)
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
                    tag = tags.get(position);
                    clSearchContainer.setVisibility(View.GONE);
                    if (!CommonMethod.isInternetAvailable(UsKelasOnlineActivity.this)) {
                        return;
                    }

                    loadCourseWithTheSameTag();
                } else if (firstClick) {
                    firstClick = false;
                } else {
                    tag = "";
                    clSearchContainer.setVisibility(View.GONE);
                    if (!CommonMethod.isInternetAvailable(UsKelasOnlineActivity.this)) {
                        return;
                    }
                    loadKelasData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
