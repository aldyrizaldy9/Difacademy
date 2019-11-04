package com.example.aldy.difacademy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Model.VideoKelasModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class VideoKelasAdapter extends RecyclerView.Adapter<VideoKelasAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VideoKelasModel> videoKelasModels;

    public VideoKelasAdapter(Context context, ArrayList<VideoKelasModel> videoKelasModels) {
        this.context = context;
        this.videoKelasModels = videoKelasModels;
    }

    @NonNull
    @Override
    public VideoKelasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_video_course, parent, false);
        return new VideoKelasAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoKelasAdapter.ViewHolder holder, int position) {
        final VideoKelasModel videoKelasModel = videoKelasModels.get(position);
        holder.tvJudul.setText(videoKelasModel.getJudul());
        holder.tvDurasi.setText(videoKelasModel.getDurasi());
        holder.tvEpisode.setText(videoKelasModel.getEpisode());
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, videoKelasModel.getJudul(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoKelasModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJudul, tvDurasi, tvEpisode;
        private ConstraintLayout clContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tv_judul);
            tvDurasi = itemView.findViewById(R.id.tv_panjang_video);
            tvEpisode = itemView.findViewById(R.id.tv_card_video_episode);
            clContainer = itemView.findViewById(R.id.cl_card_video_course_container);
        }
    }
}
