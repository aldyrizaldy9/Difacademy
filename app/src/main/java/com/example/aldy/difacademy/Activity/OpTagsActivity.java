package com.example.aldy.difacademy.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
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

import com.example.aldy.difacademy.Adapter.TagAdapter;
import com.example.aldy.difacademy.Model.TagModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OpTagsActivity extends AppCompatActivity {

    private static final String TAG = "OpTagsActivity";

    TextView tvNavbar;
    ConstraintLayout clBack;
    ImageView imgBack;

    RecyclerView rvTags;
    EditText edtAddTag;
    Button btnTambah;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference tagsRef = db.collection("Tags");

    ArrayList<TagModel> tagModels;
    TagAdapter tagAdapter;

    private ProgressDialog progressDialog;

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

        edtAddTag.requestFocus();
        progressDialog = new ProgressDialog(this);
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
                String tag = edtAddTag.getText().toString();
                if (tag.length() != 0) {
                    edtAddTag.setText("");
                    tambahTag(tag);
                } else {
                    Toast.makeText(OpTagsActivity.this, getString(R.string.data_not_complete), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadTag() {
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tagRef = db.collection("Tags");
        tagRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            TagModel tagModel = documentSnapshot.toObject(TagModel.class);
                            tagModel.setTagid(documentSnapshot.getId());

                            tagModels.add(new TagModel(tagModel.getTag(), tagModel.getTagid()));
                        }
                        tagAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void setRecyclerView() {
        tagModels = new ArrayList<>();
        tagAdapter = new TagAdapter(OpTagsActivity.this, tagModels);
        rvTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvTags.setAdapter(tagAdapter);
    }

    private void tambahTag(final String tag) {
        TagModel tagModel = new TagModel(tag);
        tagsRef.add(tagModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        TagModel newtagModel = new TagModel(tag, documentReference.getId());
                        tagModels.add(newtagModel);
                        tagAdapter.notifyDataSetChanged();

                        Toast.makeText(OpTagsActivity.this, "Tag berhasil ditambahkan", Toast.LENGTH_SHORT).show();
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
