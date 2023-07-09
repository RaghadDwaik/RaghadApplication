package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RestaurantList extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ItemListAdapter.OnItemClickListener, UserAdapter.OnItemClickListener {

    private BottomNavigationView bottom;
    private RecyclerView recyclerView;
    private ItemListAdapter itemListAdapter;
    private DatabaseReference ratedSupermarketsRef;

    private UserAdapter userAdapter;

    private DatabaseReference servicesRef;
    private RatingBar rating;
    private SearchView searchView;

    private String restaurantId;
    private String restaurantName;
    private String restaurantImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        recyclerView = findViewById(R.id.RestaurantList_recycler);
        searchView = findViewById(R.id.searchButton);
        rating = findViewById(R.id.ratingBar);

        ImageView restaurantImageView = findViewById(R.id.restaurantImageView);

        // Get the details of the selected restaurant from the intent
        Intent intent = getIntent();
        String restaurantImage = intent.getStringExtra("restaurant_image");

        // Load the restaurant image into the ImageView using Glide
        Glide.with(this)
                .load(restaurantImage)
                .centerCrop()
                .into(restaurantImageView);

        // Get the details of the selected restaurant from the intent
        restaurantId = intent.getStringExtra("restaurant_id");
        restaurantName = intent.getStringExtra("restaurant_name");
        restaurantImageUrl = intent.getStringExtra("restaurant_image");
        float RestRating = getIntent().getFloatExtra("restaurant_rating", 0.0f);
        rating.setRating(RestRating);
        //      rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> updateRating(rating));

        servicesRef = FirebaseDatabase.getInstance().getReference().child("Items");
        Query query = servicesRef.orderByChild("Resturant").equalTo(restaurantName);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Setup Firebase options for ItemListAdapter
        FirebaseRecyclerOptions<ServicesClass> firebaseOptions = new FirebaseRecyclerOptions.Builder<ServicesClass>()
                .setQuery(query, ServicesClass.class)
                .build();

        itemListAdapter = new ItemListAdapter(firebaseOptions);
        itemListAdapter.setOnItemClickListener(this);

        recyclerView.setAdapter(itemListAdapter);

        bottom = findViewById(R.id.bottom);
        bottom.setItemIconTintList(null);
        bottom.setOnNavigationItemSelectedListener(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (currentUser != null) {
            String userId = currentUser.getUid();
            CollectionReference userRatingCollectionRef = FirebaseFirestore.getInstance().collection("User");

            if (restaurantId != null) {
                DocumentReference userRatingDocRef = userRatingCollectionRef.document(userId);
                DocumentReference restaurantRatingDocRef = userRatingDocRef.collection("Rating").document(restaurantId);
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
                searchFirebase(newText);
                return true;
            }
        });

        rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> updateRating(rating));
    }

    private void searchFirebase(String query) {
        Query searchQuery = servicesRef.orderByChild("name")
                .startAt(query)
                .endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<ServicesClass> options =
                new FirebaseRecyclerOptions.Builder<ServicesClass>()
                        .setQuery(searchQuery, ServicesClass.class)
                        .build();

        itemListAdapter.updateOptions(options);
    }

    private void updateRating(float rating) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ratedSupermarketsRef = FirebaseDatabase.getInstance().getReference().child("RecommendedRest");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference restaurantRatingDocRef = firestore.collection("User")
                .document(userId)
                .collection("Rating")
                .document(restaurantId);

        // Create a new document for the supermarket with the user's rating
        HashMap<String, Object> ratingData = new HashMap<>();
        ratingData.put("name", restaurantId);
        ratingData.put("image", restaurantImageUrl);
        ratingData.put("rating", rating);
        restaurantRatingDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {

                } else {
                    // User has not rated the supermarket yet
                    restaurantRatingDocRef.set(ratingData)
                            .addOnSuccessListener(aVoid -> {
                                DatabaseReference userSupermarketRef = ratedSupermarketsRef.child(restaurantId).push();
                                //   System.out.println("iddddddddddddddddddddddd"+userSupermarketRef.toString());

                                userSupermarketRef.child("id").setValue(restaurantId);
                                userSupermarketRef.child("image").setValue(restaurantImageUrl);
                                userSupermarketRef.child("name").setValue(restaurantName);
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

    @Override
    protected void onStart() {
        super.onStart();
        itemListAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        itemListAdapter.stopListening();
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
                    Intent intent = new Intent(RestaurantList.this, ServiceDetails.class);
                    intent.putExtra("id", snapshot.getKey());
                    intent.putExtra("name", rest.getName());
                    intent.putExtra("price", rest.getPrice());
                    intent.putExtra("desc", rest.getDescription());
                    intent.putExtra("image", rest.getImage());

                    // Add any other necessary data as extras
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to add the item to RecentlyV collection
                    // Display an error message or take appropriate action
                });
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {

    }
}
