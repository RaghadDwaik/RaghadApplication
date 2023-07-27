package com.example.myapplication;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Signup_FragmentTest {

    private Signup_Fragment signupFragment;
    private IntentWrapper intentWrapper;


    @Before
    public void setUp() {
        signupFragment = new Signup_Fragment();
        intentWrapper = new IntentWrapper(ApplicationProvider.getApplicationContext());

    }
    @After
    public void tearDown() {
        TestUtils.clearToastMessages();
        TestUtils.clearStartedActivities();
        TestUtils.clearStartedIntents();
    }

    @Test
    public void testCheckValidation_AllFieldsEmpty() {
        // Test when all fields are empty
        signupFragment.fullName.setText("");
        signupFragment.email.setText("");
        signupFragment.mobileNumber.setText("");
        signupFragment.password.setText("");
        signupFragment.confirmPassword.setText("");
        signupFragment.terms_conditions.setChecked(false);

        // Call checkValidation method
        signupFragment.checkValidation();

        // Assert that the expected toast message is displayed
        assertEquals("All fields are required.", TestUtils.getLastToastMessage());
    }

    @Test
    public void testCheckValidation_InvalidEmail() {
        // Test when the email is invalid
        signupFragment.fullName.setText("Nada Hisham");
        signupFragment.email.setText("nada.hisham.com");
        signupFragment.mobileNumber.setText("1234567890");
        signupFragment.password.setText("password");
        signupFragment.confirmPassword.setText("password");
        signupFragment.terms_conditions.setChecked(true);

        // Call checkValidation method
        signupFragment.checkValidation();

        // Assert that the expected toast message is displayed
        assertEquals("Your Email Id is Invalid.", TestUtils.getLastToastMessage());
    }

    @Test
    public void testCheckValidation_PasswordsDoNotMatch() {
        // Test when the passwords do not match
        signupFragment.fullName.setText("Nada Hisham");
        signupFragment.email.setText("nada.hisham@example.com");
        signupFragment.mobileNumber.setText("1234567890");
        signupFragment.password.setText("password");
        signupFragment.confirmPassword.setText("differentpassword");
        signupFragment.terms_conditions.setChecked(true);

        // Call checkValidation method
        signupFragment.checkValidation();

        // Assert that the expected toast message is displayed
        assertEquals("Both passwords do not match.", TestUtils.getLastToastMessage());
    }

    @Test
    public void testCheckValidation_SuccessfulUserRegistration() {
        // Test successful registration as a user
        signupFragment.fullName.setText("Nada Hisham");
        signupFragment.email.setText("nada.hisham@example.com");
        signupFragment.mobileNumber.setText("1234567890");
        signupFragment.password.setText("password");
        signupFragment.confirmPassword.setText("password");
        signupFragment.terms_conditions.setChecked(true);
        signupFragment.radioButton1.setChecked(true); // For example, user registration

        // Call checkValidation method
        signupFragment.checkValidation();

        // Assert that no toast message is displayed, which indicates successful registration
        assertNull(TestUtils.getLastToastMessage());
    }

    @Test
    public void testCheckValidation_SuccessfulOwnerRegistration() {
        // Test successful registration as an owner
        signupFragment.fullName.setText("Hisham Nada ");
        signupFragment.email.setText("hisham.nada@example.com");
        signupFragment.mobileNumber.setText("9876543210");
        signupFragment.password.setText("password");
        signupFragment.confirmPassword.setText("password");
        signupFragment.terms_conditions.setChecked(true);
        signupFragment.radioButton2.setChecked(true); // For example, owner registration

        // Call checkValidation method
        signupFragment.checkValidation();

        // Assert that no toast message is displayed, which indicates successful registration
        assertNull(TestUtils.getLastToastMessage());
    }
    @Test
    public void testCheckValidation_PasswordTooShort() {
        // Test when password is too short (less than 6 characters)
        signupFragment.fullName.setText("Nada Hisham");
        signupFragment.email.setText("nada.hisham@example.com");
        signupFragment.mobileNumber.setText("1234567890");
        signupFragment.password.setText("pass"); // Password with less than 6 characters
        signupFragment.confirmPassword.setText("pass"); // Confirm password with less than 6 characters
        signupFragment.terms_conditions.setChecked(true);
        signupFragment.radioButton1.setChecked(true); // For example, user registration

        // Call checkValidation method
        signupFragment.checkValidation();

        // Assert that the expected toast message is displayed
        assertEquals("Password must be at least 6 characters long.", TestUtils.getLastToastMessage());
    }
    @Test
    public void testCheckValidation_NoRadioButtonSelected() {
        // Test when no radio button is selected (neither "Owner" nor "User")
        signupFragment.fullName.setText("Nada Hisham");
        signupFragment.email.setText("nada.hisham@example.com");
        signupFragment.mobileNumber.setText("1234567890");
        signupFragment.password.setText("password");
        signupFragment.confirmPassword.setText("password");
        signupFragment.terms_conditions.setChecked(true);
        signupFragment.radioButton1.setChecked(false);
        signupFragment.radioButton2.setChecked(false);

        // Call checkValidation method
        signupFragment.checkValidation();

        // Assert that the expected toast message is displayed
        assertEquals(" اختر منصبك من فضلك", TestUtils.getLastToastMessage());
    }

    @Test
    public void testHandleOwnerRegistration_Successful() {
        // Test handle owner registration (specify owner of a place) - successful
        String ownerId = "testOwnerId";

        // For this test, we will just verify that the intent is created with the correct extra data
        Intent expectedIntent = new Intent(ApplicationProvider.getApplicationContext(), OwnerHomePage.class);
        expectedIntent.putExtra("ownerId", ownerId);

        // Call the handleOwnerRegistration method
        signupFragment.handleOwnerRegistration(ownerId);

        // Assert that the correct intent is created
        assertEquals(expectedIntent.getComponent(), TestUtils.getLastIntent().getComponent());

        // Assert that the correct extra data is passed
        assertEquals(ownerId, TestUtils.getLastIntent().getStringExtra("ownerId"));
    }

    @Test
    public void testHandleUserRegistration_Successful() {
        // Test handle user registration - successful

        // For this test, we will just verify that the intent is created and started
        Intent expectedIntent = new Intent(ApplicationProvider.getApplicationContext(), Profile.class);

        // Call the handleUserRegistration method
        signupFragment.handleUserRegistration();

        // Assert that the correct intent is created and started
        Intent startedIntent = TestUtils.getLastStartedActivity();
        assertNotNull(startedIntent);
        assertEquals(expectedIntent.getComponent(), startedIntent.getComponent());
    }
    //Test for Unchecked Terms and Conditions
    @Test
    public void testCheckValidation_UncheckedTermsAndConditions() {
        // Test when terms and conditions checkbox is not checked
        signupFragment.fullName.setText("Nada Hisham");
        signupFragment.email.setText("nada.hisham@example.com");
        signupFragment.mobileNumber.setText("1234567890");
        signupFragment.password.setText("password");
        signupFragment.confirmPassword.setText("password");
        signupFragment.terms_conditions.setChecked(false);
        signupFragment.radioButton1.setChecked(true); // For example, user registration

        // Call checkValidation method
        signupFragment.checkValidation();

        // Assert that the expected toast message is displayed
        String expectedToastMessage = "Please accept the Terms and Conditions.";
        assertEquals(expectedToastMessage, TestUtils.getLastToastMessage());
    }

    private static class IntentWrapper {
        private final Context context;
        private Intent createdIntent;

        IntentWrapper(Context context) {
            this.context = context;
        }

        Intent createIntent(Class<?> cls) {
            return new Intent(context, cls);
        }

        void assertIntent(Intent expectedIntent) {
            assertEquals(expectedIntent.getComponent(), createdIntent.getComponent());
            assertEquals(expectedIntent.getExtras(), createdIntent.getExtras());
        }

        void startActivity(Intent intent) {
            createdIntent = intent;
        }
    }

}

