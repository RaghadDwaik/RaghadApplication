package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductActivity extends AppCompatActivity {

    private EditText productNameEditText;
    private EditText productDescriptionEditText;
    private Button addProductButton;

    private DatabaseReference productsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize the products reference
        productsReference = FirebaseDatabase.getInstance().getReference().child("Supermarket").child("Products");

        productNameEditText = findViewById(R.id.edit_text_product_name);
        productDescriptionEditText = findViewById(R.id.edit_text_product_description);
        addProductButton = findViewById(R.id.button_add_product);

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = productNameEditText.getText().toString().trim();
                String productDescription = productDescriptionEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(productName) && !TextUtils.isEmpty(productDescription)) {
                    addProductToSupermarket(productName, productDescription);
                } else {
                    Toast.makeText(AddProductActivity.this, "Please enter product name and description", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addProductToSupermarket(String productName, String productDescription) {
        // Generate a new unique key for the product
        String productKey = productsReference.push().getKey();

        // Create a new Product object with the provided data
        Product product = new Product(productName, productDescription);

        // Save the product to the database under the generated key
        productsReference.child(productKey).setValue(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Product added successfully
                        Toast.makeText(AddProductActivity.this, "Product added to the supermarket", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity after adding the product
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while adding the product
                        Toast.makeText(AddProductActivity.this, "Failed to add the product", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
