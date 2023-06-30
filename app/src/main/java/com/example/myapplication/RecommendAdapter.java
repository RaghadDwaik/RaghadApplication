package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.SalonViewHolder> {

    private List<PlacesClass> recommendList;
    private OnItemClickListener listener;

    public RecommendAdapter(List<PlacesClass> recommendList) {
        this.recommendList = recommendList;
    }

    @NonNull
    @Override
    public SalonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendcard, parent, false);
        return new SalonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalonViewHolder holder, int position) {
        PlacesClass item = recommendList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    PlacesClass clickedItem = recommendList.get(clickedPosition);
                    if (listener != null) {
                        listener.onItemClick(clickedItem, clickedPosition);
                    }
                }
            }
        });

        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return recommendList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class SalonViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mNameTextView;

        public SalonViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image);
            mNameTextView = itemView.findViewById(R.id.txtName);
        }

        public void bind(PlacesClass salon) {
            Glide.with(mImageView.getContext())
                    .load(salon.getImage())
                    .into(mImageView);
            mNameTextView.setText(salon.getName());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PlacesClass item, int position);
    }
}
