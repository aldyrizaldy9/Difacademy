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
import com.tamanpelajar.aldy.difacademy.Model.KelasOnlineModel;
import com.tamanpelajar.aldy.difacademy.R;

public class UsDetailKelasOnlineActivity extends AppCompatActivity {
    private ImageView imgThumbnail;
    private TextView tvJudul, tvTag, tvDetail, tvLampiran;
    private Button btnDaftarMateri;

    private KelasOnlineModel kelasOnlineModel;

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
        tvDetail = findViewById(R.id.tv_detail_course_detail);
        tvLampiran = findViewById(R.id.tv_detail_course_lampiran);
        btnDaftarMateri = findViewById(R.id.btn_detail_course_daftar_materi);
    }

    private void setViewWithParcelable() {
        Intent intent = getIntent();
        kelasOnlineModel = intent.getParcelableExtra(CommonMethod.intentKelasOnlineModel);
        Glide.with(this).load(kelasOnlineModel.getThumbnailUrl()).into(imgThumbnail);
        tvJudul.setText(kelasOnlineModel.getTitle());
        tvTag.setText(kelasOnlineModel.getTag());
        tvDetail.setText(kelasOnlineModel.getDescription());
    }

    private void onClick() {
        if (kelasOnlineModel.getGoogleDrive() == null || kelasOnlineModel.getGoogleDrive().equals("")) {
            tvLampiran.setVisibility(View.GONE);
        } else {
            tvLampiran.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(kelasOnlineModel.getGoogleDrive()));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String urlNew = "http://" + kelasOnlineModel.getGoogleDrive();
                        intent.setData(Uri.parse(urlNew));
                        startActivity(intent);
                    }
                }
            });
        }

        btnDaftarMateri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsDetailKelasOnlineActivity.this, UsMateriOnlineActivity.class);
                intent.putExtra(CommonMethod.intentKelasOnlineModel, kelasOnlineModel);
                startActivity(intent);
            }
        });

    }

}
