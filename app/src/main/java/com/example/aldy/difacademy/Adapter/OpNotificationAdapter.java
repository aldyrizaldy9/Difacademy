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
import com.example.aldy.difacademy.Activity.OpNotifPaymentActivity;
import com.example.aldy.difacademy.Model.OpNotificationModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class OpNotificationAdapter extends RecyclerView.Adapter<OpNotificationAdapter.ViewHolder> {
    private Context context;
    private ArrayList<OpNotificationModel> opNotificationModels;

    public OpNotificationAdapter(Context context, ArrayList<OpNotificationModel> opNotificationModels) {
        this.context = context;
        this.opNotificationModels = opNotificationModels;
    }

    @NonNull
    @Override
    public OpNotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op_notif, parent, false);
        return new OpNotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpNotificationAdapter.ViewHolder holder, int position) {
        OpNotificationModel opNotificationModel = opNotificationModels.get(position);
        holder.tvNama.setText(opNotificationModel.getNama());
        holder.tvKelas.setText(opNotificationModel.getKelas());
        holder.tvStatus.setText(opNotificationModel.getStatus());
        holder.clContainerPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpNotifGradActivity.class);
                context.startActivity(intent);
            }
        });
        holder.clContainerGrad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpNotifPaymentActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return opNotificationModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNama, tvKelas, tvStatus;
        private ConstraintLayout clContainerPay, clContainerGrad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            tvNama = itemView.findViewById(R.id.tv_hubungi_nama);
//            tvKelas = itemView.findViewById(R.id.tv_hubungi_kelas);
//            tvStatus = itemView.findViewById(R.id.tv_status_hubungi);
//            clContainerPay = itemView.findViewById(R.id.cl_op_notif_beli);
//            clContainerGrad = itemView.findViewById(R.id.cl_op_notif_hubungi);
        }
    }
}
