package com.example.oblong;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EntrantProfileScreenActivity extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView phone;
    private ImageView profilePic;
    private Database db;

    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // The edit activity finished successfully, update UI
                    fetchUserProfileData();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_profile_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.entrant_profile_name);
        email = findViewById(R.id.entrant_profile_email);
        phone = findViewById(R.id.entrant_profile_phone);
        profilePic = findViewById(R.id.entrant_profile_picture);
        ImageButton editProfileButton = findViewById(R.id.entrant_profile_edit_button);

        // Pull and display all user Info here
        db = new Database();
        fetchUserProfileData();

        editProfileButton.setOnClickListener(v -> {
            // Handle edit profile button click
            Intent intent = new Intent(this, EntrantProfileEditActivity.class);
            editProfileLauncher.launch(intent);
        });
    }

    private void fetchUserProfileData() {
        db.getCurrentUser(userId -> {
            if (userId != null) {
                db.getUser(userId, user -> {
                    if (user != null) {
                        // Update the UI with user data
                        profilePic.setImageResource(user.get("photo") == null ? R.drawable.image_placeholder : (int) user.get("photo"));
                        name.setText((CharSequence) user.get("name"));
                        email.setText((CharSequence) user.get("email"));
                        phone.setText((CharSequence) (user.get("phone") == null ? "No Phone # Provided" : user.get("phone")));

                        Log.d("user", "User Name: " + user.get("name"));
                    } else {
                        Log.d("user", "User not found or an error occurred.");
                    }
                });
            }
        });
    }

}