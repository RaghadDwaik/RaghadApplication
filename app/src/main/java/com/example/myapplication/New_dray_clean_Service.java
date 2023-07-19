package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class New_dray_clean_Service extends AppCompatActivity {

    private EditText etServiceName, etServicePrice;
    private Button btnSaveService;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dray_clean_service);

        // Initialize the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("services");

        etServiceName = findViewById(R.id.et_service_name);
        etServicePrice = findViewById(R.id.et_service_price);
        btnSaveService = findViewById(R.id.btn_save_service);

        btnSaveService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addService();
            }
        });
    }

    private void addService() {
        String serviceName = etServiceName.getText().toString().trim();
        String servicePrice = etServicePrice.getText().toString().trim();

        if (serviceName.isEmpty() || servicePrice.isEmpty()) {
            Toast.makeText(this, "Please enter service name and price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a new key for the service
        String serviceId = databaseReference.push().getKey();

        // Create a Service object with the entered details
        Service service = new Service(serviceId, serviceName, Double.parseDouble(servicePrice));

        // Save the service object to Firebase Realtime Database
        databaseReference.child(serviceId).setValue(service);

        Toast.makeText(this, "Service added successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
