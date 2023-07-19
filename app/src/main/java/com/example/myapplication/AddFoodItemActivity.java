package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AddFoodItemActivity extends AppCompatActivity {
    private EditText itemNameEditText;
    private EditText itemDescriptionEditText;
    private EditText itemPriceEditText;
    private Button addButton;
    private ImageView itemImageView;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item);

        itemNameEditText = findViewById(R.id.editText_item_name);
        itemDescriptionEditText = findViewById(R.id.editText_item_description);
        itemPriceEditText = findViewById(R.id.editText_item_price);
        addButton = findViewById(R.id.button_add);
        itemImageView = findViewById(R.id.imageView_food_item);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values
                String itemName = itemNameEditText.getText().toString().trim();
                String itemDescription = itemDescriptionEditText.getText().toString().trim();
                double itemPrice = Double.parseDouble(itemPriceEditText.getText().toString());

                // Create a new FoodItem object
                FoodItem newItem = new FoodItem(itemName, itemDescription, itemPrice);

                // Save the new item to a database or perform any other necessary operations
                saveFoodItem(newItem);
            }
        });

        itemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                itemImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFoodItem(final FoodItem item) {
        // Get a reference to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference foodItemsRef = database.getReference(" ");

        // Generate a unique key for the new food item
        final String itemId = foodItemsRef.push().getKey();

        // Set the food item object as a child under the generated key
        foodItemsRef.child(itemId).setValue(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Food item saved successfully
                        Toast.makeText(AddFoodItemActivity.this, "Food item added successfully", Toast.LENGTH_SHORT).show();

                        // Upload the image to Firebase Storage
                        if (selectedImageUri != null) {
                            uploadImage(selectedImageUri, itemId);
                        }

                        // Clear input fields
                        itemNameEditText.setText("");
                        itemDescriptionEditText.setText("");
                        itemPriceEditText.setText("");
                        itemImageView.setImageResource(R.drawable.default_image); // Set default image
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to save food item
                        Toast.makeText(AddFoodItemActivity.this, "Failed to add food item", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImage(Uri imageUri, final String itemId) {
        // Get a reference to the Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("foodItemImages");

        // Create a reference to the image file with the generated key
        final StorageReference imageRef = storageRef.child(itemId + ".jpg");

        // Upload the image file to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image uploaded successfully
                Toast.makeText(AddFoodItemActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to upload image
                Toast.makeText(AddFoodItemActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
