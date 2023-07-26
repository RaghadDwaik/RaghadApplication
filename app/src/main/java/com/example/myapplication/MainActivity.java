package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import java.util.Collections;
import java.util.List;
import android.content.res.Configuration;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, RecycleViewOnItemClick, SearchView.OnQueryTextListener {

    private BottomNavigationView bottom;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Spinner languageSpinner;
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
    List<PlacesClass> recommendedMarkets = new ArrayList<>();
    List<PlacesClass> recommendedSalon = new ArrayList<>();
    List<PlacesClass> recommendedRest = new ArrayList<>();
    List<PlacesClass> recommendedClean = new ArrayList<>();

    private boolean userLoggedIn;
    Query query;

    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private RecyclerView recyclerView3;
    private RecyclerView recyclerView4;
    private RecyclerView recyclerView5;


    ImageView love;
    ImageView list;
    EditText View;




    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient mFusedLocationClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ImageView v = findViewById(R.id.slide);
//        AnimationDrawable u = (AnimationDrawable) v.getDrawable();
//        u.start();
        userLoggedIn = checkUserLoggedIn();

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);



//        love = findViewById(R.id.love);
//        list = findViewById(R.id.Rlist);

        LanguageUtils.setAppLanguage(this, "ar");  // Set Arabic as the default language

        // Change the language to English
        Locale locale = new Locale("en");
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

//        final DrawerLayout drawerLayout = findViewById(R.id.DrawerLayout);
        bottom = findViewById(R.id.bottom);

        bottom.setOnNavigationItemSelectedListener(this);
        

//
//        navigationView = findViewById(R.id.navigation);
//
//        ImageView imageMenu= findViewById(R.id.imageMenu);
//        Toolbar toolbar = findViewById(R.id.toolBar);
//        setSupportActionBar(toolbar);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, imageMenu, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        imageMenu.addDrawerListener(toggle);
//        toggle.syncState();
        ImageView imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.home:
                        Intent in = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(in);
                        return true;

                    case  R.id.about:
                    Intent in5 = new Intent(MainActivity.this, AboutUs.class);
                    startActivity(in5);
                    return true;

                    case R.id.map:
                        Intent in1 = new Intent(MainActivity.this, Map.class);
                        startActivity(in1);
                        return true;

                    case R.id.favorite:
                        // Check if the user is logged in
                        if (userLoggedIn) {
                            Intent in9 = new Intent(MainActivity.this, FavouriteList.class);
                            startActivity(in9);
                        } else {
                            // User is not logged in, show a message or launch the login activity
                            showLoginPrompt();
                        }
                        return true;
                    case R.id.Recently:
                        if (userLoggedIn) {
                            Intent in4 = new Intent(MainActivity.this, RecentlyView.class);
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

                        drawerLayout.closeDrawer(GravityCompat.START);

                        return true;
                }
                return false;
            }
        });


        // Enable the toolbar and add a menu icon
//        Toolbar toolbar = findViewById(R.id.toolBar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.list); // Replace with your menu icon
//        actionBar.setDisplayHomeAsUpEnabled(true);

//        love.setOnClickListener(new View.OnClickListener() {
//
//
//            @Override
//            public void onClick(View view) {
//                Intent in = new Intent(MainActivity.this, FavouriteList.class);
//                startActivity(in);
//            }
//        });

//        list.setOnClickListener(new View.OnClickListener() {
//
//
//            @Override
//            public void onClick(View view) {
//                Intent in = new Intent(MainActivity.this, RecentlyView.class);
//                startActivity(in);
//            }
//        });

