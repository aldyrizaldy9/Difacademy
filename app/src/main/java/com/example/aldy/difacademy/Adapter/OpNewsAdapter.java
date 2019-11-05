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

import com.bumptech.glide.Glide;
import com.example.aldy.difacademy.Activity.OpAddNewsActivity;
import com.example.aldy.difacademy.Model.OpNewsModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class OpNewsAdapter extends RecyclerView.Adapter<OpNewsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<OpNewsModel> opNewsModels;

    public OpNewsAdapter(Context context, ArrayList<OpNewsModel> opNewsModels) {
        this.context = context;
        this.opNewsModels = opNewsModels;
    }

    @NonNull
    @Override
    public OpNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpNewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpNewsAdapter.ViewHolder holder, int position) {
        OpNewsModel opNewsModel = opNewsModels.get(position);
        holder.tvJudul.setText(opNewsModel.getJudul());
        holder.tvTag.setVisibility(View.GONE);
        holder.tvDeskripsi.setText(opNewsModel.getDeskripsi());
//        Glide.with(context).load().into(holder.imgThumbnail);
        holder.clEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,OpAddNewsActivity.class);
                context.startActivity(intent);
            }
        });
        holder.clHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Hapus", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return opNewsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJudul, tvTag, tvDeskripsi;
        private ImageView imgThumbnail;
        private ConstraintLayout clEdit, clHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tv_c_op_judul);
            tvTag = itemView.findViewById(R.id.tv_c_op_tag);
            tvDeskripsi = itemView.findViewById(R.id.tv_c_op_deskripsi);
            imgThumbnail = itemView.findViewById(R.id.img_c_op_thumbnail);
            clEdit = itemView.findViewById(R.id.cl_c_op_edit);
            clHapus = itemView.findViewById(R.id.cl_c_op_delete);
        }
    }
}
