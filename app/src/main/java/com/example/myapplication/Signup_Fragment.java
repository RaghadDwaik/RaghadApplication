package com.example.myapplication;

import android.annotation.SuppressLint;
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
    private EditText fullName, email, mobileNumber, location,
            password, confirmPassword;
    private TextView login;
    private Button signUpButton;
    private CheckBox terms_conditions;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final int RC_SIGN_IN = 123; // Request code for sign-in
    private SignInButton googleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "Signup_Fragment";
    private RadioGroup radioGroup;
    private RadioButton radioButton1,radioButton2;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(getActivity(), "Successfully", Toast.LENGTH_SHORT)
                    .show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
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
         googleSignInButton =view.findViewById(R.id.google_sign_in_button);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioButton1=view.findViewById(R.id.radioButtonOption1);
        radioButton2= view.findViewById(R.id.radioButtonOption2);


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
         googleSignInButton.setOnClickListener(this);

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
            case R.id.google_sign_in_button:
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

                googleSignInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                });




            break;

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign-In failed, handle the error
                Log.w(TAG, "Google sign-in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign-in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Continue with your app logic
                        } else {
                            // Sign-in failed, display a message to the user
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Check Validation Method
    private void checkValidation() {
        // Get all edittext texts
        String getFullName = fullName.getText().toString();
        String getEmailId = email.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getLocation = location.getText().toString();


        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or empty
        if (getFullName.isEmpty()
                || getEmailId.isEmpty()
                || getMobileNumber.isEmpty()
                || getLocation.isEmpty()
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
        }  else if (!radioButton2.isChecked()&& !radioButton1.isChecked()) {
            new CustomToast().Show_Toast(getActivity(), view, "This field is required.");
        } else {
            // Get the selected radio button value
            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = view.findViewById(selectedRadioButtonId);
            String selectedOption = selectedRadioButton.getText().toString();

            // Determine if the registration is for an owner
            boolean isOwner = selectedOption.equals("Owner");

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
                                    userInfo.put("location", getLocation);
                                    userInfo.put("isOwner", isOwner);

                                    // Set the user information in the Firestore document
                                    userRef.set(userInfo)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Document successfully written
                                                    if (isOwner) {
                                                        // Handle owner registration (specify owner of a place) here
                                                        handleOwnerRegistration(userId);
                                                    } else {
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
    // Method to handle owner registration (specify owner of a place)
    private void handleOwnerRegistration(String ownerId) {

        // For example, you can start a new activity to add the place details and associate it with the owner
        Intent intent = new Intent(getActivity(), OwnerPlaceRegistrationActivity.class);
        intent.putExtra("ownerId", ownerId);
        startActivity(intent);
    }

    // Method to handle user registration
    private void handleUserRegistration() {
        Intent intent = new Intent(getActivity(), Profile.class);
        startActivity(intent);
    }
}

