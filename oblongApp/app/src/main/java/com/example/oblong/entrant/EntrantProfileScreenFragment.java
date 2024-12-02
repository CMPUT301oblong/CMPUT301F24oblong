package com.example.oblong.entrant;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.icu.number.NumberRangeFormatter.with;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
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
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.oblong.Database;
import com.example.oblong.R;
import com.example.oblong.organizer.AddNewFacilityDialog;
import com.example.oblong.organizer.organizer_base_activity;
import com.example.oblong.imageUtils;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Fragment class for displaying user profile data.
 *
 * <p>This fragment retrieves user profile information and received notifications,
 * using Firebase to fetch relevant data. It then populates views with this information
 * for display, and builds and displays Android System Notifications.</p>
 */
public class EntrantProfileScreenFragment extends Fragment implements AddNewFacilityDialog.AddFacilityDialogListener {

    private String user_id;
    private TextView name;
    private TextView type;
    private Button email;
    private Button phone;
    private ImageView profilePic;
    private ImageView profilePicBackground;
    ImageButton editProfileButton;
    Button addFacilityButton;
    private Database db = new Database();
    boolean isOrganizer;
    private static final String CHANNEL_ID = "oblong_channel_id2";
    private Context context;

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
        View view = inflater.inflate(R.layout.activity_entrant_profile_screen, container, false);

        // Initialize UI elements using the `view` object
        name = view.findViewById(R.id.entrant_profile_name);
        type = view.findViewById(R.id.entrant_profile_type);
        email = view.findViewById(R.id.entrant_profile_email);
        phone = view.findViewById(R.id.entrant_profile_phone);
        profilePic = view.findViewById(R.id.entrant_profile_picture);
        profilePicBackground = view.findViewById(R.id.entrant_profile_picture_background);
        editProfileButton = view.findViewById(R.id.entrant_profile_edit_button);
        addFacilityButton = view.findViewById(R.id.entrant_profile_create_facility);

        context = getContext();

        //build Android System notification channel if API >= 26
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            CharSequence name = "oblong_channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(channel);
        }

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

        // TODO: Implement functionality for clicking on email/phone to call or email the user (if there's time)

        // Pull and display all user Info here

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EntrantProfileEditActivity.class);
            editProfileLauncher.launch(intent);
        });

        return view;
    }

    /**
     * {@code fetchUserProfileData} is called in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * fetches data from the Firebase for the users profile data
     * fetches data from the Firebase to build notifications
     * builds Android System Notifications using NotificationManager and NotificationCompat
     */
    private void fetchUserProfileData() {
        db.getCurrentUser(userId -> {
            if (userId != null) {
                this.user_id = userId;
                db.getUser(userId, user -> {
                    if (user != null) {
                        // Update the UI with user data
//                        profilePic.setImageResource(user.get("photo") == null ? R.drawable.image_placeholder :  user.get("profilePhoto"));
                        if(user.get("profilePhoto") == null){
                            profilePic.setImageResource(R.drawable.image_placeholder);
                            // No background for no image
                        }else{
                            Bitmap profileBitmap = imageUtils.base64ToBitmap((String) user.get("profilePhoto"));
                            if (profileBitmap != null) {
                                profilePic.setImageBitmap(profileBitmap);
//                                profilePicBackground.setImageBitmap(profileBitmap);

                                profilePicBackground.setImageBitmap(imageUtils.fastblur(profileBitmap, 0.1f, 10));
                            }

                        }
                        name.setText((CharSequence) user.get("name"));
                        type.setText(user.get("type").toString().toUpperCase());
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

                        addFacilityButton.setText("ORGANIZER VIEW");
                    } else {
                        isOrganizer = false;
                    }
                });
                //this code retrieves and builds android notifications for all of the notifications
                //sent to this entrant
                db.getEntrant(user_id, entrant -> {
                    List<String> notifications = (List<String>) entrant.get("notificationsList");
                    if(notifications != null && !notifications.isEmpty()){
                        for (String n: notifications){
                            db.getNotification(n, notif -> {
                                String title = (String) notif.get("title");
                                String content = (String) notif.get("text");
                                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                        .setSmallIcon(R.mipmap.ic_launcher_round)
                                        .setContentTitle(title)
                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText(content))
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setAutoCancel(true);

                                //don't set channel id if API < 26
                                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                                    builder.setChannelId(null);
                                }
                                //create unique id for notification to prevent android from overwriting
                                //notification in system
                                int id = n.hashCode();
                                notificationManager.notify(id, builder.build());
                            });
                            HashMap<String, Object> entrantUpdate = new HashMap<>();
                            entrantUpdate.put("notificationsList", FieldValue.arrayRemove(n));
                            db.updateDocument("entrants", user_id, entrantUpdate, v -> {});
                        }
                    }
                });
            }
        });
    }



    /**
     * {@code addFacility} is called when the user clicks the  button to become an organizer.
     * It grants the user the role of Organizer, and adds the facility to the database before
     * opening the Organizer base activity.
     *
     * @param name
     * @param email
     * @param phone
     */
    @Override
    public void addFacility(String name, String email, String phone) {
        // Generate UUID
        String facility_id = UUID.randomUUID().toString();
        db.addFacility(facility_id, email, name, (phone.isEmpty() ? null : phone), null);
        Database.getCurrentUser(
                userId -> {
                    if (userId != null) {
                        this.user_id = userId;
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
        );
    }
}