//        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
//        NavigationView nav = findViewById(R.id.navigation);
//        nav.setItemIconTintList(null);

        BottomNavigationView nav1 = findViewById(R.id.bottom);
        nav1.setItemIconTintList(null);

         searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(this);
       searchView.setQueryHint("ابحث هنا....");


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


        DatabaseReference recommendedMarketsRef = FirebaseDatabase.getInstance().getReference().child("RecommendedMarket");
        query = recommendedMarketsRef.limitToLast(5);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot supermarketSnapshot : dataSnapshot.getChildren()) {
                    String supermarketKey = supermarketSnapshot.getKey();
                    double totalRating = 0;
                    int numUsers = 0;

                    for (DataSnapshot ratingSnapshot : supermarketSnapshot.getChildren()) {
                        double rating = ratingSnapshot.child("rating").getValue(Double.class);
                        totalRating += rating;
                        numUsers++;
                    }

                    double averageRating = totalRating / numUsers;

                    // Retrieve other supermarket details such as name, image, etc.
                    String name = null;
                    String image = null;

                    for (DataSnapshot ratingSnapshot : supermarketSnapshot.getChildren()) {
                        name = ratingSnapshot.child("name").getValue(String.class);
                        image = ratingSnapshot.child("image").getValue(String.class);
                        break; // Break after retrieving the details from the first rating entry
                    }

                    if (name != null && image != null) {
                        PlacesClass market = new PlacesClass(name, image);
                        market.setRating((float) averageRating);
                        recommendedMarkets.add(market);
                    }
                }

                // Sort the recommended supermarkets based on average ratings in descending order
                Collections.sort(recommendedMarkets, new PlacesClass());

                // Display the sorted supermarkets in the RecyclerView
                adapter5 = new RecommendAdapter(recommendedMarkets);
                recyclerView.setAdapter(adapter5);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                adapter5.notifyDataSetChanged();

                adapter5.setOnItemClickListener(new RecommendAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PlacesClass item, int position) {
                        // Handle the item click event for the specific item
                        // You can access the item details using item.getId(), item.getRating(), etc.
                        String supermarketId = item.getId();
                        float rating = item.getRating();
                        String name = item.getName();
                        String image = item.getImage();

                        Intent intent = new Intent(MainActivity.this, SupermarketItemList.class);
                        intent.putExtra("supermarket_id", supermarketId);
                        intent.putExtra("supermarket_name", name);
                        intent.putExtra("supermarket_image", image);
                        intent.putExtra("supermarket_rating", rating);

                        // Add any other necessary data as extras
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the data retrieval
            }
        });


        ///-----------------------------------------------------------------------------

        DatabaseReference recommendedSalonRef = FirebaseDatabase.getInstance().getReference().child("RecommendedSalon");
        Query  query1 = recommendedSalonRef.limitToLast(5);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot supermarketSnapshot : dataSnapshot.getChildren()) {
                    String supermarketKey = supermarketSnapshot.getKey();
                    double totalRating = 0;
                    int numUsers = 0;

                    for (DataSnapshot ratingSnapshot : supermarketSnapshot.getChildren()) {
                        double rating = ratingSnapshot.child("rating").getValue(Double.class);
                        totalRating += rating;
                        numUsers++;
                    }

                    double averageRating = totalRating / numUsers;

                    // Retrieve other supermarket details such as name, image, etc.
                    String name = null;
                    String image = null;

                    for (DataSnapshot ratingSnapshot : supermarketSnapshot.getChildren()) {
                        name = ratingSnapshot.child("name").getValue(String.class);
                        image = ratingSnapshot.child("image").getValue(String.class);
                        break; // Break after retrieving the details from the first rating entry
                    }

                    if (name != null && image != null) {
                        PlacesClass market = new PlacesClass(name, image);
                        market.setRating((float) averageRating);
                        recommendedSalon.add(market);
                    }
                }

                Collections.sort(recommendedSalon, new PlacesClass());

                adapter1 = new RecommendAdapter(recommendedSalon);
                recyclerView1.setAdapter(adapter1);
                recyclerView1.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                adapter1.notifyDataSetChanged();

                adapter1.setOnItemClickListener(new RecommendAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PlacesClass item, int position) {
                        // Handle the item click event for the specific item
                        // You can access the item details using item.getId(), item.getRating(), etc.
                        String supermarketId = item.getId();
                        float rating = item.getRating();
                        String name = item.getName();
                        String image = item.getImage();

                        Intent intent = new Intent(MainActivity.this, SalonList.class);
                        intent.putExtra("salon_id", supermarketId);
                        intent.putExtra("salon_name", name);
                        intent.putExtra("salon_image", image);
                        intent.putExtra("salon_rating", rating);

                        // Add any other necessary data as extras
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the data retrieval
            }
        });


        ///-----------------------------------------------------------------------------

        DatabaseReference recommendedRestRef = FirebaseDatabase.getInstance().getReference().child("RecommendedRest");
        Query  query2 = recommendedRestRef.limitToLast(5);

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot supermarketSnapshot : dataSnapshot.getChildren()) {
                    String supermarketKey = supermarketSnapshot.getKey();
                    double totalRating = 0;
                    int numUsers = 0;

                    for (DataSnapshot ratingSnapshot : supermarketSnapshot.getChildren()) {
                        double rating = ratingSnapshot.child("rating").getValue(Double.class);
                        totalRating += rating;
                        numUsers++;
                    }

                    double averageRating = totalRating / numUsers;

                    // Retrieve other supermarket details such as name, image, etc.
                    String name = null;
                    String image = null;

                    for (DataSnapshot ratingSnapshot : supermarketSnapshot.getChildren()) {
                        name = ratingSnapshot.child("name").getValue(String.class);
                        image = ratingSnapshot.child("image").getValue(String.class);
                        break;
                    }

                    if (name != null && image != null) {
                        PlacesClass market = new PlacesClass(name, image);
                        market.setRating((float) averageRating);
                        recommendedRest.add(market);
                    }
                }

                Collections.sort(recommendedRest, new PlacesClass());

                adapter2 = new RecommendAdapter(recommendedRest);
                recyclerView2.setAdapter(adapter2);
                recyclerView2.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                adapter2.notifyDataSetChanged();

                adapter2.setOnItemClickListener(new RecommendAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PlacesClass item, int position) {
                        // Handle the item click event for the specific item
                        // You can access the item details using item.getId(), item.getRating(), etc.
                        String supermarketId = item.getId();
                        float rating = item.getRating();
                        String name = item.getName();
                        String image = item.getImage();

                        Intent intent = new Intent(MainActivity.this, RestaurantList.class);
                        intent.putExtra("restaurant_id", supermarketId);
                        intent.putExtra("restaurant_name", name);
                        intent.putExtra("restaurant_image", image);
                        intent.putExtra("restaurant_rating", rating);

                        // Add any other necessary data as extras
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the data retrieval
            }
        });



        ///-----------------------------------------------------------------------------

        DatabaseReference recommendedDryRef = FirebaseDatabase.getInstance().getReference().child("RecommendedClean");
        Query  query3 = recommendedDryRef.limitToLast(5);

        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot supermarketSnapshot : dataSnapshot.getChildren()) {
                    String supermarketKey = supermarketSnapshot.getKey();
                    double totalRating = 0;
                    int numUsers = 0;

                    for (DataSnapshot ratingSnapshot : supermarketSnapshot.getChildren()) {
                        double rating = ratingSnapshot.child("rating").getValue(Double.class);
                        totalRating += rating;
                        numUsers++;
                    }

                    double averageRating = totalRating / numUsers;

                    // Retrieve other supermarket details such as name, image, etc.
                    String name = null;
                    String image = null;

                    for (DataSnapshot ratingSnapshot : supermarketSnapshot.getChildren()) {
                        name = ratingSnapshot.child("name").getValue(String.class);
                        image = ratingSnapshot.child("image").getValue(String.class);
                        break;
                    }

                    if (name != null && image != null) {
                        PlacesClass market = new PlacesClass(name, image);
                        market.setRating((float) averageRating);
                        recommendedClean.add(market);
                    }
                }

                Collections.sort(recommendedClean, new PlacesClass());

                adapter3 = new RecommendAdapter(recommendedClean);
                recyclerView3.setAdapter(adapter3);
                recyclerView3.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                adapter3.notifyDataSetChanged();

                adapter3.setOnItemClickListener(new RecommendAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PlacesClass item, int position) {
                        // Handle the item click event for the specific item
                        // You can access the item details using item.getId(), item.getRating(), etc.
                        String supermarketId = item.getId();
                        float rating = item.getRating();
                        String name = item.getName();
                        String image = item.getImage();

                        Intent intent = new Intent(MainActivity.this, DryCleanList.class);
                        intent.putExtra("dryclean_id", supermarketId);
                        intent.putExtra("dryclean_name", name);
                        intent.putExtra("dryclean_image", image);
                        intent.putExtra("dryclean_rating", rating);

                        // Add any other necessary data as extras
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the data retrieval
            }
        });

        //---------------------------------------------------------------------

        DatabaseReference recommendedStudyRef = FirebaseDatabase.getInstance().getReference().child("RecommendedStudy");
        Query  query4 = recommendedStudyRef.limitToLast(5);

        query4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot supermarketSnapshot : dataSnapshot.getChildren()) {
                    String supermarketKey = supermarketSnapshot.getKey();
                    double totalRating = 0;
                    int numUsers = 0;

                    for (DataSnapshot ratingSnapshot : supermarketSnapshot.getChildren()) {
                        double rating = ratingSnapshot.child("rating").getValue(Double.class);
                        totalRating += rating;
                        numUsers++;
                    }

                    double averageRating = totalRating / numUsers;

                    // Retrieve other supermarket details such as name, image, etc.
                    String name = null;
                    String image = null;

                    for (DataSnapshot ratingSnapshot : supermarketSnapshot.getChildren()) {
                        name = ratingSnapshot.child("name").getValue(String.class);
                        image = ratingSnapshot.child("image").getValue(String.class);
                        break;
                    }

                    if (name != null && image != null) {
                        PlacesClass market = new PlacesClass(name, image);
                        market.setRating((float) averageRating);
                        recommendedClean.add(market);
                    }
                }

                Collections.sort(recommendedClean, new PlacesClass());

                adapter3 = new RecommendAdapter(recommendedClean);
                recyclerView3.setAdapter(adapter3);
                recyclerView3.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                adapter3.notifyDataSetChanged();

                adapter3.setOnItemClickListener(new RecommendAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PlacesClass item, int position) {
                        // Handle the item click event for the specific item
                        // You can access the item details using item.getId(), item.getRating(), etc.
                        String StudyPlaceId = item.getId();
                        float rating = item.getRating();
                        String name = item.getName();
                        String image = item.getImage();

                        Intent intent = new Intent(MainActivity.this, DryCleanList.class);
                        intent.putExtra("StudyPlace_id", StudyPlaceId);
                        intent.putExtra("StudyPlace_name", name);
                        intent.putExtra("StudyPlace_image", image);
                        intent.putExtra("StudyPlace_rating", rating);

                        // Add any other necessary data as extras
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the data retrieval
            }
        });
    }

