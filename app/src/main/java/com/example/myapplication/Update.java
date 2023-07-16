package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Update extends AppCompatActivity {
    private EditText textName;
    private EditText textPassword;
    private RadioGroup RadioGendrr;
    private RadioButton Selector;
    private EditText textDob;
    private EditText textMobile;
    private TextView textLocation;


    //   private ProgressBar progressBar;
    private String Name;
    private String Password;
    private String gender;
    private String Dob;
    private String mobile;
    private String location;
    private ImageView imageView;
    private FirebaseAuth authFirebase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        textName = findViewById(R.id.EditName);
        textPassword = findViewById(R.id.EditPassword);
//        textDob = findViewById(R.id.EditDob);
        textMobile = findViewById(R.id.EditMobile);
//        RadioGendrr = findViewById(R.id.radio_button);


        textLocation = findViewById(R.id.textLocation);
        authFirebase = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authFirebase.getCurrentUser();
        showProfile(firebaseUser);
        Button button = findViewById(R.id.EditEmail);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Update.this, UpdateEmail.class);
                startActivity(intent);
                finish();
            }
        });
        Button Save = findViewById(R.id.Save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                Name = intent.getStringExtra("Name");
                Password = intent.getStringExtra("Pass");
                gender = intent.getStringExtra("Gender");
                Dob = intent.getStringExtra("desc");
                mobile = intent.getStringExtra("mobile");
                location = intent.getStringExtra("Location");


                textName.setText(Name);
                textPassword.setText(Password);
                textDob.setText(Dob);
                textMobile.setText(mobile);
                textLocation.setText(location);
                intent = new Intent(Update.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });


        textDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] dobParts = Dob.split("/");
                int day = Integer.parseInt(dobParts[0]);
                int month = Integer.parseInt(dobParts[1]) - 1;
                int year = Integer.parseInt(dobParts[2]);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Update.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int monthOfYear, int dayOfMonth) {
                        // Handle the selected date
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + selectedYear;
                        textDob.setText(selectedDate);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

//        textDob.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String[] AsDob= Dob.split("/");
//
//                int day = Integer.parseInt(AsDob[0]);
//                int month = Integer.parseInt(AsDob[1])-1;
//                int year = Integer.parseInt(AsDob[2]);
////                DatePickerDialog pickerDialog;
////                pickerDialog= new DatePickerDialog(Update.this,{
////
////                });
//            }
//        });
//
//    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference(" User");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails details= snapshot.getValue((UserDetails.class));
                if (details != null){
                    //    private String Name,Password,,Dob,gender;
                    Name=firebaseUser.getDisplayName();
                    //Email=firebaseUser.getEmail();
                    //  Dob=details.getDob();
                    Password=details.getPassword();
                  //  gender=details.getGender();
                    mobile=details.getMobileNumber();
                    // Location=details.location;

                    // Check if dob is null before assigning it to Dob variable
//                    if (details.getDob() != null) {
//                        Dob = details.getDob();
//                        String[] AsDob = Dob.split("/");
//                        int day = Integer.parseInt(AsDob[0]);
//                        int month = Integer.parseInt(AsDob[1]) - 1;
//                        int year = Integer.parseInt(AsDob[2]);
                        // Perform further actions with the day, month, and year variables
               //     }


                    textName.setText(Name);
                    textPassword.setText(Password);
                    //    textDob.setText(Dob);
                    textMobile.setText(mobile);
                    // the gender
//                    if (RadioGendrr.get) {
//                        return;
//                    }


                }
                else {
                    Toast.makeText(Update.this, "Somthing Wrong...!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Update.this, "Somthing Wrong...!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}