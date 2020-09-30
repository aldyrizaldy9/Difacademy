package com.tamanpelajar.aldy.difacademy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpNotifGradMateriOnlineActivity;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.GraduationMateriOnlineModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class OpGraduationMateriOnlineAdapter extends RecyclerView.Adapter<OpGraduationMateriOnlineAdapter.ViewHolder> {
    private Context context;
    private ArrayList<GraduationMateriOnlineModel> models;

    public OpGraduationMateriOnlineAdapter(Context context, ArrayList<GraduationMateriOnlineModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public OpGraduationMateriOnlineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op_graduation, parent, false);
        return new OpGraduationMateriOnlineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpGraduationMateriOnlineAdapter.ViewHolder holder, int position) {
        final GraduationMateriOnlineModel graduationMateriOnlineModel = models.get(position);
        holder.tvNama.setText(graduationMateriOnlineModel.getNamaUser());
        holder.tvKelas.setText(graduationMateriOnlineModel.getNamaMateri());
        if (graduationMateriOnlineModel.isDone()) {
            holder.tvStatus.setText("SUDAH DIHUBUNGI");
        } else {
            holder.tvStatus.setText("BELUM DIHUBUNGI");
        }
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpNotifGradMateriOnlineActivity.class);
                intent.putExtra(CommonMethod.intentGraduationModel, graduationMateriOnlineModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNama, tvKelas, tvStatus;
        private ConstraintLayout clContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_c_op_graduation_hubungi_nama);
            tvKelas = itemView.findViewById(R.id.tv_c_op_graduation_hubungi_kelas);
            tvStatus = itemView.findViewById(R.id.tv_c_op_graduation_status_hubungi);
            clContainer = itemView.findViewById(R.id.cl_c_op_graduation_hubungi);
        }
    }
}
