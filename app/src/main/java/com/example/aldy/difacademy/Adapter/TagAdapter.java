package com.example.aldy.difacademy.Adapter;

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

import com.example.aldy.difacademy.Model.TagModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private static final String TAG = "TagAdapter";

    private Context context;
    private ArrayList<TagModel> tagModels;
    private ProgressDialog progressDialog;

    public TagAdapter(Context context, ArrayList<TagModel> tagModels) {
        this.context = context;
        this.tagModels = tagModels;
        progressDialog = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public TagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new TagAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.ViewHolder holder, final int position) {
        final TagModel tagModel = tagModels.get(position);
        holder.tvDeskripsi.setVisibility(View.GONE);
        holder.tvJudul.setVisibility(View.GONE);
        holder.imgThumbnail.setVisibility(View.GONE);
        holder.clEdit.setVisibility(View.GONE);
        holder.clDelete.setVisibility(View.VISIBLE);
        holder.clDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHapusDialog(position, tagModel.getTagid());
            }
        });
        holder.tvTag.setText(tagModel.getTag());
    }

    @Override
    public int getItemCount() {
        return tagModels.size();
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

    private void hapusTag(final String tagId) {
        progressDialog.show();
        progressDialog.setMessage("Menghapus");
        progressDialog.setCancelable(false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference tagRef = db.collection("Tags").document(tagId);

        final WriteBatch batch = db.batch();
        final CollectionReference videoFreeRef = db.collection("VideoFree");

        tagRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        videoFreeRef.whereEqualTo("tagId", tagId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            DocumentReference docRef = videoFreeRef.document(documentSnapshot.getId());
                                            batch.update(docRef, "tagId", "");
                                            batch.update(docRef, "tag", "");
                                        }
                                        batch.commit()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progressDialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
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
                tagModels.remove(position);
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
}
