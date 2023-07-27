package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.example.myapplication.DormsClass;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class DormsDetails extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ImageView dormImageView, image1, image2, image3,image4;
    private RatingBar ratingBar;
    private boolean isFavorite = false;
    private ImageButton favorite;
    private CollectionReference favoritesRef;

    private BottomNavigationView bottom;
    private AlertDialog editDialog;


    private SearchView searchView;
    String dormsId,dormsName,dormsImage,image, imagee, imageee,imageeee;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dormsdetails);
        bottom = findViewById(R.id.bottom);
        bottom.setItemIconTintList(null);
        bottom.setOnNavigationItemSelectedListener(this);

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

        Button editButton = findViewById(R.id.edit);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
}
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                Intent in = new Intent(this, MainActivity.class);
                startActivity(in);
                return true;
            case R.id.map:
                Intent in1 = new Intent(this, Map.class);
                startActivity(in1);
                return true;

            case R.id.favorite:
                Intent in5 = new Intent(this, FavouriteList.class);
                startActivity(in5);
                return true;

            case R.id.Recently:
                Intent in4 = new Intent(this, RecentlyView.class);
                startActivity(in4);
                return true;
            case R.id.profile:
                Intent in2 = new Intent(this, Profile.class);
                startActivity(in2);
                return true;
        }
        return false;
    }

    private void showEditDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_restaurant, null);
        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextImage = dialogView.findViewById(R.id.editTextImage);

        // Pre-fill the EditText fields with the existing data
        editTextName.setText(dormsName);
        editTextImage.setText(dormsImage);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("تعديل المكان")
                .setPositiveButton("حفظ", (dialog, which) -> {
                    String newName = editTextName.getText().toString().trim();
                    String newImage = editTextImage.getText().toString().trim();
                    updateRestaurantDetails(newName, newImage);
                })
                .setNegativeButton("تخطي", null);

        // Show the AlertDialog
        editDialog = builder.create();
        editDialog.show();
    }

    private void updateRestaurantDetails(String newName, String newImage) {
        // Update the restaurant details in the Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference restaurantRef = db.collection("Places").document(dormsId);

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("image", newImage);

        restaurantRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    // You can show a success message or take appropriate action
                    // Update the UI with the new name and image if needed
                    dormsName = newName;
                    dormsImage = newImage;

                    // Reload the image using Glide
                    ImageView restaurantImageView = findViewById(R.id.restaurantImageView);
                    Glide.with(this)
                            .load(dormsImage)
                            .centerCrop()
                            .into(restaurantImageView);
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to update the restaurant details
                    // You can show an error message or take appropriate action
                    Toast.makeText(this, "Failed to update restaurant details", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

}
