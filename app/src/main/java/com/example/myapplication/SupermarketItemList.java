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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SupermarketItemList extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener ,ItemListAdapter.OnItemClickListener{

    private BottomNavigationView bottom;
    private RecyclerView recyclerView;
    private ItemListAdapter adapter;
    private RatingBar rating;
    private String supermarketId;
    String supermarketName;


    private DatabaseReference servicesRef;
    private SearchView searchView;
    String Image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermarket_item_list);
        recyclerView = findViewById(R.id.SupermarketItemList_recycler);

        rating = findViewById(R.id.ratingBar);


        ImageView imageView = findViewById(R.id.image);

        Intent intent = getIntent();
         Image = intent.getStringExtra("supermarket_image");

        // Load the supermarket image into the ImageView using Glide
        Glide.with(this)
                .load(Image)
                .centerCrop()
                .into(imageView);

        searchView = findViewById(R.id.searchButton);

        // Get the details of the selected supermarket from the intent
        supermarketId = intent.getStringExtra("supermarket_id");
        supermarketName = intent.getStringExtra("supermarket_name");




        rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> updateRating(rating));


        // Construct the database reference for the services of the selected supermarket
        servicesRef = FirebaseDatabase.getInstance().getReference().child("SuperMarketItems");
        Query servicesQuery = servicesRef.orderByChild("Supermarket").equalTo(supermarketName);
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

        CollectionReference userRatingCollectionRef = FirebaseFirestore.getInstance().collection("User");
        DocumentReference userRatingDocRef = userRatingCollectionRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DocumentReference restaurantRatingDocRef = userRatingDocRef.collection("Rating").document(supermarketId);
        restaurantRatingDocRef.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Float ratingValue = documentSnapshot.getDouble("rating").floatValue();
                rating.setRating(ratingValue);
            } else {
                // If the restaurant doesn't exist in the user's ratings, set the rating to zero
                rating.setRating(0.0f);
            }
        });
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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference userRatingCollectionRef = firestore.collection("User");
        DocumentReference userRatingDocRef = userRatingCollectionRef.document(userId);
        DocumentReference restaurantRatingDocRef = userRatingDocRef.collection("Rating").document(supermarketId);

        // Create a new document for the restaurant with the user's rating
        HashMap<String, Object> ratingData = new HashMap<>();
        ratingData.put("name", supermarketName);
        ratingData.put("image", Image);
        ratingData.put("rating", rating);
        restaurantRatingDocRef.set(ratingData)
                .addOnSuccessListener(aVoid -> {
                    // Rating updated successfully
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to update the rating
                });
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
        switch(id){
            case R.id.home:
                Intent in = new Intent (this,MainActivity.class);
                startActivity(in);
                return true;
            case R.id.map:
                Intent in1 = new Intent (this,Map.class);
                startActivity(in1);
                return true;
            case R.id.profile:
                Intent in2 = new Intent (this,Profile.class);
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

        ServicesClass selectedItem = new ServicesClass(id,itemName, itemImage,price);


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

                    // Add any other necessary data as extras
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to add the item to RecentlyV collection
                    // Display an error message or take appropriate action
                });
    }

}
