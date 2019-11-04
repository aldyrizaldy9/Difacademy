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

import com.example.aldy.difacademy.Activity.DetailCourseActivity;
import com.example.aldy.difacademy.Model.VideoFreeModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class FreeCourseAdapter extends RecyclerView.Adapter<FreeCourseAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VideoFreeModel> freeModels;

    public FreeCourseAdapter(Context context, ArrayList<VideoFreeModel> freeModels) {
        this.context = context;
        this.freeModels = freeModels;
    }

    @NonNull
    @Override
    public FreeCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_video_with_thumbnail, parent, false);
        return new FreeCourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FreeCourseAdapter.ViewHolder holder, int position) {
        VideoFreeModel videoFreeModel = freeModels.get(position);
        holder.tvJudul.setText(videoFreeModel.getJudul());
        holder.tvTag.setText(videoFreeModel.getTag());
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailCourseActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return freeModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;
        private TextView tvJudul, tvTag;
        private ConstraintLayout clContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_card_video_thumbnail);
            tvJudul = itemView.findViewById(R.id.tv_card_video_thumbnail_judul);
            tvTag = itemView.findViewById(R.id.tv_card_video_thumbnail_tag);
            clContainer = itemView.findViewById(R.id.cl_card_video_thumbnail_container);
        }
    }
}
