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

import com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpAddOnlineVideoActivity;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.VideoBlendedModel;
import com.tamanpelajar.aldy.difacademy.Model.VideoOnlineModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class OpVideoOnlineAdapter extends RecyclerView.Adapter<OpVideoOnlineAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VideoOnlineModel> models;

    public OpVideoOnlineAdapter(Context context, ArrayList<VideoOnlineModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public OpVideoOnlineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpVideoOnlineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpVideoOnlineAdapter.ViewHolder holder, final int position) {
        holder.tvTag.setVisibility(View.GONE);
        holder.imgThumbnail.setVisibility(View.GONE);

        final VideoOnlineModel model = models.get(position);
        holder.tvJudul.setText(model.getTitle());
        holder.tvDeskripsi.setText(model.getDescription());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpAddOnlineVideoActivity.class);
                intent.putExtra(CommonMethod.intentVideoBlendedModel, model);
                intent.putExtra(CommonMethod.intentIndex, position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
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
