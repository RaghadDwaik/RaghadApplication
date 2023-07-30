package com.example.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class OwnerAddService extends AppCompatActivity {

    private EditText productNameEditText, nameee;
    private EditText productDescriptionEditText, desc;
    private EditText price;
    private EditText image, image11, image22, image33;
    private StorageReference storageReference;
    private Uri selectedImageUri;
    private static final int GALLERY_REQUEST_CODE = 123;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;


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
        boolean visited = getIntent().getBooleanExtra("visited", false);


        storageReference = FirebaseStorage.getInstance().getReference();

        if (placeType.equals("dorms")|| placeType.equals("studyplace")) {
            Intent in = new Intent(OwnerAddService.this, AddDorms.class);
            in.putExtra("placeType", placeType);

            in.putExtra("name", name);

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


                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery();
                    }
                });


                if (!TextUtils.isEmpty(productName) && !TextUtils.isEmpty(productDescription)) {
                    if (placeType != null) {
                        if (placeType.equals("salon")) {


                            image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    openGallery();

                                    showPhotoSelectionDialog();
                                }
                            });

                            ServicesClass product = new ServicesClass();
                            product.setName(productName);
                            product.setImage(image1);
                            product.setPrice(pricee);
                            product.setDescription(productDescription);
                            product.setSalon(name);

                            StorageReference imageRef = storageReference.child("images/" + productName + ".jpg");
                            imageRef.putFile(selectedImageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Image upload successful
                                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri downloadUri) {
                                                    // Get the download URL for the uploaded image
                                                    product.setImage(downloadUri.toString());
                                                    saveProductToDatabase(product);
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Image upload failed
                                            Toast.makeText(OwnerAddService.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                                        }
                                    });




                            productsReference = FirebaseDatabase.getInstance().getReference().child("salonServices"); // Update the DatabaseReference

                            productsReference.child(productName).setValue(product)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Product added successfully
                                            Toast.makeText(OwnerAddService.this, "تمت الاضافة بنجاح", Toast.LENGTH_SHORT).show();
                                            finish(); // Finish the activity after adding the product
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error occurred while adding the product
                                            Toast.makeText(OwnerAddService.this, "لم تتم عملية الاضافة", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        else if (placeType.equals("supermarket")) {
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
                                            Toast.makeText(OwnerAddService.this, "تمت الاضافة بنجاح ", Toast.LENGTH_SHORT).show();
                                            finish(); // Finish the activity after adding the product
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error occurred while adding the product
                                            Toast.makeText(OwnerAddService.this, "لم تتم عملية الاضافة", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(OwnerAddService.this, "تمت الاضافة بنجاح ", Toast.LENGTH_SHORT).show();
                                            finish(); // Finish the activity after adding the product
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error occurred while adding the product
                                            Toast.makeText(OwnerAddService.this, "لم تتم عملية الاضافة", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else if (placeType.equals("dryclean")) {
                            ServicesClass product = new ServicesClass();
                            product.setName(productName);
                            product.setImage(image1);
                            product.setPrice(pricee);
                            product.setDescription(productDescription);

                            product.setdryclean(name);

                            productsReference = FirebaseDatabase.getInstance().getReference().child("DryCleanServices");

                            productsReference.child(productName).setValue(product)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Product added successfully
                                            Toast.makeText(OwnerAddService.this, "تمت الاضافة بنجاح ", Toast.LENGTH_SHORT).show();
                                            finish(); // Finish the activity after adding the product
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error occurred while adding the product
                                            Toast.makeText(OwnerAddService.this, "لم تتم عملية الاضافة", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }
                }
            }
        });


    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            image.setText(selectedImageUri.toString());
        }
    }

    private void saveProductToDatabase(ServicesClass product) {
        productsReference = FirebaseDatabase.getInstance().getReference().child("salonServices"); // Update the DatabaseReference

        productsReference.child(productName).setValue(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Product added successfully
                        Toast.makeText(OwnerAddService.this, "تمت الاضافة بنجاح", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity after adding the product
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while adding the product
                        Toast.makeText(OwnerAddService.this, "لم تتم عملية الاضافة", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Photo Selection Dialog
    private void showPhotoSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر صورة");
        String[] options = {"اختار من الصور على الجهاز", "التقط صورة"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Open Gallery
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
                        break;
                    case 1:
                        // Open Camera
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        } else {
                            Toast.makeText(getApplicationContext(), "Camera not available", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
        builder.show();
    }
}
