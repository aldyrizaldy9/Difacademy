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

import com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpNotifPaymentKelasBlendedActivity;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.PaymentKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class OpPaymentBlendedAdapter extends RecyclerView.Adapter<OpPaymentBlendedAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PaymentKelasBlendedModel> models;

    public OpPaymentBlendedAdapter(Context context, ArrayList<PaymentKelasBlendedModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op_payment, parent, false);
        return new OpPaymentBlendedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PaymentKelasBlendedModel paymentKelasBlendedModel = models.get(position);
        holder.tvNama.setText(paymentKelasBlendedModel.getNamaUser());
        holder.tvKelas.setText(paymentKelasBlendedModel.getNamaKelas());
        if (paymentKelasBlendedModel.isPaid()) {
            holder.tvStatus.setText("SUDAH DIPROSES");
        } else {
            holder.tvStatus.setText("INGIN BELI");
        }
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpNotifPaymentKelasBlendedActivity.class);
                intent.putExtra(CommonMethod.intentPaymentModel, paymentKelasBlendedModel);
                intent.putExtra(CommonMethod.intentJenisKelas, "blended");
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
            tvNama = itemView.findViewById(R.id.tv_c_op_payment_nama);
            tvKelas = itemView.findViewById(R.id.tv_c_op_payment_kelas);
            tvStatus = itemView.findViewById(R.id.tv_c_op_status_beli);
            clContainer = itemView.findViewById(R.id.cl_c_op_payment);
        }
    }

}
