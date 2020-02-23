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
import com.example.aldy.difacademy.Activity.ListVideoBlendedActivity;
import com.example.aldy.difacademy.Model.MateriModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class BlendedMateriAdapter extends RecyclerView.Adapter<BlendedMateriAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MateriModel> materiModels;

    public BlendedMateriAdapter(Context context, ArrayList<MateriModel> materiModels) {
        this.context = context;
        this.materiModels = materiModels;
    }

    @NonNull
    @Override
    public BlendedMateriAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_video_with_thumbnail, parent, false);
        return new BlendedMateriAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlendedMateriAdapter.ViewHolder holder, int position) {
        final MateriModel materiModel = materiModels.get(position);
        holder.tvJudul.setText(materiModel.getTitle());
        holder.tvTag.setText("Rp " + materiModel.getHarga());
        Glide
                .with(context)
                .load(materiModel.getThumbnailUrl())
                .into(holder.imgThumbnail);
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListVideoBlendedActivity.class);
                intent.putExtra("jenisKelas", "blended");
                intent.putExtra("materiModel", materiModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return materiModels.size();
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
