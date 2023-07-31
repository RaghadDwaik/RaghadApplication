package com.example.myapplication;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ServiceDetails extends AppCompatActivity {

    private ImageView serviceImageView;

    private TextView serviceNameTextView;
    private TextView servicePriceTextView;
    private TextView serviceDescriptionTextView;
    private ImageButton favorite, contactButton;
    private FirebaseFirestore db;
    String mobileNumber;
    double salonPrice;
    private CollectionReference favoritesRef;
    String salonName;
    String placee;
    String place1;
    String salonId;
    String salonImageUrl, salonDesc;

    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);


        contactButton = findViewById(R.id.contact);


        favorite = findViewById(R.id.favoriteList);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is authenticated, continue with accessing user information
            String uid = user.getUid();
            favoritesRef = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(uid)
                    .collection("favorite");
            // Rest of your code
        } else {

            Intent intent = new Intent(this, Registration.class);
            startActivity(intent);
            finish(); // Optional: Close the current activity to prevent the user from navigating back

            // User is not authenticated, handle this case accordingly
            Toast.makeText(this, "Please log in to access this feature.", Toast.LENGTH_SHORT).show();

        }


        serviceImageView = findViewById(R.id.productImage);
        serviceNameTextView = findViewById(R.id.productName);
        servicePriceTextView = findViewById(R.id.productPrice);
        serviceDescriptionTextView = findViewById(R.id.productDesc);

        Intent intent = getIntent();
        placee = intent.getStringExtra("place");
        place1 = intent.getStringExtra("place1");


        salonId = intent.getStringExtra("id");
        salonName = intent.getStringExtra("name");
        salonPrice = intent.getDoubleExtra("price", 0);
        salonDesc = intent.getStringExtra("desc");
        salonImageUrl = intent.getStringExtra("image");


        serviceNameTextView.setText(salonName);
        servicePriceTextView.setText(String.valueOf(salonPrice));
        serviceDescriptionTextView.setText(salonDesc);

        // Check if the item is already in favorites and update the button color
        favoritesRef.document(salonId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                isFavorite = true;
                favorite.setColorFilter(Color.RED);
            } else {
                isFavorite = false;
                favorite.setColorFilter(Color.WHITE);
            }
        }).addOnFailureListener(e -> {
            // Failed to retrieve favorite status
        });

        favorite.setOnClickListener(v -> {
            if (isFavorite) {
                removeFromFavorites(salonId);
                favorite.setColorFilter(Color.WHITE);
                Toast.makeText(ServiceDetails.this, "ازالة من المفضلة", Toast.LENGTH_SHORT).show();
            } else {
                ServicesClass newItem = new ServicesClass(salonId, salonName, salonImageUrl, salonPrice);
                addToFavorites(newItem);
                favorite.setColorFilter(Color.RED);
                Toast.makeText(ServiceDetails.this, "اضافة الى المفضلة", Toast.LENGTH_SHORT).show();
            }
            isFavorite = !isFavorite; // Toggle the favorite state
        });

        Glide.with(this)
                .load(salonImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(serviceImageView);


        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference placesCollectionRef = firestore.collection("Places");

        String enteredPlace = place1; // Replace this with the entered place name or ID

        placesCollectionRef.whereEqualTo("name", enteredPlace) // Replace "placeName" with the field containing the place name in "Places" collection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();
                        for (DocumentSnapshot document : querySnapshot) {
                            String ownerId = document.getString("ownerId");
                            if (ownerId != null) {
                                CollectionReference ownerCollectionRef = firestore.collection("User");
                                DocumentReference ownerDocumentRef = ownerCollectionRef.document(ownerId);

                                ownerDocumentRef.get().addOnCompleteListener(ownerTask -> {
                                    if (ownerTask.isSuccessful()) {

                                        DocumentSnapshot ownerDocument = ownerTask.getResult();
                                        if (ownerDocument.exists()) {
                                            mobileNumber = ownerDocument.getString("mobileNumber");


                                        } else {
                                            Log.d("Firestore", "Owner document not found for ownerId: " + ownerId);
                                        }
                                    } else {
                                        Log.e("Firestore", "Error getting owner document: ", ownerTask.getException());
                                    }
                                });
                            } else {
                                Log.d("Firestore", "Owner ID not found for place: " + enteredPlace);
                            }
                        }
                    } else {
                        Log.e("Firestore", "Error getting place document: ", task.getException());
                    }
                });


        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNumber = "972" + mobileNumber.substring(1);
                Uri uri = Uri.parse("https://wa.me/" + mobileNumber);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });


        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v ->
                deleteService(salonId));


        Button editButton = findViewById(R.id.edit);

        editButton.setOnClickListener(v -> {
            if ("salon".equals(placee)) {
                EditService(salonId);
            } else if ("dryclean".equals(placee)) {
                EditService1(salonId);
            } else if ("resturant".equals(placee)) {
                EditService2(salonId);
            } else if ("supermarket".equals(placee)) {
                EditService3(salonName);
            }
        });



    }

    private void addToFavorites(ServicesClass item) {
        favoritesRef.document(item.getId()).set(item)
                .addOnSuccessListener(aVoid -> {

                })
                .addOnFailureListener(e -> {
                    // Error occurred while adding the item to favorites
                });
    }

    private void removeFromFavorites(String itemId) {
        favoritesRef.document(itemId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Item removed from favorites successfully
                })
                .addOnFailureListener(e -> {
                    // Error occurred while removing the item from favorites
                });
    }

    private void deleteService(String placeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("هل أنت متأكد من أنك تريد حذف هذه الخدمة او المنتج؟")
                .setPositiveButton("نعم", (dialog, which) -> {

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    // Example: Delete from "salon" node
                    DatabaseReference salonRef = database.getReference("salonServices").child(salonName);
                    salonRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ServiceDetails.this, "Product Or Service Deleted Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Handle the failure to delete from "salon" node
                                e.printStackTrace();
                            });

                    // Example: Delete from "other_node" node
                    DatabaseReference sRef = database.getReference("SuperMarketItems").child(salonName);
                    sRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ServiceDetails.this, "Product Or Service Deleted Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Handle the failure to delete from "other_node" node
                                e.printStackTrace();
                            });

                    DatabaseReference resRef = database.getReference("Items").child(salonName);
                    resRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ServiceDetails.this, "Product Or Service Deleted Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Handle the failure to delete from "other_node" node
                                e.printStackTrace();
                            });


                    DatabaseReference dryRef = database.getReference("DryCleanServices").child(salonName);
                    resRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ServiceDetails.this, "لقد تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Handle the failure to delete from "other_node" node
                                e.printStackTrace();
                            });


                })
                .setNegativeButton("لا", (dialog, which) -> {
                    // User clicked "لا", do nothing
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void EditService(String placeId) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        EditText editTextName = dialogView.findViewById(R.id.Name);
        EditText editTextImage = dialogView.findViewById(R.id.Image);
        EditText editTextprice = dialogView.findViewById(R.id.Price);
        EditText editTextdesc = dialogView.findViewById(R.id.description);

        editTextName.setText(salonName);
        editTextImage.setText(salonImageUrl);
        String p = String.valueOf(salonPrice);
        editTextprice.setText(p);
        editTextdesc.setText(salonDesc);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("تعديل المكان")
                .setPositiveButton("حفظ", (dialog, which) -> {
                    String newName = editTextName.getText().toString().trim();
                    String newImage = editTextImage.getText().toString().trim();
                    double newPrice = Double.parseDouble(editTextprice.getText().toString().trim());
                    String newDesc = editTextdesc.getText().toString().trim();

                    salonName = newName;
                    salonImageUrl = newImage;
                    salonPrice = newPrice;
                    salonDesc = newDesc;

                    // Update the UI with the new values
                    serviceNameTextView.setText(salonName);
                    servicePriceTextView.setText(String.valueOf(salonPrice));
                    serviceDescriptionTextView.setText(salonDesc);
                    Glide.with(this).load(salonImageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(serviceImageView);


                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference salonRef = database.getReference("salonServices").child(salonName);

                            salonRef.child("name").setValue(newName);
                            salonRef.child("image").setValue(newImage);
                            salonRef.child("price").setValue(newPrice);
                            salonRef.child("description").setValue(newDesc);




                })
                .setNegativeButton("تخطي", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void EditService1(String placeId) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        EditText editTextName = dialogView.findViewById(R.id.Name);
        EditText editTextImage = dialogView.findViewById(R.id.Image);
        EditText editTextprice = dialogView.findViewById(R.id.Price);
        EditText editTextdesc = dialogView.findViewById(R.id.description);

        editTextName.setText(salonName);
        editTextImage.setText(salonImageUrl);
        String p = String.valueOf(salonPrice);
        editTextprice.setText(p);
        editTextdesc.setText(salonDesc);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("تعديل المكان")
                .setPositiveButton("حفظ", (dialog, which) -> {
                    String newName = editTextName.getText().toString().trim();
                    String newImage = editTextImage.getText().toString().trim();
                    double newPrice = Double.parseDouble(editTextprice.getText().toString().trim());
                    String newDesc = editTextdesc.getText().toString().trim();

                    salonName = newName;
                    salonImageUrl = newImage;
                    salonPrice = newPrice;
                    salonDesc = newDesc;

                    // Update the UI with the new values
                    serviceNameTextView.setText(salonName);
                    servicePriceTextView.setText(String.valueOf(salonPrice));
                    serviceDescriptionTextView.setText(salonDesc);
                    Glide.with(this).load(salonImageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(serviceImageView);


                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference salonRef = database.getReference("DryCleanServices").child(salonName);

                    salonRef.child("name").setValue(newName);
                    salonRef.child("image").setValue(newImage);
                    salonRef.child("price").setValue(newPrice);
                    salonRef.child("description").setValue(newDesc);




                })
                .setNegativeButton("تخطي", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void EditService2(String placeId) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        EditText editTextName = dialogView.findViewById(R.id.Name);
        EditText editTextImage = dialogView.findViewById(R.id.Image);
        EditText editTextprice = dialogView.findViewById(R.id.Price);
        EditText editTextdesc = dialogView.findViewById(R.id.description);

        editTextName.setText(salonName);
        editTextImage.setText(salonImageUrl);
        String p = String.valueOf(salonPrice);
        editTextprice.setText(p);
        editTextdesc.setText(salonDesc);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("تعديل المكان")
                .setPositiveButton("حفظ", (dialog, which) -> {
                    String newName = editTextName.getText().toString().trim();
                    String newImage = editTextImage.getText().toString().trim();
                    double newPrice = Double.parseDouble(editTextprice.getText().toString().trim());
                    String newDesc = editTextdesc.getText().toString().trim();

                    salonName = newName;
                    salonImageUrl = newImage;
                    salonPrice = newPrice;
                    salonDesc = newDesc;

                    // Update the UI with the new values
                    serviceNameTextView.setText(salonName);
                    servicePriceTextView.setText(String.valueOf(salonPrice));
                    serviceDescriptionTextView.setText(salonDesc);
                    Glide.with(this).load(salonImageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(serviceImageView);


                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference salonRef = database.getReference("Items").child(salonName);

                    salonRef.child("name").setValue(newName);
                    salonRef.child("image").setValue(newImage);
                    salonRef.child("price").setValue(newPrice);
                    salonRef.child("description").setValue(newDesc);




                })
                .setNegativeButton("تخطي", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void EditService3(String placeId) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        EditText editTextName = dialogView.findViewById(R.id.Name);
        EditText editTextImage = dialogView.findViewById(R.id.Image);
        EditText editTextprice = dialogView.findViewById(R.id.Price);
        EditText editTextdesc = dialogView.findViewById(R.id.description);

        editTextName.setText(salonName);
        editTextImage.setText(salonImageUrl);
        String p = String.valueOf(salonPrice);
        editTextprice.setText(p);
        editTextdesc.setText(salonDesc);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("تعديل المكان")
                .setPositiveButton("حفظ", (dialog, which) -> {
                    String newName = editTextName.getText().toString().trim();
                    String newImage = editTextImage.getText().toString().trim();
                    double newPrice = Double.parseDouble(editTextprice.getText().toString().trim());
                    String newDesc = editTextdesc.getText().toString().trim();

                    salonName = newName;
                    salonImageUrl = newImage;
                    salonPrice = newPrice;
                    salonDesc = newDesc;

                    // Update the UI with the new values
                    serviceNameTextView.setText(salonName);
                    servicePriceTextView.setText(String.valueOf(salonPrice));
                    serviceDescriptionTextView.setText(salonDesc);
                    Glide.with(this).load(salonImageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(serviceImageView);


                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference salonRef = database.getReference("SuperMarketItems").child(salonName);

                    salonRef.child("name").setValue(newName);
                    salonRef.child("image").setValue(newImage);
                    salonRef.child("price").setValue(newPrice);
                    salonRef.child("description").setValue(newDesc);




                })
                .setNegativeButton("تخطي", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}