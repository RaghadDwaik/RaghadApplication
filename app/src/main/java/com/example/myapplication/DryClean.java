package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

public class DryClean extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottom;
    private RecyclerView recyclerView;
    private PlacesAdapter adapter;
    private DatabaseReference databaseReference;
    private List<PlacesClass> allDryclean;

    private boolean userLoggedIn;

    private SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_market);
        userLoggedIn = checkUserLoggedIn();
        recyclerView = findViewById(R.id.supermarket_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("DryClean");
        allDryclean = new ArrayList<>();

        setupAdapter();
        setupAdapterClickListener();

        bottom = findViewById(R.id.bottom);
        bottom.setItemIconTintList(null);

        bottom.setOnNavigationItemSelectedListener(this);

        searchView = findViewById(R.id.search);
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
    }

    private void setupAdapter() {
        Query query = databaseReference.orderByChild("name");

        FirebaseRecyclerOptions<PlacesClass> options =
                new FirebaseRecyclerOptions.Builder<PlacesClass>()
                        .setQuery(query, PlacesClass.class)
                        .build();

        adapter = new PlacesAdapter(options);
        recyclerView.setAdapter(adapter);
    }
    private boolean checkUserLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;

    }

    private void openProfileActivity() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    private void openLoginFragment() {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }


    private void setupAdapterClickListener() {
        adapter.setOnItemClickListener((snapshot, position) -> {
            PlacesClass dry = snapshot.getValue(PlacesClass.class);




            Intent intent = new Intent(DryClean.this, DryCleanList.class);
            intent.putExtra("dryclean_id", snapshot.getKey());
            intent.putExtra("dryclean_name", dry.getName());
            intent.putExtra("dryclean_image", dry.getImage());

            // Add any other necessary data as extras
            startActivity(intent);
        });
    }

    private void searchFirebase(String query) {
        Query searchQuery = databaseReference.orderByChild("name")
                .startAt(query)
                .endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<PlacesClass> options =
                new FirebaseRecyclerOptions.Builder<PlacesClass>()
                        .setQuery(searchQuery, PlacesClass.class)
                        .build();

        adapter.updateOptions(options);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allDryclean.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlacesClass supermarket = snapshot.getValue(PlacesClass.class);
                    allDryclean.add(supermarket);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        View view = findViewById(android.R.id.content); // Replace with the appropriate View ID

        new CustomToast().Show_Toast(DryClean.this, view, "This Feature is not supported whithout login please login ");

        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

}
