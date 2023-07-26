package com.example.myapplication;

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

        // Retrieve the ownerId from the intent
        ownerId = getIntent().getStringExtra("ownerId");

        // Initialize views
        placesRecyclerView = findViewById(R.id.placesRecyclerView);
      //  placesList = new ArrayList<>();

        // Query Firestore to fetch the places associated with the ownerId
        Query query = db.collection("Places").whereEqualTo("ownerId", ownerId);

        FirestoreRecyclerOptions<PlacesClass> options = new FirestoreRecyclerOptions.Builder<PlacesClass>()
                .setQuery(query, PlacesClass.class)
                .build();

        // Initialize the PlacesAdapter with the FirestoreRecyclerOptions
        placesAdapter = new PlaceOwnerAdapter(options);

        placesRecyclerView.setLayoutManager(new GridLayoutManager(this,2));

        placesRecyclerView.setAdapter(placesAdapter);
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