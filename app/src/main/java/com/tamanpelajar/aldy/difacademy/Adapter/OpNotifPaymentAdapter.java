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

import com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpNotifPaymentActivity;
import com.tamanpelajar.aldy.difacademy.Model.PaymentModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class OpNotifPaymentAdapter extends RecyclerView.Adapter<OpNotifPaymentAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PaymentModel> paymentModels;

    public OpNotifPaymentAdapter(Context context, ArrayList<PaymentModel> paymentModels) {
        this.context = context;
        this.paymentModels = paymentModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op_notif_payment, parent, false);
        return new OpNotifPaymentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PaymentModel paymentModel = paymentModels.get(position);
        holder.tvNama.setText(paymentModel.getNamaUser());
        holder.tvMateri.setText(paymentModel.getNamaMateri());
        if (paymentModel.isPaid()) {
            holder.tvStatus.setText("SUDAH DIPROSES");
        } else {
            holder.tvStatus.setText("INGIN BELI");
        }
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpNotifPaymentActivity.class);
                intent.putExtra("paymentModel", paymentModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNama, tvMateri, tvStatus;
        private ConstraintLayout clContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_c_op_notif_payment_nama);
            tvMateri = itemView.findViewById(R.id.tv_c_op_notif_payment_kelas);
            tvStatus = itemView.findViewById(R.id.tv_c_op_notif_status_beli);
            clContainer = itemView.findViewById(R.id.cl_c_op_notif_payment);
        }
    }

}
