package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
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

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, RecycleViewOnItemClick, SearchView.OnQueryTextListener {

    private BottomNavigationView bottom;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private List<PlacesClass> novelsModels = new ArrayList<>();
    private List<PlacesClass> placesList = new ArrayList<>();
    private SearchView searchView;
    private RecommendAdapter adapter;
    private RecommendAdapter adapter1;
    private RecommendAdapter adapter2;
    private RecommendAdapter adapter3;
    private RecommendAdapter adapter4;
    private RecommendAdapter adapter5;

    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;
    private DatabaseReference databaseReference3;
    private DatabaseReference databaseReference4;
    private DatabaseReference databaseReference5;

    private boolean userLoggedIn;


    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private RecyclerView recyclerView3;
    private RecyclerView recyclerView4;
    private RecyclerView recyclerView5;


    ImageView love;
    ImageView list;


    private List<PlacesClass> Recommend;
    private List<PlacesClass> RecommendR;
    private List<PlacesClass> RecommendD;
    private List<PlacesClass> RecommendSt;
    private List<PlacesClass> RecommendS;
    private List<PlacesClass> RecommendDo;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    ////////////////nada////////////////////////////
    private LocationCallback locationCallback;
    ////////////////nada////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ////////////////nada////////////////////////////
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    // Use the retrieved location
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Toast.makeText(MainActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                }
            }
        };
        ////////////////nada////////////////////////////

        ImageView v = findViewById(R.id.slide);
        AnimationDrawable u = (AnimationDrawable) v.getDrawable();
        u.start();

        love = findViewById(R.id.love);
        list = findViewById(R.id.Rlist);


        final DrawerLayout drawerLayout = findViewById(R.id.DrawerLayout);
        bottom = findViewById(R.id.bottom);

        bottom.setOnNavigationItemSelectedListener(this);

        love.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, FavouriteList.class);
                startActivity(in);
            }
        });

        list.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, RecentlyView.class);
                startActivity(in);
            }
        });

        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        NavigationView nav = findViewById(R.id.navigation);
        nav.setItemIconTintList(null);

        BottomNavigationView nav1 = findViewById(R.id.bottom);
        nav1.setItemIconTintList(null);

        searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }


        recyclerView = findViewById(R.id.Recommended_recycler);
        recyclerView1 = findViewById(R.id.RecommendedR_recycler);
        recyclerView2 = findViewById(R.id.RecommendedD_recycler);
        recyclerView3 = findViewById(R.id.RecommendedS_recycler);
        //   recyclerView4 = findViewById(R.id.RecommendedSt_recycler);
        //    recyclerView5 = findViewById(R.id.RecommendedDo_recycler);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView3.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //   recyclerView4.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //    recyclerView5.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("RecommendedMarket");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Recommended");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("RecommendedClean");
        databaseReference3 = FirebaseDatabase.getInstance().getReference().child("RecommendedSalon");
        //  databaseReference4 = FirebaseDatabase.getInstance().getReference().child("Recommended");
        //   databaseReference5 = FirebaseDatabase.getInstance().getReference().child("Recommended");

        Recommend = new ArrayList<>();
        RecommendR = new ArrayList<>();
        RecommendD = new ArrayList<>();
        RecommendS = new ArrayList<>();


        Query query = databaseReference.orderByChild("rating").startAt(4);
        Query query1 = databaseReference1.orderByChild("rating").startAt(4);
        Query query2 = databaseReference2.orderByChild("rating").startAt(4);
        Query query3 = databaseReference3.orderByChild("rating").startAt(4);

        FirebaseRecyclerOptions<PlacesClass> options =
                new FirebaseRecyclerOptions.Builder<PlacesClass>()
                        .setQuery(query, PlacesClass.class)
                        .build();

        FirebaseRecyclerOptions<PlacesClass> options1 =
                new FirebaseRecyclerOptions.Builder<PlacesClass>()
                        .setQuery(query1, PlacesClass.class)
                        .build();

        FirebaseRecyclerOptions<PlacesClass> options2 =
                new FirebaseRecyclerOptions.Builder<PlacesClass>()
                        .setQuery(query2, PlacesClass.class)
                        .build();

        FirebaseRecyclerOptions<PlacesClass> options3 =
                new FirebaseRecyclerOptions.Builder<PlacesClass>()
                        .setQuery(query3, PlacesClass.class)
                        .build();

        adapter = new RecommendAdapter(options);
        adapter1 = new RecommendAdapter(options1);
        adapter2 = new RecommendAdapter(options2);
        adapter3 = new RecommendAdapter(options3);


        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setAdapter(adapter3);

        recyclerView.setAdapter(adapter);

        setupAdapterClickListener();
        setupAdapterClickListener1();
        setupAdapterClickListener2();
        setupAdapterClickListener3();


    }


    private void setupAdapterClickListener() {
        adapter.setOnItemClickListener((snapshot, position) -> {
            PlacesClass supermarket = snapshot.getValue(PlacesClass.class);

            Intent intent = new Intent(MainActivity.this, SupermarketItemList.class);
            intent.putExtra("supermarket_id", snapshot.getKey());
            intent.putExtra("supermarket_name", supermarket.getName());
            intent.putExtra("supermarket_image", supermarket.getImage());

            // Add any other necessary data as extras
            startActivity(intent);
        });
    }

    private void setupAdapterClickListener1() {
        adapter1.setOnItemClickListener((snapshot, position) -> {
            PlacesClass supermarket = snapshot.getValue(PlacesClass.class);

            Intent intent = new Intent(MainActivity.this, RestaurantList.class);
            intent.putExtra("restaurant_id", snapshot.getKey());
            intent.putExtra("restaurant_name", supermarket.getName());
            intent.putExtra("restaurant_image", supermarket.getImage());

            // Add any other necessary data as extras
            startActivity(intent);
        });
    }

    private void setupAdapterClickListener2() {
        adapter2.setOnItemClickListener((snapshot, position) -> {
            PlacesClass supermarket = snapshot.getValue(PlacesClass.class);

            Intent intent = new Intent(MainActivity.this, DryCleanList.class);
            intent.putExtra("dryclean_id", snapshot.getKey());
            intent.putExtra("dryclean_name", supermarket.getName());
            intent.putExtra("dryclean_image", supermarket.getImage());

            // Add any other necessary data as extras
            startActivity(intent);
        });
    }

    private void setupAdapterClickListener3() {
        adapter3.setOnItemClickListener((snapshot, position) -> {
            PlacesClass supermarket = snapshot.getValue(PlacesClass.class);

            Intent intent = new Intent(MainActivity.this, SalonList.class);
            intent.putExtra("salon_id", snapshot.getKey());
            intent.putExtra("salon_name", supermarket.getName());
            intent.putExtra("salon_image", supermarket.getImage());

            // Add any other necessary data as extras
            startActivity(intent);
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Toast.makeText(this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_LONG).show();
            }
        }
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
            case R.id.profile:

                // check if the user logged in or not
                userLoggedIn = checkUserLoggedIn();
                // Navigate to the appropriate fragment
                if (userLoggedIn) {
                    openProfileActivity();
                } else {
                    openLoginFragment();
                }
                return true;
//                Intent in2 = new Intent(this, Profile.class);
//                startActivity(in2);
//                return true;
        }
        return false;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLongItemClick(int position) {

    }

    public void salonclick(View view) {
        Intent in = new Intent(this, Salon.class);
        startActivity(in);
    }

    public void superMarketclick(View view) {
        Intent in = new Intent(this, SuperMarket.class);
        startActivity(in);
    }

    public void DryCleanClick(View view) {
        Intent in = new Intent(this, DryClean.class);
        startActivity(in);
    }

    public void ResturantClick(View view) {
        Intent in = new Intent(this, Restaurant.class);
        startActivity(in);
    }

    public void DormsClick(View view) {
        Intent in = new Intent(this, Dorms.class);
        startActivity(in);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        search(s);
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    private void search(String query) {
        DatabaseReference supermarketRef = FirebaseDatabase.getInstance().getReference().child("Supermarket");
        Query queryRef = supermarketRef.orderByChild("name").equalTo(query);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlacesClass supermarket = snapshot.getValue(PlacesClass.class);
                    // Perform any action with the searched supermarket
                    // For example, start the SupermarketItem activity and pass the supermarket ID
                    Intent intent = new Intent(MainActivity.this, SupermarketItemList.class);
                    intent.putExtra("supermarket_name", supermarket.getName());
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        DatabaseReference salonRef = FirebaseDatabase.getInstance().getReference().child("salon");
        Query querysalon = salonRef.orderByChild("name").equalTo(query);
        querysalon.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlacesClass supermarket = snapshot.getValue(PlacesClass.class);
                    // Perform any action with the searched supermarket
                    // For example, start the SupermarketItem activity and pass the supermarket ID
                    Intent intent = new Intent(MainActivity.this, SalonList.class);
                    intent.putExtra("salon_name", supermarket.getName());
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        //    ---------------------------------------
        DatabaseReference RestRef = FirebaseDatabase.getInstance().getReference().child("Resturant");
        Query queryRest = RestRef.orderByChild("name").equalTo(query);
        queryRest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlacesClass supermarket = snapshot.getValue(PlacesClass.class);
                    // Perform any action with the searched supermarket
                    // For example, start the SupermarketItem activity and pass the supermarket ID
                    Intent intent = new Intent(MainActivity.this, RestaurantList.class);
                    intent.putExtra("restaurant_name", supermarket.getName());
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        //-----------------------
        DatabaseReference dryRef = FirebaseDatabase.getInstance().getReference().child("DryClean");
        Query querydry = dryRef.orderByChild("name").equalTo(query);
        querydry.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlacesClass supermarket = snapshot.getValue(PlacesClass.class);
                    // Perform any action with the searched supermarket
                    // For example, start the SupermarketItem activity and pass the supermarket ID
                    Intent intent = new Intent(MainActivity.this, DryCleanList.class);
                    intent.putExtra("dryclean_name", supermarket.getName());
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });


        //-----------------------------------------

        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("Items");
        Query queryitem = itemRef.orderByChild("name").equalTo(query);
        queryitem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ServicesClass supermarket = snapshot.getValue(ServicesClass.class);
                    Intent intent = new Intent(MainActivity.this, ServiceDetails.class);

                    intent.putExtra("id", snapshot.getKey());
                    intent.putExtra("name", supermarket.getName());
                    intent.putExtra("price", supermarket.getPrice());
                    intent.putExtra("desc", supermarket.getDescription());
                    intent.putExtra("image", supermarket.getImage());

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });


        //------------------------------------------------

        DatabaseReference ItemRef = FirebaseDatabase.getInstance().getReference().child("SuperMarketItems");
        Query queryItem = ItemRef.orderByChild("name").equalTo(query);
        queryItem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ServicesClass supermarket = snapshot.getValue(ServicesClass.class);
                    Intent intent = new Intent(MainActivity.this, ServiceDetails.class);

                    intent.putExtra("id", snapshot.getKey());
                    intent.putExtra("name", supermarket.getName());
                    intent.putExtra("price", supermarket.getPrice());
                    intent.putExtra("desc", supermarket.getDescription());
                    intent.putExtra("image", supermarket.getImage());

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });


        //-------------------------------------------------
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("Services");
        Query queryservice = serviceRef.orderByChild("name").equalTo(query);
        queryservice.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ServicesClass supermarket = snapshot.getValue(ServicesClass.class);
                    Intent intent = new Intent(MainActivity.this, ServiceDetails.class);

                    intent.putExtra("id", snapshot.getKey());
                    intent.putExtra("name", supermarket.getName());
                    intent.putExtra("price", supermarket.getPrice());
                    intent.putExtra("desc", supermarket.getDescription());
                    intent.putExtra("image", supermarket.getImage());

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    protected void onStart() {
        super.onStart();
        ////////////////nada////////////////////////////
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        ////////////////nada////////////////////////////


        adapter.startListening();

        adapter1.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        ////////////////nada////////////////////////////
        mFusedLocationClient.removeLocationUpdates(locationCallback);
        ////////////////nada////////////////////////////
        adapter.stopListening();
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

    ////////////////nada////////////////////////////
    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    ////////////////nada////////////////////////////

}