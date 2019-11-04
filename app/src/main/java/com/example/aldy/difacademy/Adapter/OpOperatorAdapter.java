package com.example.aldy.difacademy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Model.OperatorModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class OpOperatorAdapter extends RecyclerView.Adapter<OpOperatorAdapter.ViewHolder> {
    private Context context;
    private ArrayList<OperatorModel> operatorModels;

    public OpOperatorAdapter(Context context, ArrayList<OperatorModel> operatorModels) {
        this.context = context;
        this.operatorModels = operatorModels;
    }

    @NonNull
    @Override
    public OpOperatorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpOperatorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpOperatorAdapter.ViewHolder holder, int position) {
        OperatorModel operatorModel = operatorModels.get(position);
        holder.tvJudul.setText(operatorModel.getJudul());
        holder.tvTag.setText(operatorModel.getTag());
        holder.tvDeskripsi.setText(operatorModel.getDeskripsi());
        holder.clEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
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
        return operatorModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJudul, tvTag, tvDeskripsi;
        private ImageView ivThumbnail;
        private ConstraintLayout clEdit, clHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tv_c_op_judul);
            tvTag = itemView.findViewById(R.id.tv_c_op_tag);
            tvDeskripsi = itemView.findViewById(R.id.tv_c_op_deskripsi);
            ivThumbnail = itemView.findViewById(R.id.img_c_op_thumbnail);
            clEdit = itemView.findViewById(R.id.cl_c_op_edit);
            clHapus = itemView.findViewById(R.id.cl_c_op_delete);
        }
    }
}
