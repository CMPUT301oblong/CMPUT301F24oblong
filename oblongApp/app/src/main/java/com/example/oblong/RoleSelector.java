package com.example.oblong;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;


public class RoleSelector extends AppCompatActivity implements AddNewUserFragment.AddUserDialogListener {
    private Database db = new Database();
    private String user_id;

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

        db.getCurrentUser(user_id -> {
            if (user_id != null) {
                this.user_id = user_id;
                verifyUser();
            } else {
                Log.e("RoleSelector", "Failed to retrieve user ID");
            }
        });
    }

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
                        // Entrant, navigate to EntrantProfileScreenActivity
                        Intent intent = new Intent(this, EntrantProfileScreenActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "organizer": {
                        // Organizer, navigate to organizer_profile
                        Intent intent = new Intent(this, organizer_profile.class);
                        startActivity(intent);
                        break;
                    }
                    case "admin": {
                        // Admin, navigate to AdminProfileBrowserActivity
                        Intent intent = new Intent(this, AdminProfileBrowserActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
            } else {
                new AddNewUserFragment().show(getSupportFragmentManager(), "add_user");
            }
        });
    }

    @Override
    public void addUser(String name, String email, String phone) {
        db.addUser(user_id, name, email, "entrant", (phone.isEmpty() ? null : phone), null);
        Intent intent = new Intent(this, EntrantProfileScreenActivity.class);
        startActivity(intent);
    }
}