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
import com.tamanpelajar.aldy.difacademy.Activity.OpAddNewsActivity;
import com.tamanpelajar.aldy.difacademy.Model.NewsModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class OpNewsAdapter extends RecyclerView.Adapter<OpNewsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<NewsModel> newsModels;

    public OpNewsAdapter(Context context, ArrayList<NewsModel> newsModels) {
        this.context = context;
        this.newsModels = newsModels;
    }

    @NonNull
    @Override
    public OpNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpNewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpNewsAdapter.ViewHolder holder, final int position) {
        final NewsModel newsModel = newsModels.get(position);
        holder.tvJudul.setText(newsModel.getJudul());
        holder.tvTag.setVisibility(View.GONE);
        holder.tvDeskripsi.setText(newsModel.getIsi());
        Glide.with(context).load(newsModel.getLinkfoto()).into(holder.imgThumbnail);
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpAddNewsActivity.class);
                intent.putExtra("newsModel", newsModel);
                intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJudul, tvTag, tvDeskripsi;
        private ImageView imgThumbnail;
        private ConstraintLayout clContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tv_c_op_judul);
            tvTag = itemView.findViewById(R.id.tv_c_op_tag);
            tvDeskripsi = itemView.findViewById(R.id.tv_c_op_deskripsi);
            imgThumbnail = itemView.findViewById(R.id.img_c_op_thumbnail);
            clContainer = itemView.findViewById(R.id.cl_c_op);
        }
    }
}