//    private void changeLanguage(String newLanguage) {
//        // Save the new language preference
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        preferences.edit().putString("language", newLanguage).apply();
//
//        // Set the new language configuration
//        Locale locale = new Locale(newLanguage);
//        Locale.setDefault(locale);
//
//        Configuration config = new Configuration();
//        config.locale = locale;
//
//        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
//
//        // Recreate the activity to apply the language change
//        recreate();
//    }

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

        new CustomToast().Show_Toast(MainActivity.this, view, "This Feature is not supported whithout login please login ");

        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
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
    public void StudyClick(View view){
        Intent in = new Intent(this, StudyPlaces.class);
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
                    Intent intent = new Intent(MainActivity.this,RestaurantList.class);
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

        DatabaseReference studyplaceRef = FirebaseDatabase.getInstance().getReference().child("StudyPlaces");
        Query querystudyRef = studyplaceRef.orderByChild("name").equalTo(query);
        querystudyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlacesClass studyplace = snapshot.getValue(PlacesClass.class);
                    // Perform any action with the searched supermarket
                    // For example, start the SupermarketItem activity and pass the supermarket ID
                    Intent intent = new Intent(MainActivity.this, StudyPlacesList.class);
                    intent.putExtra("studyplace_name", studyplace.getName());
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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


}