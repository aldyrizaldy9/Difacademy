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

import com.example.aldy.difacademy.Activity.OpAddOnlineSoalActivity;
import com.example.aldy.difacademy.Activity.OpAddOnlineVideoActivity;
import com.example.aldy.difacademy.Model.OnlineSoalModel;
import com.example.aldy.difacademy.Model.OnlineVideoModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class OpOnlineSoalAdapter extends RecyclerView.Adapter<OpOnlineSoalAdapter.ViewHolder> {
    private Context context;
    private ArrayList<OnlineSoalModel> onlineSoalModels;

    public OpOnlineSoalAdapter(Context context, ArrayList<OnlineSoalModel> onlineSoalModels) {
        this.context = context;
        this.onlineSoalModels = onlineSoalModels;
    }

    @NonNull
    @Override
    public OpOnlineSoalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpOnlineSoalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpOnlineSoalAdapter.ViewHolder holder, final int position) {
        holder.tvTag.setVisibility(View.GONE);
        holder.imgThumbnail.setVisibility(View.GONE);
        holder.tvDeskripsi.setVisibility(View.GONE);

        final OnlineSoalModel model = onlineSoalModels.get(position);
        holder.tvJudul.setText(position + 1 + ". " + model.getSoal());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpAddOnlineSoalActivity.class);
                intent.putExtra("online_soal_model", model);
                intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return onlineSoalModels.size();
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
