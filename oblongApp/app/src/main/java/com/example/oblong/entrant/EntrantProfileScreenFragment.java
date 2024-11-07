package com.example.oblong.entrant;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.oblong.AddNewUserDialog;
import com.example.oblong.Database;
import com.example.oblong.R;
import com.example.oblong.organizer.AddNewFacilityDialog;
import com.example.oblong.organizer.organizer_base_activity;

import java.util.HashMap;
import java.util.UUID;

public class EntrantProfileScreenFragment extends Fragment implements AddNewFacilityDialog.AddFacilityDialogListener {

    private String user_id;
    private TextView name;
    private TextView email;
    private TextView phone;
    private ImageView profilePic;
    private TextView organizer_text;
    ImageButton editProfileButton;
    Button addFacilityButton;
    private Database db;
    boolean isOrganizer;

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
        addFacilityButton = view.findViewById(R.id.entrant_profile_create_facility);
        organizer_text = view.findViewById(R.id.become_an_organizer);

        db = new Database();
        fetchUserProfileData();

        addFacilityButton.setOnClickListener(v -> {
            if (isOrganizer) {
                Intent intent = new Intent(getActivity(), organizer_base_activity.class);
                startActivity(intent);
            } else {
                AddNewFacilityDialog dialog = new AddNewFacilityDialog();
                dialog.setListener(this);  // Set the listener directly
                dialog.show(getParentFragmentManager(), "add_facility_dialog");
            }
        });


        // Pull and display all user Info here

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EntrantProfileEditActivity.class);
            editProfileLauncher.launch(intent);
        });

        return view;
    }



    private void fetchUserProfileData() {
        db.getCurrentUser(userId -> {
            if (userId != null) {
                this.user_id = userId;
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
                db.getOrganizer(userId, organizer -> {
                    if (organizer != null) {
                        isOrganizer = true;

                        organizer_text.setVisibility(View.GONE);
                        addFacilityButton.setText("Organizer View");
                    } else {
                        isOrganizer = false;
                    }
                });

            }
        });
    }

    @Override
    public void addFacility(String name, String email, String phone) {
        // Generate UUID
        String facility_id = UUID.randomUUID().toString();
        db.addFacility(facility_id, email, name, (phone.isEmpty() ? null : phone), null);
        db.addOrganizer(user_id, facility_id, user_id);

        // Change user entry
        HashMap<String, Object> user_updates = new HashMap<>();
        user_updates.put("type", "organizer");
        db.updateDocument("users", user_id, user_updates, success -> {
            if(success) {
                Toast.makeText(getContext(), "You are now an organizer", Toast.LENGTH_SHORT).show();
                Log.d("user", "User updated");
            } else {
                Toast.makeText(getContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
                Log.d("user", "User not updated");
            }
        });
        Intent intent = new Intent(getActivity(), organizer_base_activity.class);
        startActivity(intent);
    }
}