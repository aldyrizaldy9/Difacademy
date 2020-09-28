package com.tamanpelajar.aldy.difacademy.Adapter;

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

import com.tamanpelajar.aldy.difacademy.ActivityUser.UsWatchVideoActivity;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.VideoBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class UsVideoBlendedAdapter extends RecyclerView.Adapter<UsVideoBlendedAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VideoBlendedModel> models;

    public UsVideoBlendedAdapter(Context context, ArrayList<VideoBlendedModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public UsVideoBlendedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_video_course, parent, false);
        return new UsVideoBlendedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsVideoBlendedAdapter.ViewHolder holder, int position) {
        final VideoBlendedModel model = models.get(position);
        holder.tvJudul.setText(model.getTitle());
        int episode = position + 1;
        holder.tvEpisode.setText("#" + episode);
        holder.imgStatus.setImageResource(R.drawable.ic_play_arrow);
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UsWatchVideoActivity.class);
                intent.putExtra(CommonMethod.intentVideoBlendedModel, model);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
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
