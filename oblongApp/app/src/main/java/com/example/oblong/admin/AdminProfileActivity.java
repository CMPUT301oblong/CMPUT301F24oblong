package com.example.oblong.admin;

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
import com.example.oblong.entrant.EntrantProfileEditActivity;
import com.example.oblong.organizer.AddNewFacilityDialog;
import com.example.oblong.organizer.organizer_base_activity;
import com.example.oblong.imageUtils;

import java.util.HashMap;
import java.util.UUID;

public class AdminProfileActivity extends Fragment {

    private String user_id;
    private TextView name;
    private TextView email;
    private TextView phone;
    private Database db;

    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // The edit activity finished successfully, update UI
                    fetchUserProfileData();
                }
            });

    /**
     * {@class onCreateView} is called to have the fragment instantiate its user interface view.
     * Fills in the layout and initializes the UI elements.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_profile, container, false);

        // Initialize UI elements using the `view` object
        name = view.findViewById(R.id.admin_name_text);
        email = view.findViewById(R.id.admin_email_text);
        phone = view.findViewById(R.id.admin_phoneNumber_text);

        db = new Database();
        fetchUserProfileData();

        return view;
    }


    /**
     * {@code onViewCreated} is called after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * fetches data from the Firebase for the users profile data
     */
    private void fetchUserProfileData() {
        Database.getCurrentUser(userId -> {
            if (userId != null) {
                this.user_id = userId;
                db.getUser(userId, user -> {
                    if (user != null) {

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