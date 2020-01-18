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

import com.example.aldy.difacademy.Activity.OpAddBlendedVideoActivity;
import com.example.aldy.difacademy.Activity.OpAddOnlineVideoActivity;
import com.example.aldy.difacademy.Model.VideoModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

import static com.example.aldy.difacademy.Activity.OpMainActivity.JENIS_KELAS;

public class OpVideoAdapter extends RecyclerView.Adapter<OpVideoAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VideoModel> videoModels;

    public OpVideoAdapter(Context context, ArrayList<VideoModel> videoModels) {
        this.context = context;
        this.videoModels = videoModels;
    }

    @NonNull
    @Override
    public OpVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpVideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpVideoAdapter.ViewHolder holder, final int position) {
        holder.tvTag.setVisibility(View.GONE);
        holder.imgThumbnail.setVisibility(View.GONE);

        final VideoModel model = videoModels.get(position);
        holder.tvDeskripsi.setText(model.getDescription());
        holder.tvJudul.setText(position + 1 + ". " + model.getTitle());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (JENIS_KELAS.equals("blended")){
                    intent = new Intent(context, OpAddBlendedVideoActivity.class);
                    intent.putExtra("blended_video_model", model);
                    intent.putExtra("index", position);
                    context.startActivity(intent);
                } else if (JENIS_KELAS.equals("online")){
                    intent = new Intent(context, OpAddOnlineVideoActivity.class);
                    intent.putExtra("online_video_model", model);
                    intent.putExtra("index", position);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoModels.size();
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
