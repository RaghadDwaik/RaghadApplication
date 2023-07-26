package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class PlaceOwnerAdapter extends FirestoreRecyclerAdapter<PlacesClass, PlaceOwnerAdapter.SalonViewHolder> {

    private OnItemClickListener listener;

    public PlaceOwnerAdapter(@NonNull FirestoreRecyclerOptions<PlacesClass> options) {
        super(options);
    }

    @Override

    protected void onBindViewHolder(@NonNull SalonViewHolder holder, int position, @NonNull PlacesClass model) {
        Glide.with(holder.mImageView.getContext())
                .load(model.getImage()) // Load the image URL from the place object
                .into(holder.mImageView);
        holder.mNameTextView.setText(model.getName());
    }


    @NonNull


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



    @NonNull
    @Override
    public SalonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homecard_view, parent, false);
        return new SalonViewHolder(view);
    }

    public class SalonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;
        private TextView mNameTextView;

        public SalonViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image);
            mNameTextView = itemView.findViewById(R.id.txtName);

            itemView.setOnClickListener(this);
        }

        public void bind(PlacesClass salon) {
            Glide.with(mImageView.getContext())
                    .load(salon.getImage())
                    .into(mImageView);
            mNameTextView.setText(salon.getName());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(getSnapshots().getSnapshot(position), position);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }
}
