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

import com.bumptech.glide.Glide;
import com.example.aldy.difacademy.Activity.OpAddBlendedCourseActivity;
import com.example.aldy.difacademy.Activity.OpAddOnlineCourseActivity;
import com.example.aldy.difacademy.Model.CourseModel;
import com.example.aldy.difacademy.R;

import java.util.ArrayList;

import static com.example.aldy.difacademy.Activity.OpMainActivity.OP_JENIS_KELAS;

public class OpCourseAdapter extends RecyclerView.Adapter<OpCourseAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CourseModel> courseModels;

    public OpCourseAdapter(Context context, ArrayList<CourseModel> courseModels) {
        this.context = context;
        this.courseModels = courseModels;
    }

    @NonNull
    @Override
    public OpCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_op, parent, false);
        return new OpCourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpCourseAdapter.ViewHolder holder, final int position) {
        final CourseModel model = courseModels.get(position);
        holder.tvJudul.setText(model.getTitle());
        holder.tvDeskripsi.setText(model.getDescription());
        holder.tvTag.setText(model.getTag());
        Glide.with(context)
                .load(model.getThumbnailUrl())
                .into(holder.imgThumbnail);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (OP_JENIS_KELAS.equals("blended")){
                    intent = new Intent(context, OpAddBlendedCourseActivity.class);
                    intent.putExtra("blended_course_model", model);
                    intent.putExtra("index", position);
                    context.startActivity(intent);
                } else if (OP_JENIS_KELAS.equals("online")){
                    intent = new Intent(context, OpAddOnlineCourseActivity.class);
                    intent.putExtra("online_course_model", model);
                    intent.putExtra("index", position);
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return courseModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvJudul, tvTag, tvDeskripsi;
        private ImageView imgThumbnail;
        private ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tv_c_op_judul);
            tvTag = itemView.findViewById(R.id.tv_c_op_tag);
            tvDeskripsi = itemView.findViewById(R.id.tv_c_op_deskripsi);
            imgThumbnail = itemView.findViewById(R.id.img_c_op_thumbnail);
            container = itemView.findViewById(R.id.cl_c_op);
        }
    }
}
