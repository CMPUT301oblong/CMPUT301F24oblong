package com.example.oblong;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EntrantProfileScreenFragment extends Fragment {

    private TextView name;
    private TextView email;
    private TextView phone;
    private ImageView profilePic;
    ImageButton editProfileButton;
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
        View view = inflater.inflate(R.layout.activity_entrant_profile_screen, container, false);

        // Initialize UI elements using the `view` object
        name = view.findViewById(R.id.entrant_profile_name);
        email = view.findViewById(R.id.entrant_profile_email);
        phone = view.findViewById(R.id.entrant_profile_phone);
        profilePic = view.findViewById(R.id.entrant_profile_picture);
        editProfileButton = view.findViewById(R.id.entrant_profile_edit_button);

        // Pull and display all user Info here
        db = new Database();
        fetchUserProfileData();

        editProfileButton.setOnClickListener(v -> {
            // Handle edit profile button click
            Intent intent = new Intent(getActivity(), EntrantProfileEditActivity.class);
            editProfileLauncher.launch(intent);
        });

        return view;
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