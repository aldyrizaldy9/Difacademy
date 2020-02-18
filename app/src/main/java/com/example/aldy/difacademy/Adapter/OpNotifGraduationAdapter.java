package com.example.aldy.difacademy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Activity.OpNotifGradActivity;
import com.example.aldy.difacademy.Model.GraduationModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class OpNotifGraduationAdapter extends RecyclerView.Adapter<OpNotifGraduationAdapter.ViewHolder> {
    private Context context;
    private ArrayList<GraduationModel> graduationModels;

    public OpNotifGraduationAdapter(Context context, ArrayList<GraduationModel> graduationModels) {
        this.context = context;
        this.graduationModels = graduationModels;
    }

    @NonNull
    @Override
    public OpNotifGraduationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op_notif_graduation, parent, false);
        return new OpNotifGraduationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpNotifGraduationAdapter.ViewHolder holder, int position) {
        final GraduationModel graduationModel = graduationModels.get(position);
        holder.tvNama.setText(graduationModel.getNamaUser());
        holder.tvKelas.setText(graduationModel.getNamaMateri());
        if (graduationModel.isDone()) {
            holder.tvStatus.setText("SUDAH DIHUBUNGI");
        } else {
            holder.tvStatus.setText("BELUM DIHUBUNGI");
        }
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpNotifGradActivity.class);
                intent.putExtra("graduationModel", graduationModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return graduationModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNama, tvKelas, tvStatus;
        private ConstraintLayout clContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_c_op_notif_hubungi_nama);
            tvKelas = itemView.findViewById(R.id.tv_c_op_notif_hubungi_kelas);
            tvStatus = itemView.findViewById(R.id.tv_c_op_notif_status_hubungi);
            clContainer = itemView.findViewById(R.id.cl_c_op_notif_hubungi);
        }
    }
}
