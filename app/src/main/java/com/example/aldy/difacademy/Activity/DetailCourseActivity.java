package com.example.aldy.difacademy.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.aldy.difacademy.R;

public class DetailCourseActivity extends AppCompatActivity {
//    private ImageView imgThumbnail;
//    private TextView tvJudul, tvTag, tvDetail, tvLampiran;
//    private ConstraintLayout clListVideo;
//
//    private BlendedCourseModel blendedCourseModel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_course);
//        initView();
//        setViewWithParcelable();
//        onClick();
//    }
//
//    private void initView() {
//        imgThumbnail = findViewById(R.id.img_detail_course_thumbnail);
//        tvJudul = findViewById(R.id.tv_detail_course_judul);
//        tvTag = findViewById(R.id.tv_detail_course_tag);
//        tvDetail = findViewById(R.id.tv_detail_course_detail);
//        tvLampiran = findViewById(R.id.tv_detail_course_lampiran);
//        clListVideo = findViewById(R.id.cl_detail_course_list_video);
//    }
//
//    private void setViewWithParcelable() {
//        Intent intent = getIntent();
//        blendedCourseModel = intent.getParcelableExtra("blendedCourseModel");
//        Glide.with(this).load(blendedCourseModel.getThumbnailUrl()).into(imgThumbnail);
//        tvJudul.setText(blendedCourseModel.getTitle());
//        tvTag.setText(blendedCourseModel.getTag());
//        tvDetail.setText(blendedCourseModel.getDescription());
//    }
//
//    private void onClick() {
//        if (blendedCourseModel.getgDriveUrl() == null || blendedCourseModel.getgDriveUrl().equals("")){
//            tvLampiran.setVisibility(View.GONE);
//        } else {
//            tvLampiran.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setData(Uri.parse(blendedCourseModel.getgDriveUrl()));
//                        startActivity(intent);
//                    } catch (ActivityNotFoundException e) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        String urlNew = "http://" + blendedCourseModel.getgDriveUrl();
//                        intent.setData(Uri.parse(urlNew));
//                        startActivity(intent);
//                    }
//                }
//            });
//        }
//        clListVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DetailCourseActivity.this, ListVideoCourseActivity.class);
//                intent.putExtra("BLENDED_COURSE_ID", blendedCourseModel.getDocumentId());
//                startActivity(intent);
//            }
//        });
//    }
//
}
