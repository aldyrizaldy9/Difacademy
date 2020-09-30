package com.tamanpelajar.aldy.difacademy.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.tamanpelajar.aldy.difacademy.Model.TagModel;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpTagAdapter extends RecyclerView.Adapter<OpTagAdapter.ViewHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;
    private ArrayList<TagModel> models;
    private ProgressDialog pd;

    public OpTagAdapter(Context context, ArrayList<TagModel> models) {
        this.context = context;
        this.models = models;
        pd = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public OpTagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpTagAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OpTagAdapter.ViewHolder holder, final int position) {
        final TagModel model = models.get(position);
        holder.tvDeskripsi.setVisibility(View.GONE);
        holder.tvJudul.setVisibility(View.GONE);
        holder.imgThumbnail.setVisibility(View.GONE);
        holder.clEdit.setVisibility(View.GONE);
        holder.clDelete.setVisibility(View.VISIBLE);
        holder.clDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHapusDialog(position, model.getTagid());
            }
        });
        holder.tvTag.setText(model.getTag());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    private void hapusTag(final String tagId) {
        pd.setMessage("Menghapus");
        pd.setCancelable(false);
        pd.show();
        DocumentReference tagRef = db.collection(CommonMethod.refTags).document(tagId);

        tagRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hapusTagDiKelasBlended(tagId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hapusTagDiKelasBlended(final String tagId) {
        final CollectionReference kelasBlendedRef = db.collection(CommonMethod.refKelasBlended);

        kelasBlendedRef.whereEqualTo("tagId", tagId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = kelasBlendedRef.document(documentSnapshot.getId());
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("tagId", "");
                            updates.put("tag", "");
                            docRef.update(updates);
                        }
                        hapusTagDiKelasOnline(tagId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hapusTagDiKelasOnline(String tagId) {
        final CollectionReference kelasOnlineRef = db.collection(CommonMethod.refKelasOnline);

        kelasOnlineRef.whereEqualTo("tagId", tagId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference docRef = kelasOnlineRef.document(documentSnapshot.getId());
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("tagId", "");
                            updates.put("tag", "");
                            docRef.update(updates);
                        }
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showHapusDialog(final int position, final String tagId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Apakah anda yakin ingin menghapus tag ini?");
        builder.setTitle("Hapus Tag");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //hapus tagnya
                models.remove(position);
                notifyDataSetChanged();

                hapusTag(tagId);
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;
        private ConstraintLayout clDelete, clEdit;
        private TextView tvJudul, tvTag, tvDeskripsi;

        public ViewHolder(@NonNull View v) {
            super(v);
            imgThumbnail = v.findViewById(R.id.img_c_op_thumbnail);
            clDelete = v.findViewById(R.id.cl_c_op_delete);
            clEdit = v.findViewById(R.id.cl_c_op_edit);
            tvJudul = v.findViewById(R.id.tv_c_op_judul);
            tvTag = v.findViewById(R.id.tv_c_op_tag);
            tvDeskripsi = v.findViewById(R.id.tv_c_op_deskripsi);
        }
    }
}
