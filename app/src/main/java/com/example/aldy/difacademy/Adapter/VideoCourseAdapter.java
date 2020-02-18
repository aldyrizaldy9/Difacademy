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

import com.example.aldy.difacademy.Activity.WatchVideoActivity;
import com.example.aldy.difacademy.Activity.WatchVideoBlendedActivity;
import com.example.aldy.difacademy.Model.VideoModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;


public class VideoCourseAdapter extends RecyclerView.Adapter<VideoCourseAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VideoModel> videoModels;

    public VideoCourseAdapter(Context context, ArrayList<VideoModel> videoModels) {
        this.context = context;
        this.videoModels = videoModels;
    }

    @NonNull
    @Override
    public VideoCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_video_course, parent, false);
        return new VideoCourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoCourseAdapter.ViewHolder holder, int position) {
        final VideoModel videoModel = videoModels.get(position);
        holder.tvJudul.setText(videoModel.getTitle());
        int episode = position + 1;
        holder.tvEpisode.setText("#" + episode);
        holder.imgStatus.setImageResource(R.drawable.ic_play_arrow);
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WatchVideoActivity.class);
                intent.putExtra("videoModel", videoModel);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJudul, tvEpisode;
        private ImageView imgStatus;
        private ConstraintLayout clContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tv_judul);
            tvEpisode = itemView.findViewById(R.id.tv_card_video_episode);
            imgStatus = itemView.findViewById(R.id.img_video_course_status);
            clContainer = itemView.findViewById(R.id.cl_card_video_course_container);
        }
    }
}
