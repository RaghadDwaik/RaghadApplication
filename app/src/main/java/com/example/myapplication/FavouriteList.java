package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FavouriteList extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, UserAdapter.OnItemClickListener {

    private BottomNavigationView bottom;
    private RecyclerView recyclerView;
    private UserAdapter favAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);

        recyclerView = findViewById(R.id.Favourite_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference favoritesRef = db.collection("User")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("favorite");

        FirestoreRecyclerOptions<ServicesClass> options =
                new FirestoreRecyclerOptions.Builder<ServicesClass>()
                        .setQuery(favoritesRef, ServicesClass.class)
                        .build();

        favAdapter = new UserAdapter(options);
        favAdapter.setOnItemClickListener(this);

        recyclerView.setAdapter(favAdapter);

        bottom = findViewById(R.id.bottom);
        bottom.setItemIconTintList(null);
        bottom.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        favAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        favAdapter.stopListening();
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
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        ServicesClass place = snapshot.toObject(ServicesClass.class);

        if (place != null) {
            Intent intent = new Intent(FavouriteList.this, ServiceDetails.class);
            intent.putExtra("id", snapshot.getId());
            intent.putExtra("name", place.getName());
            intent.putExtra("price", place.getPrice());
            intent.putExtra("desc", place.getDescription());
            intent.putExtra("image", place.getImage());

            // Add any other necessary data as extras
            startActivity(intent);
        }
    }

}
