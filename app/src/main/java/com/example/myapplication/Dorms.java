package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Dorms extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottom;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    PlacesAdapter adapter;
    private List<PlacesClass> dorm = new ArrayList<>();
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorms);

        recyclerView = findViewById(R.id.dorms_recycler);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Dorms");
        dorm = new ArrayList<>();

        setupAdapter();
        setupAdapterClickListener();

        bottom = findViewById(R.id.bottom);
        BottomNavigationView nav1 = findViewById(R.id.bottom);
        nav1.setItemIconTintList(null);


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

    private void setupAdapterClickListener() {
        adapter.setOnItemClickListener((snapshot, position) -> {
            PlacesClass supermarket = snapshot.getValue(PlacesClass.class);



            Intent intent = new Intent(Dorms.this, DormsDetails.class);
            intent.putExtra("Dorms_id", snapshot.getKey());
            intent.putExtra("Dorms_name", supermarket.getName());
            intent.putExtra("Dorms_image", supermarket.getImage());

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
                dorm.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlacesClass supermarket = snapshot.getValue(PlacesClass.class);
                    dorm.add(supermarket);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    // Function to tell the app to stop getting
    // data from database on stopping of the activity
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
}