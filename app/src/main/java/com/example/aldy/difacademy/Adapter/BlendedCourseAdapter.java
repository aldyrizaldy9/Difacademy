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
import com.example.aldy.difacademy.Model.BlendedCourseModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class BlendedCourseAdapter extends RecyclerView.Adapter<BlendedCourseAdapter.ViewHolder> {
    private Context context;
    private ArrayList<BlendedCourseModel> blendedCourseModels;

    public BlendedCourseAdapter(Context context, ArrayList<BlendedCourseModel> blendedCourseModels) {
        this.context = context;
        this.blendedCourseModels = blendedCourseModels;
    }

    @NonNull
    @Override
    public BlendedCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_video_with_thumbnail, parent, false);
        return new BlendedCourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlendedCourseAdapter.ViewHolder holder, int position) {
        final BlendedCourseModel blendedCourseModel = blendedCourseModels.get(position);
        holder.tvJudul.setText(blendedCourseModel.getTitle());
        holder.tvTag.setText(blendedCourseModel.getTag());
        //Nanti tvEpisode diganti dengan jumlah video pada blended course
        holder.tvEpisode.setVisibility(View.GONE);
        Glide.with(context)
                .load(blendedCourseModel.getThumbnailUrl())
                .into(holder.imgThumbnail);
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailCourseActivity.class);
                intent.putExtra("blendedCourseModel", blendedCourseModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blendedCourseModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;
        private TextView tvJudul, tvTag, tvEpisode;
        private ConstraintLayout clContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_card_video_thumbnail);
            tvJudul = itemView.findViewById(R.id.tv_card_video_thumbnail_judul);
            tvTag = itemView.findViewById(R.id.tv_card_video_thumbnail_tag);
            clContainer = itemView.findViewById(R.id.cl_card_video_thumbnail_container);
            tvEpisode = itemView.findViewById(R.id.tv_card_video_thumbnail_status_ongoing);
        }
    }
}
