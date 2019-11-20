package com.example.aldy.difacademy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Activity.WatchVideoBlendedActivity;
import com.example.aldy.difacademy.Model.BlendedVideoModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class BlendedCourseVideoAdapter extends RecyclerView.Adapter<BlendedCourseVideoAdapter.ViewHolder> {
    private Context context;
    private ArrayList<BlendedVideoModel> blendedVideoModels;

    public BlendedCourseVideoAdapter(Context context, ArrayList<BlendedVideoModel> blendedVideoModels) {
        this.context = context;
        this.blendedVideoModels = blendedVideoModels;
    }

    @NonNull
    @Override
    public BlendedCourseVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_video_course, parent, false);
        return new BlendedCourseVideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlendedCourseVideoAdapter.ViewHolder holder, int position) {
        final BlendedVideoModel blendedVideoModel = blendedVideoModels.get(position);
        holder.tvJudul.setText(blendedVideoModel.getTitle());
//        holder.tvPanjangVideo.setText();
        String episode = "#" + position + 1;
        holder.tvEpisode.setText(episode);
        if (position != 0) {
            holder.imgStatus.setImageResource(R.drawable.ic_lock);
            holder.clContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Belum bayar bos", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.clContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, WatchVideoBlendedActivity.class);
                    intent.putExtra("blended_video_model", blendedVideoModel);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return blendedVideoModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJudul, tvPanjangVideo, tvEpisode;
        private ImageView imgStatus;
        private ConstraintLayout clContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tv_judul);
            tvPanjangVideo = itemView.findViewById(R.id.tv_panjang_video);
            tvEpisode = itemView.findViewById(R.id.tv_card_video_episode);
            imgStatus = itemView.findViewById(R.id.img_video_course_status);
            clContainer = itemView.findViewById(R.id.cl_card_video_course_container);
        }
    }
}
