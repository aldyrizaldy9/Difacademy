package com.example.aldy.difacademy.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Model.BlendedCourseModel;
import com.example.aldy.difacademy.Model.PaymentModel;
import com.example.aldy.difacademy.Model.UserModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OpNotifPaymentAdapter extends RecyclerView.Adapter<OpNotifPaymentAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PaymentModel> paymentModels;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private static final String TAG = "OpNotifPaymentAdapter";
    private String namaUser, namaKelas;

    public OpNotifPaymentAdapter(Context context, ArrayList<PaymentModel> paymentModels) {
        this.context = context;
        this.paymentModels = paymentModels;
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op_notif_payment, parent, false);
        return new OpNotifPaymentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvNama.setText(getUserData(position));
        holder.tvKelas.setText(getCourseData(position));
//        holder.tvStatus.setText();
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    private String getUserData(int index) {
        CollectionReference userRef = firebaseFirestore.collection("User");
        userRef
                .whereEqualTo("userId", paymentModels.get(index).getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        UserModel userModel = new UserModel();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            userModel = queryDocumentSnapshot.toObject(UserModel.class);
                        }
                        namaUser = userModel.getNama();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
        return namaUser;
    }

    private String getCourseData(int index) {
        DocumentReference courseRef = firebaseFirestore
                .collection("BlendedCourse")
                .document(paymentModels.get(index).getBlendedCourseId());
        courseRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        BlendedCourseModel blendedCourseModel = documentSnapshot.toObject(BlendedCourseModel.class);
                        if (blendedCourseModel != null) {
                            namaKelas = blendedCourseModel.getTitle();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
        return namaKelas;
    }
}
