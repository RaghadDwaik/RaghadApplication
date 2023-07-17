package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.example.myapplication.DormsClass;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DormsDetails extends AppCompatActivity {

    private ImageView dormImageView, image1, image2, image3,image4;
    private RatingBar ratingBar;
    private boolean isFavorite = false;
    private ImageButton favorite;
    private CollectionReference favoritesRef;

    private SearchView searchView;
    String dormsId,dormsName,dormsImage,image, imagee, imageee,imageeee;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dormsdetails);


        // Initialize views
        dormImageView = findViewById(R.id.restaurantImageView);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);

        ratingBar = findViewById(R.id.ratingBar);
        searchView = findViewById(R.id.searchButton);
        descriptionTextView = findViewById(R.id.description);



        Intent intent = getIntent();
        if (intent != null) {
            dormsId = intent.getStringExtra("Dorms_id");
             dormsName = intent.getStringExtra("Dorms_name");
             dormsImage = intent.getStringExtra("Dorms_image");

            // Use Glide to load the dormitory image
            Glide.with(this)
                    .load(dormsImage)
                    .into(dormImageView);


            DatabaseReference dormDetailsRef = FirebaseDatabase.getInstance().getReference().child("DormsDetails").child(dormsName);
            dormDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DormsClass dormDetails = dataSnapshot.getValue(DormsClass.class);

                    if (dormDetails != null) {
                        ratingBar.setRating(dormDetails.getRating());
                        Glide.with(DormsDetails.this).load(dormDetails.getImage1()).into(image1);
                        Glide.with(DormsDetails.this).load(dormDetails.getImage2()).into(image2);
                        Glide.with(DormsDetails.this).load(dormDetails.getImage3()).into(image3);
                        Glide.with(DormsDetails.this).load(dormDetails.getImage4()).into(image4);

                        descriptionTextView.setText(dormDetails.getDescription());

                        // After setting the description, scroll to the bottom
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database read error if needed
                }
            });
        }

        // Implement the search functionality (assuming it's used to search for other dormitories)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform the search operation here
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform filtering of dormitories based on newText
                return false;
            }
        });


}}
