package com.example.oblong;

import static com.example.oblong.imageUtils.bitmapToBase64;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.admin.AdminBaseActivity;
import com.example.oblong.entrant.EntrantBaseActivity;
import com.example.oblong.organizer.organizer_base_activity;


/**
 * {@code RoleSelector} This class handles the role selection screen which is the first screen a user will see
 * upon opening the app
 */
public class RoleSelector extends AppCompatActivity {

    private Database db = new Database();
    private String user_id;

    /**
     * The {@code onCreate} method populates the screen and UI elements
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_role_selector);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView greeting = findViewById(R.id.greeting);

        Spannable text1 = new SpannableString("Hello,\n");
        text1.setSpan(new ForegroundColorSpan(getColor(R.color.accent)), 0, text1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        greeting.setText(text1);

        Spannable text2 = new SpannableString("You!");
        text2.setSpan(new ForegroundColorSpan(getColor(R.color.white)), 0, text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        greeting.append(text2);

        TextView introduction = findViewById(R.id.introduction);

        Spannable text3 = new SpannableString("It must be your first time logging on!\nLet's get you started ");
        text3.setSpan(new ForegroundColorSpan(getColor(R.color.white)), 0, text3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        introduction.setText(text3);

        Spannable text4 = new SpannableString("ASAP!");
        text4.setSpan(new ForegroundColorSpan(getColor(R.color.accent)), 0, text4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        introduction.append(text4);

        //this code required for Android System Notifications to appear (API >= 33)
        //User MUST ALLOW for system notifications to appear
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        Database.getCurrentUser(user_id -> {
            if (user_id != null) {
                this.user_id = user_id;
                verifyUser();
            } else {
                Log.e("RoleSelector", "Failed to retrieve user ID");
            }
        });
    }

    /**
     * The {@code verifyUser} method navigates the user based on their type to their respective screens
     */
    private void verifyUser() {
        // Verify user exists in database
        db.getUser(user_id, user -> {
            if (user != null) {
                Log.d("user", "User Name: " + user.get("name")); // print user's name to console
                // User exists, check their type
                String type = (String) user.get("type");
                assert type != null;
                switch (type) {
                    case "entrant": {
                        // Entrant, navigate to EntrantProfileScreenFragment
                        Intent intent = new Intent(this, EntrantBaseActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "organizer": {
                        // Organizer, navigate to organizer_profile_fragment
                        Intent intent = new Intent(this, organizer_base_activity.class);
                        startActivity(intent);
                        break;
                    }
                    case "admin": {
                        // Admin, navigate to AdminBaseActivity
                        Intent intent = new Intent(this, AdminBaseActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
                finish(); // Close this activity
            } else {
                handleNewUser();
            }
        });
    }

    private void handleNewUser() {
        Button ContinueButton = findViewById(R.id.continue_button);
        EditText name = findViewById(R.id.userName);
        EditText email = findViewById(R.id.userEmail);
        EditText phone = findViewById(R.id.userPhone);
        ContinueButton.setOnClickListener(v -> {
            inputValidator validator = new inputValidator(this);
            if (validator.validateUserProfile(name.getText().toString(), email.getText().toString(), phone.getText().toString())) {
                this.addUser(name.getText().toString(), email.getText().toString(), phone.getText().toString());
            }

        });
    }

    /**
     * The {@code addUser} method adds a user to the database as an entrant
     * @param name
     * @param email
     * @param phone
     */
    public void addUser(String name, String email, String phone) {
        Bitmap profilePicture = ProfilePicGenerator.generateProfilePic(name);

        // Convert the Bitmap to a Base64 string
        String profilePictureBase64 = bitmapToBase64(profilePicture);
        db.addUser(user_id, name, email, "entrant", (phone.isEmpty() ? null : phone), profilePictureBase64);
        db.addEntrant(user_id, false, false, user_id);
        Intent intent = new Intent(this, EntrantBaseActivity.class);
        startActivity(intent);
        finish(); // Close this activity
    }
}