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

import com.tamanpelajar.aldy.difacademy.Activity.OpAddBlendedSoalActivity;
import com.tamanpelajar.aldy.difacademy.Activity.OpAddOnlineSoalActivity;
import com.tamanpelajar.aldy.difacademy.Model.SoalModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

import static com.tamanpelajar.aldy.difacademy.Activity.OpMainActivity.OP_JENIS_KELAS;

public class OpSoalAdapter extends RecyclerView.Adapter<OpSoalAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SoalModel> soalModels;

    public OpSoalAdapter(Context context, ArrayList<SoalModel> soalModels) {
        this.context = context;
        this.soalModels = soalModels;
    }

    @NonNull
    @Override
    public OpSoalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpSoalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpSoalAdapter.ViewHolder holder, final int position) {
        holder.tvTag.setVisibility(View.GONE);
        holder.imgThumbnail.setVisibility(View.GONE);
        holder.tvDeskripsi.setVisibility(View.GONE);

        final SoalModel model = soalModels.get(position);
        holder.tvJudul.setText(position + 1 + ". " + model.getSoal());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (OP_JENIS_KELAS.equals("blended")) {
                    intent = new Intent(context, OpAddBlendedSoalActivity.class);
                    intent.putExtra("blended_soal_model", model);
                    intent.putExtra("index", position);
                    context.startActivity(intent);
                } else if (OP_JENIS_KELAS.equals("online")) {
                    intent = new Intent(context, OpAddOnlineSoalActivity.class);
                    intent.putExtra("online_soal_model", model);
                    intent.putExtra("index", position);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return soalModels.size();
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
