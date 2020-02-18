package com.example.aldy.difacademy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aldy.difacademy.Activity.DetailCourseActivity;
import com.example.aldy.difacademy.Activity.OnlineMateriActivity;
import com.example.aldy.difacademy.Model.CourseModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CourseModel> courseModels;

    public CourseAdapter(Context context, ArrayList<CourseModel> courseModels) {
        this.context = context;
        this.courseModels = courseModels;
    }

    @NonNull
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_video_with_thumbnail, parent, false);
        return new CourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, int position) {
        final CourseModel courseModel = courseModels.get(position);
        holder.tvJudul.setText(courseModel.getTitle());
        holder.tvTag.setText(courseModel.getTag());
        holder.tvHarga.setText(courseModel.getHarga());
        Glide
                .with(context)
                .load(courseModel.getThumbnailUrl())
                .into(holder.imgThumbnail);
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailCourseActivity.class);
                intent.putExtra("courseModel", courseModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;
        private TextView tvJudul, tvTag, tvHarga;
        private ConstraintLayout clContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_card_video_thumbnail);
            tvJudul = itemView.findViewById(R.id.tv_card_video_thumbnail_judul);
            tvTag = itemView.findViewById(R.id.tv_card_video_thumbnail_tag);
            clContainer = itemView.findViewById(R.id.cl_card_video_thumbnail_container);
            tvHarga = itemView.findViewById(R.id.tv_card_video_thumbnail_harga);
        }
    }
}
