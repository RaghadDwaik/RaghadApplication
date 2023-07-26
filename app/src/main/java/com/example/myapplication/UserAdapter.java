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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserAdapter extends FirestoreRecyclerAdapter<ServicesClass, UserAdapter.SalonViewHolder> {

    private OnItemClickListener listener;

    public UserAdapter(@NonNull FirestoreRecyclerOptions<ServicesClass> options) {
        super(options);
    }

    public void updateData(Query query) {
        FirestoreRecyclerOptions<ServicesClass> options =
                new FirestoreRecyclerOptions.Builder<ServicesClass>()
                        .setQuery(query, ServicesClass.class)
                        .build();

        updateOptions(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull SalonViewHolder holder, int position, @NonNull ServicesClass model) {
        holder.bind(model);

    }

    @NonNull
    @Override
    public SalonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new SalonViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class SalonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView serviceimage;
        private TextView name;
        private TextView price;
        // Add more TextViews or views as needed

        public SalonViewHolder(View itemView) {
            super(itemView);
            serviceimage = itemView.findViewById(R.id.productImage);
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);

            itemView.setOnClickListener(this);
        }

        public void bind(ServicesClass salon) {
            Glide.with(serviceimage.getContext())
                    .load(salon.getImage())
                    .into(serviceimage);
            name.setText(salon.getName());


            price.setText(String.valueOf(salon.getPrice()));
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
                listener.onItemClick(snapshot, position);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }
    public int getItemCount() {
        return getSnapshots().size();
    }
}
