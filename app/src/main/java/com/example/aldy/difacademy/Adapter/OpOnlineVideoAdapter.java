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

import com.example.aldy.difacademy.Activity.OpAddOnlineVideoActivity;
import com.example.aldy.difacademy.Model.OnlineVideoModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class OpOnlineVideoAdapter extends RecyclerView.Adapter<OpOnlineVideoAdapter.ViewHolder> {
    private Context context;
    private ArrayList<OnlineVideoModel> onlineVideoModels;

    public OpOnlineVideoAdapter(Context context, ArrayList<OnlineVideoModel> onlineVideoModels) {
        this.context = context;
        this.onlineVideoModels = onlineVideoModels;
    }

    @NonNull
    @Override
    public OpOnlineVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_video_course, parent, false);
        return new OpOnlineVideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpOnlineVideoAdapter.ViewHolder holder, final int position) {
        holder.tvTag.setVisibility(View.GONE);
        holder.imgThumbnail.setVisibility(View.GONE);

        final OnlineVideoModel model = onlineVideoModels.get(position);
        holder.tvDeskripsi.setText(model.getDescription());
        holder.tvJudul.setText(model.getTitle());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpAddOnlineVideoActivity.class);
                intent.putExtra("online_video_model", model);
                intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return onlineVideoModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
