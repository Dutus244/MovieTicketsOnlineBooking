package com.example.movieticketsonlinebooking.fragments;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.movieticketsonlinebooking.R;
import com.example.movieticketsonlinebooking.viewmodels.Review;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> listReview;

    public ReviewAdapter(List<Review> listReview) {
        this.listReview = listReview;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review model = listReview.get(position);

        holder.totalStarRating.setRating(Float.parseFloat(String.valueOf(model.getRating())));
        holder.tvDescReview.setText(model.getDetail());
        holder.tvTglRating.setText(formatter.format(model.getDate()));

        if (model.getUser_name() != "") {
            holder.tvNamaPasien.setText(model.getUser_name());
        }
        else {
            holder.tvNamaPasien.setText("Duy Nguyễn");
        }
    }

    @Override
    public int getItemCount() {
        return listReview.size();
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNamaPasien;
        public MaterialRatingBar totalStarRating;
        public TextView tvTglRating;
        public TextView tvDescReview;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            tvNamaPasien = itemView.findViewById(R.id.tv_nama_pasien);
            totalStarRating = itemView.findViewById(R.id.total_star_rating);
            tvTglRating = itemView.findViewById(R.id.tv_tgl_rating);
            tvDescReview = itemView.findViewById(R.id.tv_desc_review);
        }
    }

}