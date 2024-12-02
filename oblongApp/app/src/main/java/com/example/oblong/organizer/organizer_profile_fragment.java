package com.example.oblong.organizer;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.oblong.Database;
import com.example.oblong.R;
import com.example.oblong.entrant.EntrantBaseActivity;
import com.example.oblong.entrant.EntrantProfileEditActivity;
import com.example.oblong.imageUtils;

public class organizer_profile_fragment extends Fragment {

    private String user_id;

    private TextView name;
    private TextView type;
    private Button email;
    private Button phone;
    private TextView facility_name;
    private Button facility_email;
    private Button facility_phoneno;
    private ImageView profilePic;
    private ImageView profilePicBackground;
    ImageButton editProfileButton;
    ImageButton editFacilityButton;
    Button entrantViewButton;
    private Database db;

    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // The edit activity finished successfully, update UI
                    fetchUserProfileData();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_organizer_profile, container, false);

        // Initialize UI elements using the `view` object
        name = view.findViewById(R.id.profile_name);
        type = view.findViewById(R.id.profile_type);
        email = view.findViewById(R.id.profile_email);
        phone = view.findViewById(R.id.profile_phoneno);
        facility_name = view.findViewById(R.id.facility_name);
        facility_email = view.findViewById(R.id.facility_email);
        facility_phoneno = view.findViewById(R.id.facility_phoneno);
        profilePic = view.findViewById(R.id.imageView);
        profilePicBackground = view.findViewById(R.id.profile_picture_background);
        editProfileButton = view.findViewById(R.id.profile_edit_button);
        editFacilityButton = view.findViewById(R.id.facility_edit_button);
        entrantViewButton = view.findViewById(R.id.delete_user_button);


        // Pull and display all user Info here
        db = new Database();
        fetchUserProfileData();

        editProfileButton.setOnClickListener(v -> {
            // Handle edit profile button click
            Intent intent = new Intent(getActivity(), EntrantProfileEditActivity.class);
            editProfileLauncher.launch(intent);
        });

        editFacilityButton.setOnClickListener(v -> {
            // Handle edit facility button click
            Intent intent = new Intent(getActivity(), organizer_profile_edit.class);
            editProfileLauncher.launch(intent);
        });

        entrantViewButton.setOnClickListener(v -> {
            // Handle edit facility button click
            Intent intent = new Intent(getActivity(), EntrantBaseActivity.class);
            startActivity(intent);
        });
        return view;
    }


    private void fetchUserProfileData() {
        Database.getCurrentUser(userId -> {
            if (userId != null) {
                // Get User Information
                db.getUser(userId, user -> {
                    if (user != null) {
                        // Update the UI with user data
                        if (user.get("profilePhoto") == null) {
                            profilePic.setImageResource(R.drawable.image_placeholder);
                        } else {
                            Bitmap profileBitmap = imageUtils.base64ToBitmap((String) user.get("profilePhoto"));
                            if (profileBitmap != null) {
                                profilePic.setImageBitmap(profileBitmap);
                                profilePicBackground.setImageBitmap(imageUtils.fastblur(profileBitmap, 0.1f, 10));
                                type.setText(user.get("type").toString().toUpperCase());
                                name.setText((CharSequence) user.get("name"));
                                email.setText((CharSequence) user.get("email"));
                                phone.setText((CharSequence) (user.get("phone") == null ? "No Phone # Provided" : user.get("phone")));

                                Log.d("user", "User Name: " + user.get("name"));
                            } else {
                                Log.d("user", "User not found or an error occurred.");
                            }
                        }
                        // Get Facility Information
                        db.getOrganizer(userId, organizer -> {
                            if (organizer != null) {
                                // Process data
                                String facility_id = (String) organizer.get("facility");
                                db.getFacility(facility_id, facility -> {
                                    if (facility != null) {
                                        // Process data
                                        facility_name.setText((CharSequence) facility.get("name"));
                                        facility_email.setText((CharSequence) facility.get("email"));
                                        facility_phoneno.setText((CharSequence) (facility.get("phone") == null ? "No Phone # Provided" : facility.get("phone")));

                                        Log.d("Facility", "Obtained all facility info"); // print user's name to console
                                    } else {
                                        Log.d("Facility", "facility not found or an error occurred.");
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}

