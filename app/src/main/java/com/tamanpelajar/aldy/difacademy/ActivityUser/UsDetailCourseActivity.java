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
import com.tamanpelajar.aldy.difacademy.Model.CourseModel;
import com.tamanpelajar.aldy.difacademy.R;

public class UsDetailCourseActivity extends AppCompatActivity {
    private ImageView imgThumbnail;
    private TextView tvJudul, tvTag, tvDetail, tvLampiran;
    private Button btnDaftarMateri;
    private String jenisKelas;

    private CourseModel courseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_detail_course);
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
        courseModel = intent.getParcelableExtra("courseModel");
        Glide.with(this).load(courseModel.getThumbnailUrl()).into(imgThumbnail);
        tvJudul.setText(courseModel.getTitle());
        tvTag.setText(courseModel.getTag());
        tvDetail.setText(courseModel.getDescription());
        jenisKelas = intent.getStringExtra("jenisKelas");
    }

    private void onClick() {
        if (courseModel.getGoogleDrive() == null || courseModel.getGoogleDrive().equals("")) {
            tvLampiran.setVisibility(View.GONE);
        } else {
            tvLampiran.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(courseModel.getGoogleDrive()));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String urlNew = "http://" + courseModel.getGoogleDrive();
                        intent.setData(Uri.parse(urlNew));
                        startActivity(intent);
                    }
                }
            });
        }

        btnDaftarMateri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (jenisKelas.equalsIgnoreCase("online")) {
                    intent = new Intent(UsDetailCourseActivity.this, UsOnlineMateriActivity.class);

                } else {
                    intent = new Intent(UsDetailCourseActivity.this, UsBlendedMateriActivity.class);

                }
                intent.putExtra("courseModel", courseModel);
                startActivity(intent);
            }
        });

    }

}
