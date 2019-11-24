package com.example.aldy.difacademy.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Activity.OpNotifPaymentActivity;
import com.example.aldy.difacademy.Model.PaymentModel;
import com.example.aldy.difacademy.R;
import com.google.firebase.firestore.FirebaseFirestore;

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
        holder.tvKelas.setText(paymentModel.getNamaKelas());
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
        private TextView tvNama, tvKelas, tvStatus;
        private ConstraintLayout clContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_c_op_notif_payment_nama);
            tvKelas = itemView.findViewById(R.id.tv_c_op_notif_payment_kelas);
            tvStatus = itemView.findViewById(R.id.tv_c_op_notif_status_beli);
            clContainer = itemView.findViewById(R.id.cl_c_op_notif_payment);
        }
    }

}
