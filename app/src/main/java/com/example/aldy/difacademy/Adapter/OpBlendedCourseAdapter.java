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
import com.example.aldy.difacademy.Activity.OpAddBlendedCourseActivity;
import com.example.aldy.difacademy.Model.BlendedCourseModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class OpBlendedCourseAdapter extends RecyclerView.Adapter<OpBlendedCourseAdapter.ViewHolder> {
    private Context context;
    private ArrayList<BlendedCourseModel> blendedCourseModels;

    public OpBlendedCourseAdapter(Context context, ArrayList<BlendedCourseModel> blendedCourseModels) {
        this.context = context;
        this.blendedCourseModels = blendedCourseModels;
    }

    @NonNull
    @Override
    public OpBlendedCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpBlendedCourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpBlendedCourseAdapter.ViewHolder holder, int position) {
        final BlendedCourseModel blendedCourseModel = blendedCourseModels.get(position);
        holder.tvDeskripsi.setText(blendedCourseModel.getDescription());
        holder.tvJudul.setText(blendedCourseModel.getTitle());
        holder.tvTag.setText(blendedCourseModel.getTag());
        Glide.with(context)
                .load(blendedCourseModel.getThumbnailUrl())
                .into(holder.imgThumbnail);
        holder.clEdit.setVisibility(View.GONE);
        holder.clHapus.setVisibility(View.GONE);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pindah ke detail
                Intent intent = new Intent(context, OpAddBlendedCourseActivity.class);
                intent.putExtra("blended_course_model", blendedCourseModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blendedCourseModels.size();
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
