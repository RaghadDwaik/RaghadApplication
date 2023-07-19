package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewDormActivity extends AppCompatActivity {

    private EditText etLocation, etPrice;
    private Button btnChooseImage, btnSaveDorm;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dorm);

        // Initialize the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("dorms");

        etLocation = findViewById(R.id.et_location);
        etPrice = findViewById(R.id.et_price);
        btnChooseImage = findViewById(R.id.btn_choose_image);
        btnSaveDorm = findViewById(R.id.btn_save_dorm);

        btnSaveDorm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDorm();
            }
        });
    }

    private void saveDorm() {
        String location = etLocation.getText().toString().trim();
        String price = etPrice.getText().toString().trim();

        if (location.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Please enter location and price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a new key for the dorm
        String dormId = databaseReference.push().getKey();

        // Create a Dorm object with the entered details
        Dorme dorm = new Dorme(dormId, location, Double.parseDouble(price));

        // Save the dorm object to Firebase Realtime Database
        databaseReference.child(dormId).setValue(dorm);
        Toast.makeText(this, "Dorm added successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
