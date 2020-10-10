package com.tamanpelajar.aldy.difacademy.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Model.AnggotaKelasBlendedModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OpAnggotaKelasBlendedAdapter extends RecyclerView.Adapter<OpAnggotaKelasBlendedAdapter.ViewHolder> {
    private static final String TAG = "ganteng";
    private Context context;
    private ArrayList<AnggotaKelasBlendedModel> models;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OpAnggotaKelasBlendedAdapter(Context context, ArrayList<AnggotaKelasBlendedModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public OpAnggotaKelasBlendedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op_anggota, parent, false);
        return new OpAnggotaKelasBlendedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpAnggotaKelasBlendedAdapter.ViewHolder holder, final int position) {
        final AnggotaKelasBlendedModel model = models.get(position);

        Date date = new Date(model.getDateCreated() * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

        holder.tvNama.setText(model.getName());
        holder.tvTanggal.setText("Beli tanggal : " + sdf.format(date));
        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showHapusDialog(model, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    private void hapusAnggota(AnggotaKelasBlendedModel model) {
        /**
         * ambil user doc id dulu
         * hapus kelas di ongoingnya
         * terus hapus user di anggota
         */

        getUserDocId(model);
    }

    private void getUserDocId(final AnggotaKelasBlendedModel model) {
        CollectionReference ref = db.collection(CommonMethod.refUser);
        ref.whereEqualTo(CommonMethod.fieldUserId, model.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String docId = "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            docId = documentSnapshot.getId();
                        }
                        hapusOngoingKelas(model, docId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hapusOngoingKelas(final AnggotaKelasBlendedModel model, final String docId) {
        //get ongoingkelasblended

        CollectionReference colRef = db.collection(CommonMethod.refUser)
                .document(docId)
                .collection(CommonMethod.refOngoingKelasBlended);

        colRef.whereEqualTo("kelasId", model.getKelasId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentReference ref;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ref = db.collection(CommonMethod.refUser)
                                    .document(docId)
                                    .collection(CommonMethod.refOngoingKelasBlended)
                                    .document(documentSnapshot.getId());
                            Log.d(TAG, "hapusOngoingKelas: user docId : " + docId);
                            Log.d(TAG, "hapusOngoingKelas: ongoing docId : " + documentSnapshot.getId());

                            ref.delete();
                        }

                        hapusUserDariAnggota(model);
                    }
                });
    }

    private void hapusUserDariAnggota(AnggotaKelasBlendedModel model) {
        DocumentReference ref = db.collection(CommonMethod.refKelasBlended)
                .document(model.getKelasId())
                .collection(CommonMethod.refAnggota)
                .document(model.getDocumentId());

        ref.delete();
    }

    private void showHapusDialog(final AnggotaKelasBlendedModel model, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Ingin menghapus anggota?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //hapus pertanyaan
                if (!CommonMethod.isInternetAvailable(context)) {
                    return;
                }

                hapusAnggota(model);

                models.remove(position);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout container;
        TextView tvNama, tvTanggal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.cl_c_op_anggota_kelas);
            tvNama = itemView.findViewById(R.id.tv_c_op_anggota_kelas_nama);
            tvTanggal = itemView.findViewById(R.id.tv_c_op_anggota_kelas_tanggal);
        }
    }
}
