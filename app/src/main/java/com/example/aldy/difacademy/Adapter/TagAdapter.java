package com.example.aldy.difacademy.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Model.TagModel;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private static final String TAG = "TagAdapter";

    private Context context;
    private ArrayList<TagModel> tagModels;

    public TagAdapter(Context context, ArrayList<TagModel> tagModels) {
        this.context = context;
        this.tagModels = tagModels;
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
        holder.clDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hapus tagnya
                tagModels.remove(position);
                notifyDataSetChanged();
                hapusTag(tagModel.getTagid());
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

    private void hapusTag(String tagId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference tagRef = db.collection("Tags").document(tagId);
        tagRef.delete();
        Log.d(TAG, "hapusTag: tag terhapus");
    }
}
