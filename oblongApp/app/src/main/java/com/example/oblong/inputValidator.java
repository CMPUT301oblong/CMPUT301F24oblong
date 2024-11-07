package com.example.oblong;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

public class inputValidator {
    private Context context;
    public inputValidator(Context context) {
        this.context = context;
    }

    public boolean validateUserProfile(String name, String email, String phone) {
        if (name.isEmpty() || email.isEmpty()){
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate that two names are provided and they are valid
        String[] names = name.split(" ");
        if (names.length < 2 || !name.matches("[a-zA-Z ]+")) {
            Toast.makeText(context, "Please enter a valid first and last name", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate optional phone format (example: checking for 10-digit numbers) or it is empty
        if (!phone.isEmpty() && !phone.matches("\\d{10}")) {
            Toast.makeText(context, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean validateFacilityProfile(String name, String email, String phone) {
        if (name.isEmpty() || email.isEmpty()){
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate that two names are provided and they are valid
        String[] names = name.split(" ");
        if (names.length < 1) {
            Toast.makeText(context, "Please enter a valid facility name", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate optional phone format (example: checking for 10-digit numbers) or it is empty
        if (!phone.isEmpty() && !phone.matches("\\d{10}")) {
            Toast.makeText(context, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
