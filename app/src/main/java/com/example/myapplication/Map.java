package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.databinding.ActivityMapBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Map extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Spinner spinner;
    private Button findbutton;
    private SupportMapFragment supportMapFragment;
    String TAG;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker mCurrLocationMarker;
    private ActivityMapBinding binding;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;
    private static final int REQUEST_CODE = 101;

    private PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_api_key));
        placesClient = Places.createClient(this);

        spinner = findViewById(R.id.spinner);
        findbutton = findViewById(R.id.findbutton);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        String[] placeTypeList = {"department_store", "restaurant", "beauty_salon", "laundry", " library", "supermarket"};
        String[] placeNameList = {"سكنات", "مطاعم", "صالونات", "دراي كلين", "اماكن للدراسة", "سوبرماركت"};

        spinner.setAdapter(new ArrayAdapter<>(Map.this, android.R.layout.simple_spinner_dropdown_item, placeNameList));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        placesClient = Places.createClient(this);


        getCurrentLocation();

        findbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = spinner.getSelectedItemPosition();
                String placeType = placeTypeList[i];
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                        "?location=" + currentLat + "," + currentLong +
                        "&radius=5000" +
                        "&types=" + placeType +
                        "&sensor=true" +
                        "&key=" + getResources().getString(R.string.google_maps_api_key);
                new PlaceTask().execute(url);
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
               //     currentLocation = location; // Assign the location to the currentLocation variable
//                       currentLat=31.761700;
//                       currentLong=-95.631523;
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    supportMapFragment.getMapAsync(Map.this);

                    Toast.makeText(getApplicationContext(), "Latitude: " + currentLat +
                            ", Longitude: " + currentLong, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


//    private void findNearbyPlaces(String placeType) {
//        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
//        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
//            placeResponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
//                @Override
//                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
//                    if (task.isSuccessful()) {
//                        FindCurrentPlaceResponse response = task.getResult();
//                        if (response != null) {
//                            List<PlaceLikelihood> placeLikelihoods = response.getPlaceLikelihoods();
//                            if (placeLikelihoods != null) {
//                                mMap.clear();
//                                for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
//                                    Place place = placeLikelihood.getPlace();
//                                    LatLng latLng = place.getLatLng();
//                                    String placeName = place.getName();
////                                    List<String> placeTypes = place.getTypes();
////                                    if (placeTypes != null && placeTypes.contains(placeType)){
//                                        MarkerOptions markerOptions = new MarkerOptions()
//                                                .position(latLng)
//                                                .title(placeName);
//                                        mMap.addMarker(markerOptions);
//                                    }
//                                }
//                            }
//
//                    } else {
//                        Exception exception = task.getException();
//                        if (exception != null) {
//                            Toast.makeText(Map.this, "Failed to find nearby places: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            });
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//    }

//    private List<String> convertTypesToStringList(List<Integer> types) {
//        List<String> stringList = new ArrayList<>();
//        if (types != null) {
//            for (Place.Type type : types) {
//                String typeString = type.toString();
//                stringList.add(typeString);
//            }
//        }
//        return stringList;
//    }

//    private String getPlaceTypeString(int type) {
//        switch (type) {
//            case Place.TYPE_RESTAURANT:
//                return "restaurant";
//            case Place.TYPE_SCHOOL:
//                return "school";
//            case Place.TYPE_STORE:
//                return "store";
//            // Add more place types here as needed
//            default:
//                return null;
//        }
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            requestLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentLat, currentLat), 10
            ));
            getCurrentLocation();
        }


    }

    private void requestLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                onLocationChanged(lastKnownLocation);
            }
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();
            List<HashMap<String, String>> mapList = null;
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                if (object.has("results")) {

                    mapList = jsonParser.parseResult(object);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            if (mMap != null) {
                mMap.clear();
                if (hashMaps != null) {
//                for (int i = 0; i < hashMaps.size(); i++) {
//                    HashMap<String, String> hashMapList = hashMaps.get(i);
                    for (HashMap<String, String> hashMapList : hashMaps) {
                        double lat = Double.parseDouble(hashMapList.get("lat"));
                        double lng = Double.parseDouble(hashMapList.get("lng"));
                        String name = hashMapList.get("name");
                        LatLng latLng = new LatLng(lat, lng);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(name);
                        mMap.addMarker(markerOptions);
                    }
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Not used in this code snippet
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Not used in this code snippet
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Not used in this code snippet
    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadURL(strings[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
            Log.d(TAG, "Response: " + s);
        }
    }


        private String downloadURL(String string) throws IOException {
            URL url = new URL(string);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            String data = builder.toString();
            reader.close();
            ;
            return data;
        }
    }
