package com.example.oblong;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

public class EntrantProfileEditActivity extends AppCompatActivity {

    private String user_id;
    HashMap<String, Object> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_profile_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        EditText nameInput = findViewById(R.id.entrant_profile_edit_name_input);
        EditText emailInput = findViewById(R.id.entrant_profile_edit_email_input);
        EditText phoneInput = findViewById(R.id.entrant_profile_edit_phone_input);
        ImageView profilePic = findViewById(R.id.imageView);
        Button saveChangesButton = findViewById(R.id.entrant_profile_edit_save_changes_button);
        Button cancelButton = findViewById(R.id.entrant_profile_edit_cancel_button);
        // TODO: Implement notification checkbox
        Button notificationCheckbox = findViewById(R.id.entrant_profile_edit_notification_checkbox);

        // TODO: Implement image picker
        ImageView imageButton = findViewById(R.id.entrant_profile_edit_image_button);
        imageButton.setOnClickListener(v -> {
            // Handle image button click
        });

        // Pull and display all user Info here
        Database db = new Database();
        db.getCurrentUser(userId -> {
            if (userId != null) {
                user_id = userId;
                db.getUser(userId, user -> {
                    if (user != null) {
                        // Process data
                        this.user = user;
                        profilePic.setImageResource(user.get("photo") == null ? R.drawable.image_placeholder : (int) user.get("photo"));
                        nameInput.setText((CharSequence) user.get("name"));
                        emailInput.setText((CharSequence) user.get("email"));
                        phoneInput.setText((CharSequence) (user.get("phone") == null ? "" : user.get("phone")));

                        Log.d("Edit Activity", "Obtained all user info"); // print user's name to console
                    } else {
                        Log.d("Edit Activity", "User not found or an error occurred.");
                    }
                });
            }
        });

        saveChangesButton.setOnClickListener(v -> {
            // Handle save changes button click
            inputValidator validator = new inputValidator(this);
            Log.d("Edit Activity", "Entered save changes button");
            if(validator.validateInput(nameInput.getText().toString(), emailInput.getText().toString(), phoneInput.getText().toString())) {
                // Update user info into hashmap
                user.put("name", nameInput.getText().toString());
                user.put("email", emailInput.getText().toString());
                user.put("phone", phoneInput.getText().toString().isEmpty() ? null : phoneInput.getText().toString());

                db.updateDocument("users", user_id, user, success -> {
                    if(success)
                        Log.d("user", "User updated");
                    else
                        Log.d("user", "User not updated");
                });
                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });

        cancelButton.setOnClickListener(v -> {
            // Handle cancel button click
            finish();
        });

    }


}