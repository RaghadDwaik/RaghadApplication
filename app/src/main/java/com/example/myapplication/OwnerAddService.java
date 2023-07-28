package com.example.myapplication;

import android.content.Intent;
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

public class OwnerAddService extends AppCompatActivity {

    private EditText productNameEditText, nameee;
    private EditText productDescriptionEditText, desc;
    private EditText price;
    private EditText image, image11, image22, image33;

    private Button addProductButton;
    String productName;

    private DatabaseReference productsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        productNameEditText = findViewById(R.id.edit_text_product_name);
        price = findViewById(R.id.price);
        image = findViewById(R.id.imagee);
        productDescriptionEditText = findViewById(R.id.edit_text_product_description);
        addProductButton = findViewById(R.id.button_add_product);

        String placeType = getIntent().getStringExtra("placeType");
        String name = getIntent().getStringExtra(placeType);

        System.out.println("place Nmae eeeeeeeeeeeeeee "+name);

        if (placeType.equals("dorms")|| placeType.equals("studyplace")) {
            Intent in = new Intent(OwnerAddService.this, AddDorms.class);
            in.putExtra("placeType", placeType);
            in.putExtra(placeType, name);

            startActivity(in);
        }

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productName = productNameEditText.getText().toString().trim();
                String productDescription = productDescriptionEditText.getText().toString().trim();
                String image1 = image.getText().toString().trim();
                double pricee = Double.parseDouble(price.getText().toString().trim());
                String namee = placeType.toString();




                if (!TextUtils.isEmpty(productName) && !TextUtils.isEmpty(productDescription)) {
                    if (placeType != null) {
                        if (placeType.equals("salon")) {

                            ServicesClass product = new ServicesClass();
                            product.setName(productName);
                            product.setImage(image1);
                            product.setPrice(pricee);
                            product.setDescription(productDescription);
                            product.setSalon(name);
                            productsReference = FirebaseDatabase.getInstance().getReference().child("salonServices"); // Update the DatabaseReference

                            productsReference.child(productName).setValue(product)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Product added successfully
                                            Toast.makeText(OwnerAddService.this, "Product added to the "+placeType, Toast.LENGTH_SHORT).show();
                                            finish(); // Finish the activity after adding the product
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error occurred while adding the product
                                            Toast.makeText(OwnerAddService.this, "Failed to add the product", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else if (placeType.equals("supermarket")) {
                            ServicesClass product = new ServicesClass();
                            product.setName(productName);
                            product.setImage(image1);
                            product.setPrice(pricee);
                            product.setDescription(productDescription);

                            product.setSupermarket(name);

                            productsReference = FirebaseDatabase.getInstance().getReference().child("SuperMarketItems");

                            productsReference.child(productName).setValue(product)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Product added successfully
                                            Toast.makeText(OwnerAddService.this, "Product added to the "+placeType, Toast.LENGTH_SHORT).show();
                                            finish(); // Finish the activity after adding the product
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error occurred while adding the product
                                            Toast.makeText(OwnerAddService.this, "Failed to add the product", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else if (placeType.equals("resturant")) {
                            ServicesClass product = new ServicesClass();
                            product.setName(productName);
                            product.setImage(image1);
                            product.setPrice(pricee);
                            product.setDescription(productDescription);


                            product.setResturant(name);
                            productsReference = FirebaseDatabase.getInstance().getReference().child("Items");

                            productsReference.child(productName).setValue(product)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Product added successfully
                                            Toast.makeText(OwnerAddService.this, "Product added to the "+placeType, Toast.LENGTH_SHORT).show();
                                            finish(); // Finish the activity after adding the product
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error occurred while adding the product
                                            Toast.makeText(OwnerAddService.this, "Failed to add the product", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else if (placeType.equals("dryclean")) {
                            ServicesClass product = new ServicesClass();
                            product.setName(productName);
                            product.setImage(image1);
                            product.setPrice(pricee);
                            product.setDescription(productDescription);

                            product.setDryClean(name);

                            productsReference = FirebaseDatabase.getInstance().getReference().child("DryCleanServices");

                            productsReference.child(productName).setValue(product)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Product added successfully
                                            Toast.makeText(OwnerAddService.this, "Product added to the "+placeType, Toast.LENGTH_SHORT).show();
                                            finish(); // Finish the activity after adding the product
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error occurred while adding the product
                                            Toast.makeText(OwnerAddService.this, "Failed to add the product", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }
                }
            }
        });


    }}