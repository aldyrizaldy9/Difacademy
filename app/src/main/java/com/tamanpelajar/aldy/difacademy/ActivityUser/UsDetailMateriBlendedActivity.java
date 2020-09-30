package com.tamanpelajar.aldy.difacademy.ActivityUser;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.MateriBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

public class UsDetailMateriBlendedActivity extends AppCompatActivity {
    private ImageView imgThumbnail;
    private TextView tvJudul, tvTag, tvDetail;
    private Button btnDaftarVideo, btnLampiran;

    private MateriBlendedModel materiBlendedModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_detail_kelas);
        initView();
        setViewWithParcelable();
        onClick();
    }

    private void initView() {
        imgThumbnail = findViewById(R.id.img_detail_course_thumbnail);
        tvJudul = findViewById(R.id.tv_detail_course_judul);
        tvTag = findViewById(R.id.tv_detail_course_tag);
        tvTag.setVisibility(View.GONE);
        tvDetail = findViewById(R.id.tv_detail_course_detail);
        btnLampiran = findViewById(R.id.btn_detail_course_lampiran);
        btnDaftarVideo = findViewById(R.id.btn_detail_course_daftar_materi);
        btnDaftarVideo.setText("DAFTAR VIDEO");
    }

    private void setViewWithParcelable() {
        Intent intent = getIntent();
        materiBlendedModel = intent.getParcelableExtra(CommonMethod.intentMateriBlendedModel);
        Glide.with(this).load(materiBlendedModel.getThumbnailUrl()).into(imgThumbnail);
        tvJudul.setText(materiBlendedModel.getTitle());
        tvDetail.setText(materiBlendedModel.getDescription());
    }

    private void onClick() {
        if (materiBlendedModel.getLampiranUrl() == null || materiBlendedModel.getLampiranUrl().equals("")) {
            btnLampiran.setVisibility(View.GONE);
        } else {
            btnLampiran.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(materiBlendedModel.getLampiranUrl()));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String urlNew = "http://" + materiBlendedModel.getLampiranUrl();
                        intent.setData(Uri.parse(urlNew));
                        startActivity(intent);
                    }
                }
            });
        }

        btnDaftarVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsDetailMateriBlendedActivity.this, UsListVideoBlendedActivity.class);
                intent.putExtra(CommonMethod.intentMateriBlendedModel, materiBlendedModel);
                startActivity(intent);
            }
        });

    }

}
