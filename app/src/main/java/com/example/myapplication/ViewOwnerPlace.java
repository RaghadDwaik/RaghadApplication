package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.PlacesAdapter;
import com.example.myapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewOwnerPlace extends AppCompatActivity {

    private static final String TAG = "ViewOwnerPlace";
    private RecyclerView placesRecyclerView;
    private List<String> placeNamesList;
    private PlaceOwnerAdapter placesAdapter;
    private FirebaseFirestore db;
    private String ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_owner_place);

        db = FirebaseFirestore.getInstance();

        ownerId = getIntent().getStringExtra("ownerId");
        boolean visited = getIntent().getBooleanExtra("visited", false);


        placesRecyclerView = findViewById(R.id.placesRecyclerView);

        Query query = db.collection("Places").whereEqualTo("ownerId", ownerId);

        FirestoreRecyclerOptions<PlacesClass> options = new FirestoreRecyclerOptions.Builder<PlacesClass>()
                .setQuery(query, PlacesClass.class)
                .build();

        // Initialize the PlacesAdapter with the FirestoreRecyclerOptions
        placesAdapter = new PlaceOwnerAdapter(options);

        placesRecyclerView.setLayoutManager(new GridLayoutManager(this,2));

        placesRecyclerView.setAdapter(placesAdapter);

        placesAdapter.setOnItemClickListener(new PlaceOwnerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                PlacesClass place = snapshot.toObject(PlacesClass.class);
                String placeType = place.getPlaceType();
                System.out.println("plaaaaaaaaaaaaaaaaceeee " + placeType);

                // Open different activities based on the place type
                if (placeType.equals("salon")) {
                    Intent intent = new Intent(ViewOwnerPlace.this, SalonList.class);
                    intent.putExtra("salon_id", snapshot.getId());
                    intent.putExtra("salon_name", place.getName());
                    intent.putExtra("visited", true);

                    intent.putExtra("salon_image", place.getImage());
                    startActivity(intent);
                } else if (placeType.equals("supermarket")) {
                    Intent intent = new Intent(ViewOwnerPlace.this, SupermarketItemList.class);

                    intent.putExtra("supermarket_id", snapshot.getId());
                    intent.putExtra("supermarket_name", place.getName());
                    intent.putExtra("supermarket_image", place.getImage());
                    startActivity(intent);

                } else if (placeType.equals("resturant")) {
                    Intent intent = new Intent(ViewOwnerPlace.this, RestaurantList.class);
                    intent.putExtra("restaurant_id", snapshot.getId());
                    intent.putExtra("restaurant_name", place.getName());
                    intent.putExtra("restaurant_image", place.getImage());
                    startActivity(intent);
                } else if (placeType.equals("dryclean")) {
                    Intent intent = new Intent(ViewOwnerPlace.this, DryCleanList.class);
                    intent.putExtra("dryclean_id", snapshot.getId());
                    intent.putExtra("dryclean_name", place.getName());
                    intent.putExtra("dryclean_image", place.getImage());
                    startActivity(intent);

                } else if (placeType.equals("dorms")) {
                    Intent intent = new Intent(ViewOwnerPlace.this, DormsDetails.class);
                    intent.putExtra("Dorms_id", snapshot.getId());
                    intent.putExtra("Dorms_name", place.getName());
                    intent.putExtra("Dorms_image", place.getImage());
                    startActivity(intent);
                } else if (placeType.equals("studyplace")) {
                    Intent intent = new Intent(ViewOwnerPlace.this, StudyPlacesDetails.class);
                    intent.putExtra("studyplace_id", snapshot.getId());
                    intent.putExtra("studyplace_name", place.getName());
                    intent.putExtra("studyplace_image", place.getImage());
                    startActivity(intent);
                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        placesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        placesAdapter.stopListening();
    }
}