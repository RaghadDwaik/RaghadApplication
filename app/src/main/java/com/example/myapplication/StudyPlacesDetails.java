package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class StudyPlacesDetails extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ImageView studyImageView, image1, image2, image3,image4;
    private RatingBar ratingBar;
    private boolean isFavorite = false;
    private ImageButton favorite;
    private CollectionReference favoritesRef;

    private BottomNavigationView bottom;

    private SearchView searchView;
    String studyId,studyName,studyImage,image, imagee, imageee,imageeee;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_places_details);
        bottom = findViewById(R.id.bottom);
        bottom.setItemIconTintList(null);
        bottom.setOnNavigationItemSelectedListener(this);

        // Initialize views
        studyImageView = findViewById(R.id.restaurantImageView);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);

        ratingBar = findViewById(R.id.ratingBar);
        searchView = findViewById(R.id.searchButton);
        descriptionTextView = findViewById(R.id.description);



        Intent intent = getIntent();
        if (intent != null) {
            studyId = intent.getStringExtra("studyplace_id");
            studyName = intent.getStringExtra("studyplace_name");
            studyImage = intent.getStringExtra("studyplace_image");

            // Use Glide to load the dormitory image
            Glide.with(this)
                    .load(studyImage)
                    .into(studyImageView);


            DatabaseReference studyDetailsRef = FirebaseDatabase.getInstance().getReference().child("StudyPlaceItems").child(studyName);
            studyDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DormsClass studyDetails = dataSnapshot.getValue(DormsClass.class);

                    if (studyDetails != null) {
                        ratingBar.setRating(studyDetails.getRating());
                        Glide.with(StudyPlacesDetails.this).load(studyDetails.getImage1()).into(image1);
                        Glide.with(StudyPlacesDetails.this).load(studyDetails.getImage2()).into(image2);
                        Glide.with(StudyPlacesDetails.this).load(studyDetails.getImage3()).into(image3);
                        Glide.with(StudyPlacesDetails.this).load(studyDetails.getImage4()).into(image4);

                        descriptionTextView.setText(studyDetails.getDescription());

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

}
