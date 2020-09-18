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
import com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddFreeCourseActivity;
import com.tamanpelajar.aldy.difacademy.Model.VideoFreeModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class OpFreeCourseAdapter extends RecyclerView.Adapter<OpFreeCourseAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VideoFreeModel> videoFreeModels;

    public OpFreeCourseAdapter(Context context, ArrayList<VideoFreeModel> videoFreeModels) {
        this.context = context;
        this.videoFreeModels = videoFreeModels;
    }

    @NonNull
    @Override
    public OpFreeCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpFreeCourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpFreeCourseAdapter.ViewHolder holder, int position) {
        final VideoFreeModel videoFreeModel = videoFreeModels.get(position);
        holder.tvDeskripsi.setText(videoFreeModel.getDescription());
        holder.tvJudul.setText(videoFreeModel.getTitle());
        holder.tvTag.setText(videoFreeModel.getTag());
        Glide.with(context)
                .load(videoFreeModel.getThumbnailUrl())
                .into(holder.imgThumbnail);
        holder.clEdit.setVisibility(View.GONE);
        holder.clHapus.setVisibility(View.GONE);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pindah ke detail
                Intent intent = new Intent(context, OpAddFreeCourseActivity.class);
                intent.putExtra("video_free_model", videoFreeModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoFreeModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJudul, tvTag, tvDeskripsi;
        private ImageView imgThumbnail;
        private ConstraintLayout clEdit, clHapus, container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tv_c_op_judul);
            tvTag = itemView.findViewById(R.id.tv_c_op_tag);
            tvDeskripsi = itemView.findViewById(R.id.tv_c_op_deskripsi);
            imgThumbnail = itemView.findViewById(R.id.img_c_op_thumbnail);
            clEdit = itemView.findViewById(R.id.cl_c_op_edit);
            clHapus = itemView.findViewById(R.id.cl_c_op_delete);
            container = itemView.findViewById(R.id.cl_c_op);
        }
    }
}
