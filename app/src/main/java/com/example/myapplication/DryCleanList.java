package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class DryCleanList extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener ,ItemListAdapter.OnItemClickListener{

    private DatabaseReference ratedSupermarketsRef;

    private BottomNavigationView bottom;
    private boolean userLoggedIn;
    private AlertDialog editDialog;

    private RecyclerView recyclerView;
    private ItemListAdapter adapter;
    private DatabaseReference servicesRef;
    private SearchView searchView;
    private RatingBar rating;

    private String drycleanId;
    String drycleanName;
    String drycleanImageUrl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dry_clean_list);
        recyclerView = findViewById(R.id.DrycleanList_recycler);

        searchView = findViewById(R.id.searchButton);

        rating = findViewById(R.id.ratingBar);
        userLoggedIn = checkUserLoggedIn();


        ImageView restaurantImageView = findViewById(R.id.restaurantImageView);

        // Get the details of the selected restaurant from the intent
        Intent intent = getIntent();
        String restaurantImage = intent.getStringExtra("dryclean_image");

        // Load the restaurant image into the ImageView using Glide
        Glide.with(this)
                .load(restaurantImage)
                .centerCrop()
                .into(restaurantImageView);


        drycleanId = intent.getStringExtra("dryclean_id");
        drycleanName = intent.getStringExtra("dryclean_name");
        drycleanImageUrl = intent.getStringExtra("dryclean_image");
        float supermarketRating = getIntent().getFloatExtra("dryclean_rating", 0.0f);
        rating.setRating(supermarketRating);

        // Construct the database reference for the services of the selected Restaurant
        servicesRef = FirebaseDatabase.getInstance().getReference().child("DryCleanServices");
        Query query = servicesRef.orderByChild("DryClean").equalTo(drycleanName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<ServicesClass> options =
                new FirebaseRecyclerOptions.Builder<ServicesClass>()
                        .setQuery(query, ServicesClass.class)
                        .build();

        adapter = new ItemListAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        // Create a FirebaseRecyclerOptions object for the adapter

        bottom = findViewById(R.id.bottom);
        BottomNavigationView nav1 = findViewById(R.id.bottom);
        nav1.setItemIconTintList(null);


        bottom.setOnNavigationItemSelectedListener(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

                String ownerId = currentUser.getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference placeRef = db.collection("Places").document(drycleanId);
                placeRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String owner = document.getString("ownerId");
                            if (owner != null && owner.equals(ownerId)) {
                                Button deleteButton = findViewById(R.id.deleteButton);
                                deleteButton.setVisibility(View.VISIBLE);
                                deleteButton.setOnClickListener(v -> deletePlace(drycleanId));
                                Button editButton = findViewById(R.id.edit);

                                editButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showEditDialog();
                                    }
                                });

                            } else {

                                Button editButton = findViewById(R.id.edit);
                                editButton.setVisibility(View.GONE);

                                Button deleteButton = findViewById(R.id.deleteButton);
                                deleteButton.setVisibility(View.GONE);
                            }
                        }
                    }
                });


            //---------------------------------------------
            String userId = currentUser.getUid();
            CollectionReference userRatingCollectionRef = FirebaseFirestore.getInstance().collection("User");

            if (drycleanId != null) {
                DocumentReference userRatingDocRef = userRatingCollectionRef.document(userId);
                DocumentReference restaurantRatingDocRef = userRatingDocRef.collection("Rating").document(drycleanId);
                restaurantRatingDocRef.addSnapshotListener((documentSnapshot, e) -> {
                    try {
                        if (e != null) {
                            throw e; // Throw the exception if it's not null
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Float ratingValue = documentSnapshot.getDouble("rating").floatValue();
                            rating.setRating(ratingValue);
                        } else {
                            // If the restaurant doesn't exist in the user's ratings, set the rating to zero
                            rating.setRating(0.0f);
                        }
                    } catch (Exception exception) {
                        // Handle the exception
                        System.out.println("Error retrieving rating snapshot: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                });
            }
        } else {

            Button editButton = findViewById(R.id.edit);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog();
                }
            });


            Button deleteButton = findViewById(R.id.deleteButton);
            deleteButton.setVisibility(View.GONE);
        }




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do nothing when the search query is submitted
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Call searchFirebase method to filter the data based on the entered query
                searchFirebase(newText);
                return true;
            }
        });
        rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> updateRating(rating));

    }
    private boolean checkUserLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;

    }

    private void showEditDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_restaurant, null);
        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextImage = dialogView.findViewById(R.id.editTextImage);

        // Pre-fill the EditText fields with the existing data
        editTextName.setText(drycleanName);
        editTextImage.setText(drycleanImageUrl);

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
        DocumentReference restaurantRef = db.collection("Places").document(drycleanId);

        java.util.Map<String, Object> firestoreUpdates = new HashMap<>();
        firestoreUpdates.put("name", newName);
        firestoreUpdates.put("image", newImage);

        restaurantRef.update(firestoreUpdates)
                .addOnSuccessListener(aVoid -> {
                    // Update successful in Firestore
                    // You can show a success message or take appropriate action
                    // Update the UI with the new name and image if needed
                    drycleanName = newName;
                    drycleanImageUrl = newImage;

                    // Reload the image using Glide
                    ImageView restaurantImageView = findViewById(R.id.restaurantImageView);
                    Glide.with(this)
                            .load(drycleanImageUrl)
                            .centerCrop()
                            .into(restaurantImageView);

                    // After updating in Firestore, now update in Realtime Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference r = database.getReference("Supermarket").child(drycleanName);

                    java.util.Map<String, Object> realtimeUpdates = new HashMap<>();
                    realtimeUpdates.put("name", newName);
                    realtimeUpdates.put("image", newImage);

                    r.updateChildren(realtimeUpdates)
                            .addOnSuccessListener(aVoid1 -> {
                                // Update successful in Realtime Database
                                // You can show a success message or take appropriate action
                                // Update the UI with the new name and image if needed
                                drycleanName = newName;
                                drycleanImageUrl = newImage;

                                // Reload the image using Glide
                                Glide.with(this)
                                        .load(drycleanImageUrl)
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

    private void openProfileActivity() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    private void deletePlace(String placeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("هل أنت متأكد من أنك تريد حذف هذا المكان؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    // Delete from Firebase Realtime Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference placeRef = database.getReference("DryClean").child(drycleanName);

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


    private void openLoginFragment() {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }



    private void updateRating(float rating) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ratedSupermarketsRef = FirebaseDatabase.getInstance().getReference().child("RecommendedClean");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference restaurantRatingDocRef = firestore.collection("User")
                .document(userId)
                .collection("Rating")
                .document(drycleanId);

        // Create a new document for the supermarket with the user's rating
        HashMap<String, Object> ratingData = new HashMap<>();
        ratingData.put("name", drycleanName);
        ratingData.put("image", drycleanImageUrl);
        ratingData.put("rating", rating);
        restaurantRatingDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {

                } else {
                    // User has not rated the supermarket yet
                    restaurantRatingDocRef.set(ratingData)
                            .addOnSuccessListener(aVoid -> {
                                DatabaseReference userSupermarketRef = ratedSupermarketsRef.child(drycleanId).push();
                                //   System.out.println("iddddddddddddddddddddddd"+userSupermarketRef.toString());

                                userSupermarketRef.child("id").setValue(drycleanId);
                                userSupermarketRef.child("image").setValue(drycleanImageUrl);
                                userSupermarketRef.child("name").setValue(drycleanName);
                                userSupermarketRef.child("rating").setValue(rating);
                            })
                            .addOnFailureListener(e -> {
                                // Handle the failure to update the rating
                            });
                }
            } else {
                // Handle the failure to check the existing rating
            }
        });
    }


    private void searchFirebase(String query) {
        Query searchQuery = servicesRef.orderByChild("name")
                .startAt(query)
                .endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<ServicesClass> options =
                new FirebaseRecyclerOptions.Builder<ServicesClass>()
                        .setQuery(searchQuery, ServicesClass.class)
                        .build();

        adapter.updateOptions(options);

    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
                // Check if the user is logged in
                if (userLoggedIn) {
                    Intent in5 = new Intent(this, FavouriteList.class);
                    startActivity(in5);
                } else {
                    // User is not logged in, show a message or launch the login activity
                    showLoginPrompt();
                }
                return true;
            case R.id.Recently:
                if (userLoggedIn) {
                    Intent in4 = new Intent(this, RecentlyView.class);
                    startActivity(in4);

                } else {
                    showLoginPrompt();
                }

                return true;
            case R.id.profile:


                if (userLoggedIn) {
                    openProfileActivity();
                } else {
                    openLoginFragment();
                }
                return true;
        }
        return false;
    }

    private void showLoginPrompt() {
      }

    @Override
    public void onItemClick(DataSnapshot snapshot, int position) {
        if (userLoggedIn) {
            ServicesClass rest = snapshot.getValue(ServicesClass.class);

            String id = rest.getId();
            String itemName = rest.getName();
            String itemImage = rest.getImage();
            double price = rest.getPrice();

            ServicesClass selectedItem = new ServicesClass(id, itemName, itemImage, price);


            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference recentlyViewedRef = firestore.collection("User")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())// Replace "userId" with the actual user ID
                    .collection("RecentlyV");
            DocumentReference documentRef = recentlyViewedRef.document(itemName);

            documentRef.set(selectedItem)
                    .addOnSuccessListener(aVoid -> {
                        // Successfully added the item to RecentlyV collection
                        // Proceed with starting the ServiceDetails activity
                        Intent intent = new Intent(DryCleanList.this, ServiceDetails.class);
                        intent.putExtra("id", snapshot.getKey());
                        intent.putExtra("name", rest.getName());
                        intent.putExtra("price", rest.getPrice());
                        intent.putExtra("desc", rest.getDescription());
                        intent.putExtra("image", rest.getImage());
                        intent.putExtra("place", drycleanName);
                        // Add any other necessary data as extras
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to add the item to RecentlyV collection
                        // Display an error message or take appropriate action
                    });
        } else {
            // User is not logged in, show a message to log in
            Toast.makeText(this, "Please log in to perform this action.", Toast.LENGTH_LONG).show();
        }
    }
}