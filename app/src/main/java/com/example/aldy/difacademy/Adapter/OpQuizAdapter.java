package com.example.aldy.difacademy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aldy.difacademy.Activity.OpAddQuizActivity;
import com.example.aldy.difacademy.Model.QuizModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

public class OpQuizAdapter extends RecyclerView.Adapter<OpQuizAdapter.ViewHolder> {
    private static final String TAG = "OpQuizAdapter";

    private Context context;
    private ArrayList<QuizModel> quizModels;

    public OpQuizAdapter(Context context, ArrayList<QuizModel> quizModels) {
        this.context = context;
        this.quizModels = quizModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpQuizAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final QuizModel quizModel = quizModels.get(position);
        holder.tvDeskripsi.setVisibility(View.GONE);
        holder.imgThumbnail.setVisibility(View.GONE);
        holder.tvTag.setVisibility(View.GONE);
        holder.tvJudul.setText(position + 1 + ". " + quizModel.getSoal());
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OpAddQuizActivity.class);
                intent.putExtra("quiz_model", quizModel);
                intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;
        private ConstraintLayout clContainer;
        private TextView tvJudul, tvTag, tvDeskripsi;

        public ViewHolder(@NonNull View v) {
            super(v);
            imgThumbnail = v.findViewById(R.id.img_c_op_thumbnail);
            tvJudul = v.findViewById(R.id.tv_c_op_judul);
            tvTag = v.findViewById(R.id.tv_c_op_tag);
            tvDeskripsi = v.findViewById(R.id.tv_c_op_deskripsi);
            clContainer = v.findViewById(R.id.cl_c_op);
        }
    }
}
