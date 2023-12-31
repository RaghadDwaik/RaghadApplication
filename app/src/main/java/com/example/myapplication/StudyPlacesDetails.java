package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class StudyPlacesDetails extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ImageView studyImageView, image1, image2, image3,image4;

    boolean visited;
    Button editButton;
    Button deleteButton;
    private RatingBar ratingBar;
    private boolean isFavorite = false;
    private ImageButton favorite;
    private CollectionReference favoritesRef;

    private BottomNavigationView bottom;
    private AlertDialog editDialog;

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

        editButton = findViewById(R.id.edit);
        deleteButton = findViewById(R.id.deleteButton);

        Intent intent = getIntent();
        if (intent != null) {
            visited = getIntent().getBooleanExtra("visited", false);

            studyId = intent.getStringExtra("studyplace_id");
            studyName = intent.getStringExtra("studyplace_name");
            studyImage = intent.getStringExtra("studyplace_image");

            float supermarketRating = getIntent().getFloatExtra("studyplace_rate", 0.0f);
            ratingBar.setRating(supermarketRating);




            Glide.with(this)

                    .load(studyImage)
                    .into(studyImageView);




            DatabaseReference studyDetailsRef = FirebaseDatabase.getInstance().getReference().child("StudyPlaceItems").child(studyName);
            studyDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DormsClass studyDetails = dataSnapshot.getValue(DormsClass.class);

                    if (studyDetails != null) {
                    //    ratingBar.setRating(studyDetails.getRating());
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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {




            //---------------------------------------------
            String userId = currentUser.getUid();
            CollectionReference userRatingCollectionRef = FirebaseFirestore.getInstance().collection("User");

            if (studyId != null) {
                DocumentReference userRatingDocRef = userRatingCollectionRef.document(userId);
                DocumentReference restaurantRatingDocRef = userRatingDocRef.collection("Rating").document(studyId);
                restaurantRatingDocRef.addSnapshotListener((documentSnapshot, e) -> {
                    try {
                        if (e != null) {
                            throw e; // Throw the exception if it's not null
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Float ratingValue = documentSnapshot.getDouble("rating").floatValue();
                            ratingBar.setRating(ratingValue);
                        } else {
                            // If the restaurant doesn't exist in the user's ratings, set the rating to zero
                            ratingBar.setRating(0.0f);
                        }
                    } catch (Exception exception) {
                        // Handle the exception
                        System.out.println("Error retrieving rating snapshot: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                });
            }
        } else {
            // Handle the case when the currentUser is null
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

        buttonVisibility();

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> updateRating(rating));

    }


    private void showEditDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit, null);
        EditText editTextName = dialogView.findViewById(R.id.Name);
        EditText editTextImage = dialogView.findViewById(R.id.Image);

        // Pre-fill the EditText fields with the existing data
        editTextName.setText(studyName);
        editTextImage.setText(studyImage);

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
        DocumentReference restaurantRef = db.collection("Places").document(studyId);

        java.util.Map<String, Object> firestoreUpdates = new HashMap<>();
        firestoreUpdates.put("name", newName);
        firestoreUpdates.put("image", newImage);

        restaurantRef.update(firestoreUpdates)
                .addOnSuccessListener(aVoid -> {
                    // Update successful in Firestore
                    // You can show a success message or take appropriate action
                    // Update the UI with the new name and image if needed
                    studyName = newName;
                    studyImage = newImage;

                    // Reload the image using Glide
                    ImageView restaurantImageView = findViewById(R.id.restaurantImageView);
                    Glide.with(this)
                            .load(studyImage)
                            .centerCrop()
                            .into(restaurantImageView);

                    // After updating in Firestore, now update in Realtime Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference r = database.getReference("studyplace").child(studyName);

                    java.util.Map<String, Object> realtimeUpdates = new HashMap<>();
                    realtimeUpdates.put("name", newName);
                    realtimeUpdates.put("image", newImage);

                    r.updateChildren(realtimeUpdates)
                            .addOnSuccessListener(aVoid1 -> {
                                // Update successful in Realtime Database
                                // You can show a success message or take appropriate action
                                // Update the UI with the new name and image if needed
                                studyName = newName;
                                studyImage = newImage;

                                // Reload the image using Glide
                                Glide.with(this)
                                        .load(studyImage)
                                        .centerCrop()
                                        .into(restaurantImageView);
                            })
                            .addOnFailureListener(e -> {
                                // Handle the failure to update in Realtime Database
                                Toast.makeText(this, "Failed to update restaurant details in Realtime Database", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to update in Firestore
                    Toast.makeText(this, "Failed to update restaurant details in Firestore", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void updateRating(float rating) {
        if (studyId != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            DocumentReference restaurantRatingDocRef = firestore.collection("User")
                    .document(userId)
                    .collection("Rating")
                    .document(studyId);

            // Create a new HashMap to store the updated rating data
            HashMap<String, Object> ratingData = new HashMap<>();
            ratingData.put("name", studyName);
            ratingData.put("image", studyImage);
            ratingData.put("rating", rating);

            // Check if the rating document already exists for this user and study place
            restaurantRatingDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If the rating document exists, update the rating value
                        restaurantRatingDocRef.update("rating", rating)
                                .addOnSuccessListener(aVoid -> {
                                    // Update successful, you can show a success message if needed
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to update the rating
                                    Toast.makeText(StudyPlacesDetails.this, "Failed to update rating.", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // If the rating document doesn't exist, set the new rating data
                        restaurantRatingDocRef.set(ratingData)
                                .addOnSuccessListener(aVoid -> {
                                    // Set successful, you can show a success message if needed
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to set the new rating data
                                    Toast.makeText(StudyPlacesDetails.this, "Failed to set rating data.", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    // Handle the case when fetching the rating document fails
                    Toast.makeText(StudyPlacesDetails.this, "Failed to fetch rating document.", Toast.LENGTH_SHORT).show();
                }
            });

            // Also, update the rating value in the Realtime Database
            DatabaseReference ratedSupermarketsRef = FirebaseDatabase.getInstance().getReference().child("RecommendedStudy");
            DatabaseReference userSupermarketRef = ratedSupermarketsRef.child(studyId).child(userId);
            userSupermarketRef.child("id").setValue(studyId);
            userSupermarketRef.child("image").setValue(studyImage);
            userSupermarketRef.child("name").setValue(studyName);
            userSupermarketRef.child("rating").setValue(rating)
                    .addOnSuccessListener(aVoid -> {
                        // Rating update in Realtime Database successful, if needed, you can show a success message
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to update the rating in Realtime Database
                        Toast.makeText(StudyPlacesDetails.this, "Failed to update rating in Realtime Database.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(StudyPlacesDetails.this, "Invalid study place ID.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePlace(String placeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("هل أنت متأكد من أنك تريد حذف هذا المكان؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    // Delete from Firebase Realtime Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference placeRef = database.getReference("studyplace").child(studyName);

                    placeRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                // Deletion from Realtime Database successful
                                // You can add any specific actions you want after deletion
                            })
                            .addOnFailureListener(e -> {
                                // Handle the failure to delete from Realtime Database
                                e.printStackTrace();
                            });

                    // Delete from Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Places").document(placeId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                // Deletion from Firestore successful
                                Toast.makeText(this, "تم حذف المكان بنجاح", Toast.LENGTH_SHORT).show();
                                finish(); // Finish the activity after deletion
                            })
                            .addOnFailureListener(e -> {
                                // Handle the failure to delete from Firestore
                                Toast.makeText(this, "فشل في حذف المكان", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            });
                })
                .setNegativeButton("لا", (dialog, which) -> {
                    // User clicked "لا", do nothing
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    private void buttonVisibility() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (visited) {
                deleteButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Call the edit method when the editButton is clicked
                        showEditDialog();
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Call the delete method when the deleteButton is clicked
                        deletePlace(studyId);
                    }
                });
            }
            else {
                deleteButton.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
            }

        }
    }

}
