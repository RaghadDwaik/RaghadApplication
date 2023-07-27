package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.nullness.qual.NonNull;

public class AddDorms extends AppCompatActivity {
    private EditText image, image11, image22, image33;
    private EditText productNameEditText, nameee;
    private EditText productDescriptionEditText, desc;
    private EditText price;
    private Button addProductButton;


    private DatabaseReference productsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dorms);

        String placeType = getIntent().getStringExtra("placeType");
        String name = getIntent().getStringExtra("placeName");

        System.out.println("placcccccccccccccccccccccccccccce dd " + placeType);

        if (placeType.equals("Dorms")) {
            image11 = findViewById(R.id.image1);
            image22 = findViewById(R.id.image2);
            image33 = findViewById(R.id.image3);
            nameee = findViewById(R.id.edit_text_product_name);
            desc = findViewById(R.id.edit_text_product_description);

            addProductButton = findViewById(R.id.button_add_product);

            addProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get additional data from the additional views
                    String des = desc.getText().toString().trim();
                    String imagee = image11.getText().toString().trim();
                    String imageee = image22.getText().toString().trim();
                    String imageeee = image33.getText().toString().trim();
                    String name1 = nameee.getText().toString().trim();

                    String namee3 = name.toString();
                    DormsClass productt = new DormsClass(imageee, imageee, imageeee, des, namee3);
                    productsReference = FirebaseDatabase.getInstance().getReference().child("DormsDetails");

                    productsReference.child(name1).setValue(productt)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Product added successfully
                                    Toast.makeText(AddDorms.this, "Product added to the salon", Toast.LENGTH_SHORT).show();
                                    finish(); // Finish the activity after adding the product
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Error occurred while adding the product
                                    Toast.makeText(AddDorms.this, "Failed to add the product", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        } else if (placeType.equals("StudyPlace")) {
            image11 = findViewById(R.id.image1);
            image22 = findViewById(R.id.image2);
            image33 = findViewById(R.id.image3);
            nameee = findViewById(R.id.edit_text_product_name);
            desc = findViewById(R.id.edit_text_product_description);

            addProductButton = findViewById(R.id.button_add_product);

            addProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get additional data from the additional views
                    String des = desc.getText().toString().trim();
                    String imagee = image11.getText().toString().trim();
                    String imageee = image22.getText().toString().trim();
                    String imageeee = image33.getText().toString().trim();
                    String name1 = nameee.getText().toString().trim();

                    String namee3 = name.toString();
                    DormsClass productt = new DormsClass(imageee, imageee, imageeee, des, namee3);
                    productsReference = FirebaseDatabase.getInstance().getReference().child("StudyPlaceItems");

                    productsReference.child(name1).setValue(productt)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Product added successfully
                                    Toast.makeText(AddDorms.this, "Product added to the " + placeType, Toast.LENGTH_SHORT).show();
                                    finish(); // Finish the activity after adding the product
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Error occurred while adding the product
                                    Toast.makeText(AddDorms.this, "Failed to add the product", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
    }
}