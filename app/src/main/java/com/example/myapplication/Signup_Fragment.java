package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup_Fragment extends Fragment implements OnClickListener {
    private View view;
    public EditText fullName;
    public EditText email;
    public EditText mobileNumber;
    public EditText password;
    public EditText confirmPassword;
    private TextView login;
    private Button signUpButton;
    public CheckBox terms_conditions;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RadioGroup radioGroup;
    public RadioButton radioButton1;
    public RadioButton radioButton2;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    FirebaseUser updatedUser = mAuth.getCurrentUser();
                    if (updatedUser != null && updatedUser.isEmailVerified()) {
                        // If the email is verified, navigate to the main activity
                        Toast.makeText(getActivity(), "Email verified. Logging in...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    public Signup_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_signup, container, false);
        initViews();
        setListeners();
        return view;

    }

    // Initialize all views
    private void initViews() {
        fullName = view.findViewById(R.id.fullName);
        email = view.findViewById(R.id.userEmailId);
        mobileNumber = view.findViewById(R.id.mobileNumber);
//        location = view.findViewById(R.id.location);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        signUpButton = view.findViewById(R.id.signUpBtn);
        login = view.findViewById(R.id.already_user);
        terms_conditions = view.findViewById(R.id.terms_conditions);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioButton1 = view.findViewById(R.id.radioButtonOption1);
        radioButton2 = view.findViewById(R.id.radioButtonOption2);


        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setting text selector over textviews
        try {
            @SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.textview_selector);
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:
                // Call checkValidation method
                checkValidation();
                break;

            case R.id.already_user:
                // Replace login fragment
                new Registration().replaceLoginFragment();
                break;


        }
    }


    // Check Validation Method
    public void checkValidation() {
        // Get all edittext texts
        String getFullName = fullName.getText().toString();
        String getEmailId = email.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or empty
        if (getFullName.isEmpty()
                || getEmailId.isEmpty()
                || getMobileNumber.isEmpty()
                || getPassword.isEmpty()
                || getConfirmPassword.isEmpty()) {
            new CustomToast().Show_Toast(getActivity(), view, "All fields are required.");
        }
        // Check if email id is valid
        else if (!m.find()) {
            new CustomToast().Show_Toast(getActivity(), view, "Your Email Id is Invalid.");
        }
        // Check if both passwords match
        else if (!getConfirmPassword.equals(getPassword)) {
            new CustomToast().Show_Toast(getActivity(), view, "Both passwords do not match.");
        }
        // Make sure the user has checked the Terms and Conditions checkbox
        else if (!terms_conditions.isChecked()) {
            new CustomToast().Show_Toast(getActivity(), view, "Please accept the Terms and Conditions.");
        } else if (!radioButton2.isChecked() && !radioButton1.isChecked()) {
            new CustomToast().Show_Toast(getActivity(), view, "This field is required.");
        } else {
            // Get the selected radio button value
            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = view.findViewById(selectedRadioButtonId);
            String selectedOption = selectedRadioButton.getText().toString();

            // Determine if the registration is for an owner
            boolean isOwner = selectedOption.equalsIgnoreCase("مالك");

            // Create user with email and password
            mAuth.createUserWithEmailAndPassword(getEmailId, getPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Authentication Successful.", Toast.LENGTH_SHORT)
                                        .show();
                                // Save user information to Firestore
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    // Create a new document with the user's ID
                                    DocumentReference userRef = db.collection("User").document(userId);

                                    // Create a HashMap to store the user information
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("fullName", getFullName);
                                    userInfo.put("email", getEmailId);
                                    userInfo.put("mobileNumber", getMobileNumber);
                                    userInfo.put("isUser", !isOwner);
                                    userInfo.put("id", userId);

//                                    sendEmailVerification(user);
                                    // Set the user information in the Firestore document
                                    userRef.set(userInfo)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Document successfully written
                                                    if (selectedRadioButtonId == R.id.radioButtonOption1) {
                                                        // Handle owner registration (specify owner of a place) here
                                                        handleOwnerRegistration(userId);
                                                    } else if (selectedRadioButtonId == R.id.radioButtonOption2) {
                                                        // Handle user registration here
                                                        handleUserRegistration();
                                                    }
//                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
//                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Error writing document
                                                    Toast.makeText(getActivity(), "Failed to save user information.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getActivity(), "Authentication Failed.", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
        }
    }

//    private void sendEmailVerification(FirebaseUser user) {
//        user.sendEmailVerification()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            if(user.isEmailVerified()){
//                            Log.d("SignUpActivity", "Email verification sent.");
//                            // Add code to inform the user that the verification email has been sent.
//                            startActivity(new Intent(getActivity(),MainActivity.class));
//                        } else {
//                            Log.e("SignUpActivity", "sendEmailVerification:failure", task.getException());
//                            // Add code to handle the failure to send the verification email.
//                        }
//                    }else {
//                            Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_SHORT);
//                        }
//                    }
//                });
//        signUpButton.setOnClickListener(( view) -> {
//            startActivity(new Intent(getActivity(), MainActivity.class));
//        });
//    }


    // Method to handle owner registration (specify owner of a place)
    public void handleOwnerRegistration(String ownerId) {
                // For example, you can start a new activity to add the place details and associate it with the owner
                Intent intent = new Intent(getActivity(), OwnerHomePage.class);
                intent.putExtra("ownerId", ownerId);
                startActivity(intent);
        }


        // Method to handle user registration
        public void handleUserRegistration() {


                    Intent intent = new Intent(getActivity(), Profile.class);
                    startActivity(intent);

        }


//    private void showEmailVerificationDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage("Your email address is not verified. Please check your email for the verification link.")
//                .setTitle("Email Verification")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("Resend Verification Email", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        sendVerificationEmail();
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }

//    private void sendVerificationEmail() {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(getActivity(), "Verification email sent.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getActivity(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }
}

