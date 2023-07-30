package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import java.util.Map;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SupermarketItemList extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ItemListAdapter.OnItemClickListener {

    private BottomNavigationView bottom;
    private RecyclerView recyclerView;
    private DatabaseReference ratedSupermarketsRef;

    private ItemListAdapter adapter;
    private RatingBar rating;
    private String supermarketId;
    private String supermarketName;

    private AlertDialog editDialog;

    private DatabaseReference servicesRef;
    private SearchView searchView;
    private String Image ;
    boolean visited;
    Button editButton;
    Button deleteButton;
    float rate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermarket_item_list);

        recyclerView = findViewById(R.id.SupermarketItemList_recycler);
        rating = findViewById(R.id.ratingBar);
        ImageView imageView = findViewById(R.id.restaurantImageView);
        searchView = findViewById(R.id.searchButton);

        editButton = findViewById(R.id.edit);
        deleteButton = findViewById(R.id.deleteButton);

        Intent intent = getIntent();
        Image = intent.getStringExtra("supermarket_image");

        // Load the supermarket image into the ImageView using Glide
        Glide.with(this)
                .load(Image)
                .centerCrop()
                .into(imageView);

        // Get the details of the selected supermarket from the intent
        supermarketId = intent.getStringExtra("supermarket_id");
        supermarketName = intent.getStringExtra("supermarket_name");
        visited = getIntent().getBooleanExtra("visited", false);


        float supermarketRating = getIntent().getFloatExtra("supermarket_rating", 0.0f);
        rating.setRating(supermarketRating);
        // rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> updateRating(rating));

        // Construct the database reference for the services of the selected supermarket
        servicesRef = FirebaseDatabase.getInstance().getReference().child("SuperMarketItems");
        Query servicesQuery = servicesRef.orderByChild("supermarket").equalTo(supermarketName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<ServicesClass> options =
                new FirebaseRecyclerOptions.Builder<ServicesClass>()
                        .setQuery(servicesQuery, ServicesClass.class)
                        .build();

        adapter = new ItemListAdapter(options);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        bottom = findViewById(R.id.bottom);
        bottom.setItemIconTintList(null);
        bottom.setOnNavigationItemSelectedListener(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {


            String userId = currentUser.getUid();
            CollectionReference userRatingCollectionRef = FirebaseFirestore.getInstance().collection("User");

            if (supermarketId != null) {
                DocumentReference userRatingDocRef = userRatingCollectionRef.document(userId);
                DocumentReference restaurantRatingDocRef = userRatingDocRef.collection("Rating").document(supermarketId);
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
            // Handle the case when the currentUser is null
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


        buttonVisibility();


        rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> updateRating(rating));
    }

    private void showEditDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit, null);
        EditText editTextName = dialogView.findViewById(R.id.Name);
        EditText editTextImage = dialogView.findViewById(R.id.Image);

        editTextName.setText(supermarketName);
        editTextImage.setText(Image);

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference restaurantRef = db.collection("Places").document(supermarketId);

        Map<String, Object> firestoreUpdates = new HashMap<>();
        firestoreUpdates.put("name", newName);
        firestoreUpdates.put("image", newImage);

        restaurantRef.update(firestoreUpdates)
                .addOnSuccessListener(aVoid -> {
                    // Update successful in Firestore
                    // You can show a success message or take appropriate action
                    // Update the UI with the new name and image if needed
                    supermarketName = newName;
                    Image = newImage;

                    // Reload the image using Glide
                    ImageView restaurantImageView = findViewById(R.id.restaurantImageView);
                    Glide.with(this)
                            .load(Image)
                            .centerCrop()
                            .into(restaurantImageView);

                    // After updating in Firestore, now update in Realtime Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference r = database.getReference("supermarket").child(supermarketName);

                    Map<String, Object> realtimeUpdates = new HashMap<>();
                    realtimeUpdates.put("name", newName);
                    realtimeUpdates.put("image", newImage);

                    r.updateChildren(realtimeUpdates)
                            .addOnSuccessListener(aVoid1 -> {
                                // Update successful in Realtime Database
                                // You can show a success message or take appropriate action
                                // Update the UI with the new name and image if needed
                                supermarketName = newName;
                                Image = newImage;

                                // Reload the image using Glide
                                Glide.with(this)
                                        .load(Image)
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

    private void deletePlace(String placeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("هل أنت متأكد من أنك تريد حذف هذا المكان؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    // Delete from Firebase Realtime Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference placeRef = database.getReference("supermarket").child(supermarketName);

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

    private void updateRating(float rating) {
        if (supermarketId != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            DocumentReference restaurantRatingDocRef = firestore.collection("User")
                    .document(userId)
                    .collection("Rating")
                    .document(supermarketId);

            // Create a new HashMap to store the updated rating data
            HashMap<String, Object> ratingData = new HashMap<>();
            ratingData.put("name", supermarketName);
            ratingData.put("image", Image);
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
                                    Toast.makeText(SupermarketItemList.this, "Failed to update rating.", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // If the rating document doesn't exist, set the new rating data
                        restaurantRatingDocRef.set(ratingData)
                                .addOnSuccessListener(aVoid -> {
                                    // Set successful, you can show a success message if needed
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to set the new rating data
                                    Toast.makeText(SupermarketItemList.this, "Failed to set rating data.", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    // Handle the case when fetching the rating document fails
                    Toast.makeText(SupermarketItemList.this, "Failed to fetch rating document.", Toast.LENGTH_SHORT).show();
                }
            });

            // Also, update the rating value in the Realtime Database
            DatabaseReference ratedSupermarketsRef = FirebaseDatabase.getInstance().getReference().child("RecommendedClean");
            DatabaseReference userSupermarketRef = ratedSupermarketsRef.child(supermarketId).child(userId);
            userSupermarketRef.child("id").setValue(supermarketId);
            userSupermarketRef.child("image").setValue(Image);
            userSupermarketRef.child("name").setValue(supermarketName);
            userSupermarketRef.child("rating").setValue(rating)
                    .addOnSuccessListener(aVoid -> {
                        // Rating update in Realtime Database successful, if needed, you can show a success message
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to update the rating in Realtime Database
                        Toast.makeText(SupermarketItemList.this, "Failed to update rating in Realtime Database.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(SupermarketItemList.this, "Invalid study place ID.", Toast.LENGTH_SHORT).show();
        }

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

    @Override
    public void onItemClick(DataSnapshot snapshot, int position) {
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
                    Intent intent = new Intent(SupermarketItemList.this, ServiceDetails.class);
                    intent.putExtra("id", snapshot.getKey());
                    intent.putExtra("name", rest.getName());
                    intent.putExtra("price", rest.getPrice());
                    intent.putExtra("desc", rest.getDescription());
                    intent.putExtra("image", rest.getImage());
                    intent.putExtra("place", supermarketName);

                    // Add any other necessary data as extras
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to add the item to RecentlyV collection
                    // Display an error message or take appropriate action
                });
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
                        deletePlace(supermarketId);
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