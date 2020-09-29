package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tamanpelajar.aldy.difacademy.Adapter.OpAnggotaKelasBlendedAdapter;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.AnggotaKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.KelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class OpAnggotaKelasBlendedActivity extends AppCompatActivity {
    private ImageView imgBanner;
    private TextView tvJudul;
    private RecyclerView rvAnggota;

    private KelasBlendedModel kelasBlendedModel;
    private ArrayList<AnggotaKelasBlendedModel> models;
    private OpAnggotaKelasBlendedAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_anggota_kelas_blended);

        models = new ArrayList<>();

        initView();
        checkIntent();
        setRecyclerView();
        getData();
    }

    private void initView(){
        imgBanner = findViewById(R.id.img_op_anggota_kelas_banner);
        tvJudul = findViewById(R.id.tv_op_anggota_kelas_judul);
        rvAnggota = findViewById(R.id.rv_anggota_kelas);
    }

    private void checkIntent(){
        Intent intent = getIntent();
        kelasBlendedModel = intent.getParcelableExtra(CommonMethod.intentKelasBlendedModel);
        if (kelasBlendedModel != null){
            Glide.with(this)
                    .load(kelasBlendedModel.getThumbnailUrl())
                    .into(imgBanner);

            tvJudul.setText(kelasBlendedModel.getTitle());
        }
    }

    private void setRecyclerView(){
        rvAnggota.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new OpAnggotaKelasBlendedAdapter(this, models);
        rvAnggota.setAdapter(adapter);
    }

    private void getData(){
        CollectionReference ref = db.collection(CommonMethod.refKelasBlended)
                .document(kelasBlendedModel.getDocumentId())
                .collection(CommonMethod.refAnggota);

        ref.orderBy(CommonMethod.fieldDateCreated, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            AnggotaKelasBlendedModel model = documentSnapshot.toObject(AnggotaKelasBlendedModel.class);
                            model.setDocumentId(documentSnapshot.getId());
                            models.add(model);
                        }

                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpAnggotaKelasBlendedActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}