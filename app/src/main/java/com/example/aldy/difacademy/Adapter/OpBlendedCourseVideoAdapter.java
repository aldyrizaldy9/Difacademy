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

import com.example.aldy.difacademy.Activity.OpAddBlendedCourseVideoActivity;
import com.example.aldy.difacademy.Model.BlendedVideoModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class OpBlendedCourseVideoAdapter extends RecyclerView.Adapter<OpBlendedCourseVideoAdapter.ViewHolder>{

    private Context context;
    private ArrayList<BlendedVideoModel> blendedVideoModels;

    public OpBlendedCourseVideoAdapter(Context context, ArrayList<BlendedVideoModel> blendedVideoModels) {
        this.context = context;
        this.blendedVideoModels = blendedVideoModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpBlendedCourseVideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final BlendedVideoModel blendedVideoModel = blendedVideoModels.get(position);
        holder.tvDeskripsi.setText(blendedVideoModel.getDescription());
        holder.tvJudul.setText(blendedVideoModel.getTitle());
        holder.tvTag.setVisibility(View.GONE);
        holder.imgThumbnail.setVisibility(View.GONE);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pindah ke detail
                Intent intent = new Intent(context, OpAddBlendedCourseVideoActivity.class);
                intent.putExtra("blended_video_model", blendedVideoModel);
                intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blendedVideoModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJudul, tvTag, tvDeskripsi;
        private ImageView imgThumbnail;
        private ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tv_c_op_judul);
            tvTag = itemView.findViewById(R.id.tv_c_op_tag);
            tvDeskripsi = itemView.findViewById(R.id.tv_c_op_deskripsi);
            imgThumbnail = itemView.findViewById(R.id.img_c_op_thumbnail);
            container = itemView.findViewById(R.id.cl_c_op);
        }
    }
}
