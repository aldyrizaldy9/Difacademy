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

import com.bumptech.glide.Glide;
import com.tamanpelajar.aldy.difacademy.ActivityUser.UsListVideoOnlineActivity;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.MateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class UsMateriOnlineAdapter extends RecyclerView.Adapter<UsMateriOnlineAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MateriOnlineModel> models;

    public UsMateriOnlineAdapter(Context context, ArrayList<MateriOnlineModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public UsMateriOnlineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_video_with_thumbnail, parent, false);
        return new UsMateriOnlineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsMateriOnlineAdapter.ViewHolder holder, int position) {
        final MateriOnlineModel model = models.get(position);
        holder.tvJudul.setText(model.getTitle());
        holder.tvTag.setVisibility(View.GONE);
        holder.tvHarga.setText("Rp " + model.getHarga());
        Glide
                .with(context)
                .load(model.getThumbnailUrl())
                .into(holder.imgThumbnail);
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UsListVideoOnlineActivity.class);
                intent.putExtra(CommonMethod.intentMateriOnlineModel, model);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
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
            tvHarga = itemView.findViewById(R.id.tv_card_video_thumbnail_harga);
            clContainer = itemView.findViewById(R.id.cl_card_video_thumbnail_container);
        }
    }
}
