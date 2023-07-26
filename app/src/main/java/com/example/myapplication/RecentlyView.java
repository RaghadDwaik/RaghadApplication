package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecentlyView extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, UserAdapter.OnItemClickListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private BottomNavigationView bottom;
    private UserAdapter adapter;
    private ImageView imageButton;

   // private List<PlacesClass> recentlyViewedList;
    private SearchView searchView;
    private CollectionReference recentlyViewedRef;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_view);
//        imageButton  = findViewById(R.id.backButton);
//       imageButton.setOnClickListener(this);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("ImageButton", "Back button clicked");
//                Toast.makeText(RecentlyView.this, "Back button clicked", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(RecentlyView.this, MainActivity.class);
//                startActivity(intent);
//
//            }
//        });



        recyclerView = findViewById(R.id.Recently_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference favoritesRef = db.collection("User")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("RecentlyV");

        FirestoreRecyclerOptions<ServicesClass> options =
                new FirestoreRecyclerOptions.Builder<ServicesClass>()
                        .setQuery(favoritesRef, ServicesClass.class)
                        .build();

        adapter = new UserAdapter(options);
        adapter.setOnItemClickListener(this);

        recyclerView.setAdapter(adapter);

        bottom = findViewById(R.id.bottom);
        bottom.setItemIconTintList(null);
        bottom.setOnNavigationItemSelectedListener(this);





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
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        ServicesClass place = snapshot.toObject(ServicesClass.class);

        if (place != null) {
            Intent intent = new Intent(RecentlyView.this, ServiceDetails.class);
            intent.putExtra("id", snapshot.getId());
            intent.putExtra("name", place.getName());
            intent.putExtra("price", place.getPrice());
            intent.putExtra("desc", place.getDescription());
            intent.putExtra("image", place.getImage());

            // Add any other necessary data as extras
            startActivity(intent);
        }
    }

//    public void backButton(View view) {
//        Intent intent = new Intent(RecentlyView.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }

    @Override
    public void onClick(View v) {
//        Intent intent = new Intent(RecentlyView.this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }
}
