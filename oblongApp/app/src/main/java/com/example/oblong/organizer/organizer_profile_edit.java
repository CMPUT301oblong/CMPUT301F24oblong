package com.example.oblong.organizer;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
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

public class organizer_profile_edit extends AppCompatActivity {

    private String user_id;
    private String facility_id;
    private HashMap<String, Object> facility;
    private ImageView profilePic;
    private Bitmap selectedProfilePicBitmap = null; // Store the selected profile picture bitmap
    private boolean isProfilePicChanged = false;    // Track if the profile picture has changed

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
        setContentView(R.layout.activity_organizer_profile_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText facilityNameInput = findViewById(R.id.facility_name_editText);
        EditText facilityEmailInput = findViewById(R.id.facility_email_editText);
        EditText facilityPhoneInput = findViewById(R.id.facility_phone_editText);
        profilePic = findViewById(R.id.user_profile_image);
        Button saveChangesButton = findViewById(R.id.save_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        ImageView imageButton = findViewById(R.id.user_profile_edit_button);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            // Convert URI to bitmap
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            if (imageUtils.isImageTooLarge(bitmap)){
                                Toast.makeText(this, "Image is too large", Toast.LENGTH_LONG).show();

                            } else {
                                // Update ImageView and set flags
                                profilePic.setImageBitmap(bitmap);
                                selectedProfilePicBitmap = bitmap;
                                isProfilePicChanged = true;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Pull and display all user Info here
        Database db = new Database();
        db.getCurrentUser(userId -> {
            if (userId != null) {
                db.getOrganizer(userId, organizer -> {
                    if (organizer != null) {
                        // Process data
                        facility_id = (String) organizer.get("facility");
                        db.getFacility(facility_id, facility -> {
                            if (facility != null) {
                                // Process data
                                // FIXME: Picture decoding not working. crashes when opening activity. (below)
//                                profilePic.setImageResource(facility.get("photo") == null ? R.drawable.image_placeholder : (int) user.get("photo"));
                                facilityNameInput.setText((CharSequence) facility.get("name"));
                                facilityEmailInput.setText((CharSequence) facility.get("email"));
                                facilityPhoneInput.setText((CharSequence) (facility.get("phone") == null ? "" : facility.get("phone")));
                                Log.d("Facility", "Obtained all facility info"); // print user's name to console

                                // Set up save changes button
                                saveChangesButton.setOnClickListener(v -> {
                                    inputValidator validator = new inputValidator(this);
                                    if(validator.validateFacilityProfile(facilityNameInput.getText().toString(), facilityEmailInput.getText().toString(), facilityPhoneInput.getText().toString())) {
                                        // Update user info in hashmap
                                        facility.put("name", facilityNameInput.getText().toString());
                                        facility.put("email", facilityEmailInput.getText().toString());
                                        facility.put("phone", facilityPhoneInput.getText().toString().isEmpty() ? null : facilityPhoneInput.getText().toString());

                                        Log.d("savechanges", "facility name: " + facility.get("name"));
                                        // Check if profile picture was changed
                                        if (isProfilePicChanged) {
                                            if (selectedProfilePicBitmap != null) {
                                                // Convert Bitmap to Base64 String
                                                String base64Image = imageUtils.bitmapToBase64(selectedProfilePicBitmap);
                                                facility.put("photo", base64Image);
                                            } else {
                                                facility.put("photo", null); // Set to null if deleted
                                            }
                                        }

                                        Log.d("facility", "facility updated");

                                        db.updateDocument("facilities", facility_id, facility, success -> {
                                            if(success) {
                                                Log.d("facility", "facility updated");
                                                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
                                                setResult(RESULT_OK);
                                                finish();
                                            } else {
                                                Log.d("facility", "facility not updated");
                                                Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else {
                                Log.d("Facility", "facility not found or an error occurred.");
                            }
                        });
                    }
                });
            }
        });

        imageButton.setOnClickListener(v -> {
            // Check for permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                openImageChooser();
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
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    /**
     * Handles permission result for accessing external storage. If permission is granted,
     * the image picker is opened; otherwise, a toast message is displayed to the user.
     *
     * @param requestCode  The request code for the permission request.
     * @param permissions  The requested permissions.
     * @param grantResults The results for the requested permissions.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            openImageChooser();
        }
    }


}