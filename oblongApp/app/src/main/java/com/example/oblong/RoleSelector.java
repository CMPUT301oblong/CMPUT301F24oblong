package com.example.oblong;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
public class RoleSelector extends AppCompatActivity implements AddNewUserDialog.AddUserDialogListener {
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
            } else {
                new AddNewUserDialog().show(getSupportFragmentManager(), "add_user");
            }
        });
    }

    /**
     * The {@code addUser} method adds a user to the database as an entrant
     * @param name
     * @param email
     * @param phone
     */
    @Override
    public void addUser(String name, String email, String phone) {
        db.addUser(user_id, name, email, "entrant", (phone.isEmpty() ? null : phone), null);
        db.addEntrant(user_id, false, false, user_id);
        Intent intent = new Intent(this, EntrantBaseActivity.class);
        startActivity(intent);
    }
}