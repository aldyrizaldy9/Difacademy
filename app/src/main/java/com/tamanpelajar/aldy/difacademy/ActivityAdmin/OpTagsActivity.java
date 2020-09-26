package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tamanpelajar.aldy.difacademy.Adapter.OpTagAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.TagModel;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OpTagsActivity extends AppCompatActivity {

    private static final String TAG = "OpTagsActivity";

    private TextView tvNavbar;
    private ConstraintLayout clBack;
    private ImageView imgBack;

    private RecyclerView rvTags;
    private EditText edtAddTag;
    private Button btnTambah;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tagsRef = db.collection(CommonMethod.refTags);

    private ArrayList<TagModel> tagModels;
    private OpTagAdapter opTagAdapter;

    private SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_tags);
        initView();
        onClick();
        setRecyclerView();
        loadTag();
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Tags");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);

        rvTags = findViewById(R.id.rv_op_tags_tags);
        edtAddTag = findViewById(R.id.edt_op_tags_tag);
        btnTambah = findViewById(R.id.btn_op_tags_simpan);
        srl = findViewById(R.id.srl_op_tags);

        edtAddTag.requestFocus();

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tagModels.clear();
                opTagAdapter.notifyDataSetChanged();
                loadTag();
            }
        });
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonMethod.isInternetAvailable(OpTagsActivity.this)) {
                    String tag = edtAddTag.getText().toString();
                    if (tag.length() != 0) {
                        edtAddTag.setText("");
                        tambahTag(tag);
                    } else {
                        Toast.makeText(OpTagsActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void loadTag() {
        tagsRef
                .orderBy("tag", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            TagModel tagModel = documentSnapshot.toObject(TagModel.class);
                            tagModel.setTagid(documentSnapshot.getId());

                            tagModels.add(new TagModel(tagModel.getTag(), tagModel.getTagid()));
                        }
                        opTagAdapter.notifyDataSetChanged();
                        srl.setRefreshing(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        srl.setRefreshing(false);
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void setRecyclerView() {
        tagModels = new ArrayList<>();
        opTagAdapter = new OpTagAdapter(OpTagsActivity.this, tagModels);
        rvTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvTags.setAdapter(opTagAdapter);
    }

    private void tambahTag(final String tag) {
        TagModel tagModel = new TagModel(tag);
        tagsRef.add(tagModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        TagModel newtagModel = new TagModel(tag, documentReference.getId());
                        tagModels.add(newtagModel);
                        opTagAdapter.notifyDataSetChanged();

//                        Toast.makeText(OpTagsActivity.this, "Tag berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpTagsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
