package com.example.oblong;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EntrantProfileScreenActivity extends AppCompatActivity {

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

        TextView name = findViewById(R.id.entrant_profile_name);
        TextView email = findViewById(R.id.entrant_profile_email);
        TextView phone = findViewById(R.id.entrant_profile_phone);
        ImageView profilePic = findViewById(R.id.entrant_profile_picture);
        ImageButton editProfileButton = findViewById(R.id.entrant_profile_edit_button);

        // Pull and display all user Info here
        Database db = new Database();
        db.getCurrentUser(userId -> {
            if (userId != null) {
                db.getUser(userId, user -> {
                    if (user != null) {
                        // Process data
                        profilePic.setImageResource(user.get("photo") == null ? R.drawable.image_placeholder : (int) user.get("photo"));
                        name.setText((CharSequence) user.get("name"));
                        email.setText((CharSequence) user.get("email"));
                        phone.setText((CharSequence) (user.get("phone") == null ? "No Phone # Provided" : user.get("phone")));

                        Log.d("user", "User Name: " + user.get("name")); // print user's name to console
                    } else {
                        Log.d("user", "User not found or an error occurred.");
                    }
                });
            }
        });

        editProfileButton.setOnClickListener(v -> {
            // Handle edit profile button click
            Intent intent = new Intent(this, EntrantProfileEditActivity.class);
            startActivity(intent);
        });
    }
}