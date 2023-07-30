package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OwnerAddPlace extends AppCompatActivity {

    private EditText placeNameEditText;
    private EditText placeAddressEditText;
    private EditText placeDescriptionEditText;
    private Button saveButton;
    private Spinner spinner;
    private String selectedPlace; // This will store the Arabic label of the selected place
    private String selectedPlaceEnglish; // This will store the corresponding English value
    private EditText image;
    String placeName;
    public static boolean visited = false;


    String ownerId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owneraddplace);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the ownerId from the intent
        ownerId = getIntent().getStringExtra("ownerId");

        // Initialize views
        placeNameEditText = findViewById(R.id.placeNameEditText);
        boolean visited = getIntent().getBooleanExtra("visited", false);


        image = findViewById(R.id.imagee);
        saveButton = findViewById(R.id.saveButton);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.places, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPlace = parent.getItemAtPosition(position).toString();
             //   Toast.makeText(OwnerAddPlace.this, "Selected Place: " + selectedPlace, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String englishPlaceType = convertToEnglish(selectedPlace);
                savePlaceDetails(ownerId);
                Intent intent = new Intent(OwnerAddPlace.this, OwnerAddService.class);
                intent.putExtra("placeType", englishPlaceType);

                //  Toast.makeText(OwnerAddPlace.this, "Selected Place: " + englishPlaceType, Toast.LENGTH_SHORT).show();
                intent.putExtra(englishPlaceType, placeName);
                startActivity(intent);
            }
        });

    }

    private String  convertToEnglish(String arabicLabel) {
        // Use switch statement or custom mapping method to convert Arabic to English
        switch (arabicLabel) {
            case "مطعم":
                return "resturant";
            case "سوبرماركت":
                return "supermarket";
            case "صالون":
                return "salon";
            case "مكان دراسة":
                return "studyplace";
            case "سكن":
                return "dorms";
            case "دراي كلين":
                return "dryclean";
            // Add more cases for other places
            default:
                return ""; // Return empty string or null if no match found
        }
    }

//    private String getEnglishValue(String arabicLabel) {
//         Map<String, String> arabicToEnglishMap = new HashMap<>();
//        arabicToEnglishMap.put("مطعم", "Resturant");
//        arabicToEnglishMap.put("سوبرماركت", "Supermarket");
//        arabicToEnglishMap.put("صالون", "salon");
//        arabicToEnglishMap.put("مكان دراسة", "StudyPlace");
//        arabicToEnglishMap.put("سكن", "Dorms");
//        arabicToEnglishMap.put("دراي كلين", "DryClean");
//        // Add more mappings as needed
//        return arabicToEnglishMap.get(arabicLabel);
//
//    }

    private void savePlaceDetails(String ownerId) {
        // Retrieve the place details from the input fields
        placeName = placeNameEditText.getText().toString();
        String image1 = image.getText().toString();
        String placeTypear = spinner.getSelectedItem().toString();
      String  placeType= convertToEnglish(placeTypear);


        // Create a new document in the "Places" collection
        DocumentReference placeRef = db.collection("Places").document();



        // Create a HashMap to store the place details
        Map<String, Object> placeInfo = new HashMap<>();
        placeInfo.put("name", placeName);
        placeInfo.put("ownerId", ownerId); // Associate the owner with the place
        placeInfo.put("placeType", placeType); // Add the selected place type
        placeInfo.put("image", image1); // Add the selected place type


        // Set the place information in the Firestore document
        placeRef.set(placeInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document successfully written
                        Toast.makeText(OwnerAddPlace.this, "Place details saved successfully.", Toast.LENGTH_SHORT).show();

                        // Write the name and image to the Realtime Database based on the place type
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(placeType).child(placeName);
                        Map<String, Object> placeData = new HashMap<>();
                        placeData.put("name", placeName);
                        placeData.put("image", image1);
                        placeData.put("ownerId", ownerId);// Add the image URL here, or you can upload the image to Firebase Storage and store its URL here.
                        databaseReference.setValue(placeData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Name and image added to the Realtime Database
                                        Toast.makeText(OwnerAddPlace.this, "Name and image added to the Realtime Database.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error adding name and image to the Realtime Database
                                        Toast.makeText(OwnerAddPlace.this, "Failed to add name and image to the Realtime Database.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        // Optionally, you can navigate to another activity or perform further actions
                        finish(); // Finish the activity after saving the place details
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error writing document
                        Toast.makeText(OwnerAddPlace.this, "Failed to save place details.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void saveButtonClicked(View view) {
        savePlaceDetails(ownerId);

    }
}

