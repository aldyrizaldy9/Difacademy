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

import com.tamanpelajar.aldy.difacademy.ActivityUser.UsPaymentActivity;
import com.tamanpelajar.aldy.difacademy.ActivityUser.UsWatchVideoOnlineActivity;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.VideoOnlineModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

import static com.tamanpelajar.aldy.difacademy.ActivityUser.UsListVideoOnlineActivity.IS_PAID;

public class UsVideoOnlineAdapter extends RecyclerView.Adapter<UsVideoOnlineAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VideoOnlineModel> models;

    public UsVideoOnlineAdapter(Context context, ArrayList<VideoOnlineModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public UsVideoOnlineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_video_course, parent, false);
        return new UsVideoOnlineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsVideoOnlineAdapter.ViewHolder holder, int position) {
        final VideoOnlineModel model = models.get(position);
        holder.tvJudul.setText(model.getTitle());
        int episode = position + 1;
        holder.tvEpisode.setText("#" + episode);
        holder.imgStatus.setImageResource(R.drawable.ic_play_arrow);
        if (position != 0) {
            if (!IS_PAID) {
                holder.imgStatus.setImageResource(R.drawable.ic_lock);
                holder.clContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UsPaymentActivity.class);
                        intent.putExtra(CommonMethod.intentVideoOnlineModel, model);
                        context.startActivity(intent);
                    }
                });
            } else {
                holder.imgStatus.setImageResource(R.drawable.ic_play_arrow);
                holder.clContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UsWatchVideoOnlineActivity.class);
                        intent.putExtra(CommonMethod.intentVideoOnlineModel, model);
                        context.startActivity(intent);
                    }
                });
            }
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_play_arrow);
            holder.clContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UsWatchVideoOnlineActivity.class);
                    intent.putExtra(CommonMethod.intentVideoOnlineModel, model);
                    context.startActivity(intent);
                }
            });
        }

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
