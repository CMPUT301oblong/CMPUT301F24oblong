package com.example.oblong.entrant;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.oblong.Database;
import com.example.oblong.R;
import com.example.oblong.imageUtils;
import com.example.oblong.inputValidator;

import java.io.IOException;

import java.util.HashMap;

/**
 * Activity class for editing a user profile.
 * <p>
 * This activity allows the user to update their profile information, including
 * name, email, phone number, and profile picture. It includes functionality for
 * selecting an image from the device gallery, deleting the profile picture, and
 * saving or canceling changes.
 * <p>
 * User data is pulled from a database on creation, and updates are saved to the
 * database when the user confirms changes.
 */

public class EntrantProfileEditActivity extends AppCompatActivity {

    private String user_id;
    private HashMap<String, Object> user;
    private ImageView profilePic;
    private Bitmap selectedProfilePicBitmap = null; // Store the selected profile picture bitmap
    private boolean isProfilePicChanged = false;    // Track if the profile picture has changed
    private Database db;

    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 100;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    /**
     * Initializes the activity, sets up the user interface, retrieves current user data from
     * the database, and sets up click listeners for buttons.
     * <p>
     * It also sets up edge-to-edge insets for the UI and initializes the image picker
     * for profile picture selection.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
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
        profilePic = findViewById(R.id.activity_entrant_profile_edit_profile_picture);
        Button saveChangesButton = findViewById(R.id.entrant_profile_edit_save_changes_button);
        Button cancelButton = findViewById(R.id.entrant_profile_edit_cancel_button);
        ImageView imageButton = findViewById(R.id.entrant_profile_edit_image_button);
        ImageView deleteProfileButton = findViewById(R.id.delete_profile_button);

        // TODO: Implement notification checkbox
        Button notificationCheckbox = findViewById(R.id.entrant_profile_edit_notification_checkbox);


        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            // Convert URI to bitmap
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                            // Update ImageView and set flags
                            profilePic.setImageBitmap(bitmap);
                            selectedProfilePicBitmap = bitmap;
                            isProfilePicChanged = true;

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Pull and display all user Info here
        db = new Database();
        db.getCurrentUser(userId -> {
            if (userId != null) {
                this.user_id = userId;
                db.getUser(userId, user -> {
                    if (user != null) {
                        // Process data
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

        imageButton.setOnClickListener(v -> {
            requestGalleryPermissions();
        });

        deleteProfileButton.setOnClickListener(v -> {
            profilePic.setImageResource(R.drawable.image_placeholder);
            selectedProfilePicBitmap = null;
            isProfilePicChanged = true;
        });

        saveChangesButton.setOnClickListener(v -> {
            inputValidator validator = new inputValidator(this);
            if(validator.validateUserProfile(nameInput.getText().toString(), emailInput.getText().toString(), phoneInput.getText().toString())) {
                // Update user info in hashmap
                HashMap<String, Object> newUser = new HashMap<>();
                newUser.put("name", nameInput.getText().toString());
                newUser.put("email", emailInput.getText().toString());
                String phoneIn = phoneInput.getText().toString();
                if (phoneIn.isEmpty()){
                    phoneIn = "";
                }
                newUser.put("phone", phoneIn);
                Log.d("user", "newusername: " + newUser);
                // Check if profile picture was changed
                if (isProfilePicChanged) {
                    if (selectedProfilePicBitmap != null) {
                        // Convert Bitmap to Base64 String
                        String base64Image = imageUtils.bitmapToBase64(selectedProfilePicBitmap);
                        newUser.put("profilePhoto", base64Image);
                    } else {
                        newUser.put("profilePhoto", "");
                    }
                }
                Log.d("user", "User updated: " + newUser);

                db.updateDocument("users", user_id, newUser, success -> {
                    if(success) {
                        Log.d("user", "User updated");
                        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Log.d("user", "User not updated");
                        Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        cancelButton.setOnClickListener(v -> {
            finish();
        });

    }
    /**
     * Opens the device's image picker to select a new profile picture.
     */
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    /**
     * Requests the necessary permissions to access the device's external storage.
     */
    private void requestGalleryPermissions(){
        // Check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            openImageChooser();
        }
    }
    /**
     * Handles permission result for accessing external storage. If permission is granted,
     * the image picker is opened; otherwise, a toast message is displayed to the user.
     *
     * @param requestCode  The request code for the permission request.
     * @param permissions  The requested permissions.
     * @param grantResults The results for the requested permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {

            openImageChooser();
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openImageChooser();
//            } else {
//                Toast.makeText(this, "Permission required to access photos", Toast.LENGTH_SHORT).show();
//            }
        }
    }


}