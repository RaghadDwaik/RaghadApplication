package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;


public class Profile extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private TextView textName , title;
    private TextView textPassword;
    private TextView textEmail;
   // private TextView textDob;
    private TextView textLocation;
    private TextView textMobile;
    private Button button,button2;
    private FirebaseFirestore db;

    private BottomNavigationView bottom;

    //   private ProgressBar progressBar;
    private String Name,Password,Email,Dob,Location,Mobile;
    private ImageView imageView;
    private FirebaseAuth authFirebase;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize the views
        textName = findViewById(R.id.textName);
        textPassword = findViewById(R.id.textPassword);
        textEmail = findViewById(R.id.textEmail);
        //  textDob = findViewById(R.id.textDob);
        textLocation = findViewById(R.id.textLocation);
        textMobile = findViewById(R.id.textMobile);
        title = findViewById(R.id.title);
       // button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);

        bottom = findViewById(R.id.bottom);
        bottom.setItemIconTintList(null);

        bottom.setOnNavigationItemSelectedListener(this);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in1;
//                in1 = new Intent(Profile.this, Update.class);
//                startActivity(in1);
//            }
//        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent in2;
                in2 = new Intent(Profile.this, Registration.class);
                startActivity(in2);
                finish();
            }
        });

//        progressBar =findViewById(R.id.progressBarr);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        // Get the currently logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Retrieve the user's ID
            String userId = currentUser.getUid();
            // Retrieve the user's document from Firestore
            db.collection("User")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Get the user's data from the document
                                String fullName = document.getString("fullName");
                                String email = document.getString("email");
                                String mobileNumber = document.getString("mobileNumber");
                                String location = document.getString("location");

                                // Set the user's data in the respective TextViews
                                textName.setText(fullName);
                                textEmail.setText(email);
                                textMobile.setText(mobileNumber);
                                textLocation.setText(location);
                            } else {
                                // Document doesn't exist
                                Toast.makeText(Profile.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Error retrieving user data
                            Toast.makeText(Profile.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(Profile.this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
//                        }else {
//                            // Error retrieving user data
//                            // Handle the error gracefully
//                            Toast.makeText(this, "Something Wrong With DataBase filling", Toast.LENGTH_SHORT).show();
//
//                        }


        authFirebase = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authFirebase.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "Something Wrong No user logged in", Toast.LENGTH_SHORT).show();
        } else {
            //  progressBar.setVisibility(View.VISIBLE);
            showProfile(firebaseUser);
        }

    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("User").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserDetails details = snapshot.getValue(UserDetails.class);
                    if (details != null) {

                        String name = firebaseUser.getDisplayName();
                        String email = firebaseUser.getEmail();
                     //   String password = details.getPassword();
                        String location = details.getLocation();
                        String mobile = details.getMobileNumber();

                        textName.setText(name);
                        textEmail.setText(email);
//                        textPassword.setText(password);
                        textLocation.setText(location);
                        textMobile.setText(mobile);
                        title.setText(name);

//                        Name = firebaseUser.getDisplayName();
//                        Email = firebaseUser.getEmail();
//                        //     Dob = details.getDob();
////                    Name=details.getName();
////                    Email=details.getEmail();
//                        Password = details.getPassword();
//                        Location = details.getLocation();
//                        Mobile = details.getMobileNumber();
//
//
//                        textName.setText(Name);
//                        textEmail.setText(Email);
//                        // textDob.setText(Dob);
//                        textPassword.setText(Password);
//                        textLocation.setText(Location);
//                        textMobile.setText(Mobile);
//                        textView.setText(Name);

                    }
                    //   progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Somthing Wrong...!", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.GONE);
            }
        });
    }



    public TextView getTextName() {
        return textName;
    }

    public void setTextName(TextView textName) {
        this.textName = textName;
    }

    public TextView getTextPassword() {
        return textPassword;
    }

    public void setTextPassword(TextView textPassword) {
        this.textPassword = textPassword;
    }

    public TextView getTextEmail() {
        return textEmail;
    }

    public void setTextEmail(TextView textEmail) {
        this.textEmail = textEmail;
    }

    public void editName(View view) {
        TextView textName = findViewById(R.id.textName);
        String currentName = textName.getText().toString();

        // Launch an edit dialog or activity to modify the name
        // For example, you can use an AlertDialog or start a new activity with an EditText field
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Name");

        final EditText editName = new EditText(this);
        editName.setText(currentName);

        builder.setView(editName);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editName.getText().toString();
                textName.setText(newName);

                // Save the changes to the profile data
                // Add your code here to update the name in your data storage (e.g., a database)
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("User").document(userId);
                    userRef.update("fullName", newName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Profile.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Profile.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void editPassword(View view) {
    }

    public void editEmail(View view) {
        TextView textEmail = findViewById(R.id.textEmail);
        String currentEmail = textEmail.getText().toString();

        // Launch an edit dialog or activity to modify the email
        // For example, you can use an AlertDialog or start a new activity with an EditText field
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Email");

        final EditText editEmail = new EditText(this);
        editEmail.setText(currentEmail);

        builder.setView(editEmail);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newEmail = editEmail.getText().toString();
                textEmail.setText(newEmail);

                // Save the changes to the profile data
                // Add your code here to update the email in your data storage (e.g., a database)

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("User").document(userId);
                    userRef.update("email", newEmail)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Profile.this, "email updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Profile.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void editDOB(View view) {
    }

    public void editLocation(View view) {
        TextView textLocation = findViewById(R.id.textLocation);
        String currentLocation = textLocation.getText().toString();

        // Launch an edit dialog or activity to modify the location
        // For example, you can use an AlertDialog or start a new activity with an EditText field
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Location");

        final EditText editLocation = new EditText(this);
        editLocation.setText(currentLocation);

        builder.setView(editLocation);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newLocation = editLocation.getText().toString();
                textLocation.setText(newLocation);

                // Save the changes to the profile data
                // Add your code here to update the location in your data storage (e.g., a database)
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("User").document(userId);
                    userRef.update("location", newLocation)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Profile.this, "location updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Profile.this, "Failed to update location", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void editMobileNumber(View view) {
        TextView textMobile = findViewById(R.id.textMobile);
        String currentMobileNumber = textMobile.getText().toString();

        // Launch an edit dialog or activity to modify the mobile number
        // For example, you can use an AlertDialog or start a new activity with an EditText field
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Mobile Number");

        final EditText editMobileNumber = new EditText(this);
        editMobileNumber.setText(currentMobileNumber);

        builder.setView(editMobileNumber);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newMobileNumber = editMobileNumber.getText().toString();
                textMobile.setText(newMobileNumber);

                // Save the changes to the profile data
                // Add your code here to update the mobile number in your data storage (e.g., a database)
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("User").document(userId);
                    userRef.update("mobileNumber", newMobileNumber)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Profile.this, "mobileNumber updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Profile.this, "Failed to update mobileNumber", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

//    public TextView getTextDob() {
//        return textDob;
//    }
//
//    public void setTextDob(TextView textDob) {
//        this.textDob = textDob;
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                Intent in = new Intent(this, MainActivity.class);
                startActivity(in);
                return true;
            case R.id.map:
                Intent in1 = new Intent(this, Map.class);
                startActivity(in1);
                return true;

            case R.id.favorite:
                Intent in5 = new Intent(this, FavouriteList.class);
                startActivity(in5);
                return true;

            case R.id.Recently:
                Intent in4 = new Intent(this, RecentlyView.class);
                startActivity(in4);
                return true;
            case R.id.profile:
                Intent in2 = new Intent(this, Profile.class);
                startActivity(in2);
                return true;
        }
        return false;
    }

}

