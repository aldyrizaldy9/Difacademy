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
import com.tamanpelajar.aldy.difacademy.ActivityUser.UsDetailNewsActivity;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.NewsModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class UsNewsAdapter extends RecyclerView.Adapter<UsNewsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<NewsModel> models;

    public UsNewsAdapter(Context context, ArrayList<NewsModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public UsNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_news, parent, false);
        return new UsNewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsNewsAdapter.ViewHolder holder, int position) {
        final NewsModel model = models.get(position);
        Glide.with(context).load(model.getLinkfoto()).into(holder.imgThumbnail);
        holder.tvJudul.setText(model.getJudul());
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UsDetailNewsActivity.class);
                intent.putExtra(CommonMethod.intentNewsModel, model);
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
        private TextView tvJudul;
        private ConstraintLayout clContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_c_berita_thumbnail);
            tvJudul = itemView.findViewById(R.id.tv_c_berita_judul);
            clContainer = itemView.findViewById(R.id.cl_card_news_container);
        }
    }
}
