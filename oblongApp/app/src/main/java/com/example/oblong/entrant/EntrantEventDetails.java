package com.example.oblong.entrant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.imageUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

public class EntrantEventDetails extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    GeoPoint entrantLocation = new GeoPoint(0,0);
    String event_id;

    private ImageButton backButton;
    private Button cancelButton;
    private Button proceedButton;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_view_event_details);


        // Find views
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        setUpBottomSheet();
        fetchEventDetails();

    }

    private void setUpBottomSheet() {
        View bottomSheet = findViewById(R.id.bottomSheet);
        ImageView imageView = findViewById(R.id.eventDetailsPoster);
        LinearLayout header = findViewById(R.id.header);

        // Attach BottomSheetBehavior
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Make it persistent (cannot hide)
        bottomSheetBehavior.setHideable(false);

        // Set the default state
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Adjust the image height dynamically based on the BottomSheet height and toolbar height
        bottomSheet.post(() -> {
            // Get the total height of the screen
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            // Get the height of the toolbar (ImageButton + TextView)
            int toolbarHeight = header.getHeight(); // This will return the height of the toolbar

            // Get the current peek height of the BottomSheet
            int peekHeight = bottomSheetBehavior.getPeekHeight();

            // Calculate available space for the ImageView
            int availableHeight = screenHeight - toolbarHeight - peekHeight;

            // Set the height of the ImageView to the available space
            imageView.getLayoutParams().height = availableHeight;
            imageView.requestLayout();
        });

        // Optional: Add listeners
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // Handle expanded state
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // Handle collapsed state
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Handle sliding offset
            }
        });
    }

    // Literally don't touch cuz this is holding on by a thread and I have no idea what's keeping it together
    private void fetchEventDetails() {

        // FIXME: SOMETHING IN HERE IS BROKEN

        db = new Database();
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();


        TextView title = findViewById(R.id.entrant_event_list_item_event_name);
        TextView desc = findViewById(R.id.eventDescription);
        TextView date = findViewById(R.id.drawDate);
        TextView time = findViewById(R.id.drawTime);
        ImageView poster = findViewById(R.id.eventDetailsPoster);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Event event = (Event) bundle.get("EVENT");

        title.setText(event.getEventName());
        desc.setText(event.getEventDescription());
        if(event.getPoster() != null) {
            poster.setImageBitmap(imageUtils.base64ToBitmap(event.getPoster()));
        }

        date.setText(event.getEventCloseLong());
        time.setText(event.getEventCloseTime());

        event_id = event.getEventID();
        Log.d("event_details", event_id);

        // Set click listeners
        proceedButton = findViewById(R.id.proceed_button);
        cancelButton = findViewById(R.id.cancel_button);

        String status = event.getStatus();

//         Setup Text in proceed Button
        if(status.equals("NA")) {
            proceedButton.setText("JOIN EVENT");
        } else if (status.equals("attending")) {
            proceedButton.setText("LEAVE EVENT");
        } else if (status.equals("waitlisted")) {
            proceedButton.setText("LEAVE WAITLIST");
        } else if (status.equals("selected")) {
            proceedButton.setText("ACCEPT INVITE");
            // Set CancelButton to be wide enough to fit text
            ViewGroup.LayoutParams params = cancelButton.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            cancelButton.setLayoutParams(params);
            cancelButton.setText("REJECT INVITE");
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        boolean locationRequired;

        //TODO: Make a thing that checks if location is required
//        if (event.get("locationRequired") != null && event.get("locationRequired").equals("1")){
//            locationRequired = true;
//            Toast.makeText(this, "Location permissions are required to join this event", Toast.LENGTH_SHORT).show();
//        } else {
            locationRequired = false;
//        }

        proceedButton.setOnClickListener(v -> {
            // If NA, then Join Event
            if(status.equals("NA")) {
                Log.d("event_details", "NA button clicked");
                // Handle join event
                if(locationRequired) {
                    getPermissionAndJoin();
                }else{
                    // get current user (asynchronous)
                    db.getCurrentUser(userId -> {
                        String participantId = userId + event_id;

                        // Add user as a participant
                        db.addParticipant(participantId, userId, event_id, entrantLocation, "waitlisted");
                        Log.d("event_details", String.format("Passed in %s, %s, %s, %s, %s", participantId, userId, event_id, entrantLocation, "waitlisted"));

                        Log.d("button", "User Joined event after button clicked");
                        startActivity(new Intent(this, EntrantBaseActivity.class));
//                        finish();
                    });
                }
            }
            // If Attending, then Leave Event
            else if(status.equals("attending")) {
                Log.d("button", "attending button clicked");
                Database.getCurrentUser(new Database.OnDataReceivedListener<String>() {
                    /**
                     * Callback method invoked when the current user's ID is received.
                     *
                     * @param data The current user's ID received from the database.
                     */
                    @Override
                    public void onDataReceived(String data) {
                        fdb.collection("participants")
                                .whereEqualTo("event", event.getEventID())
                                .whereEqualTo("entrant", data)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                            if(!queryDocumentSnapshots.isEmpty()){
                                                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                                                doc.getReference().delete();
                                            }
                                        }
                                );
                    }
                });
                finish();
            }

            // If Waitlisted then Leave Waitlist
            else if(status.equals("waitlisted")) {
                Log.d("button", "waitlisted button clicked");
                db.getCurrentUser(userId -> {
                    // once userid is retrieved remove participant
                    String participantId = userId + event_id;

                    // add user as a participant
                    HashMap<String, Object> cancelParticipant = new HashMap<>();
                    cancelParticipant.put("status","cancelled");
                    db.updateDocument("participants", participantId,cancelParticipant, participant -> {
                        if (participant != null) {
                            // Go back
                            finish();
                        }
                    });

                });
            }

            // If Selected, then Join Event
            else if(status.equals("selected")) {
                Database.getCurrentUser(user_id -> {
                    if (user_id != null) {

                        // check for entrants with the given user id and events with the given event id
                        fdb.collection("participants").whereEqualTo("entrant", user_id).whereEqualTo("event", event_id).get().addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    DocumentReference participantRef = doc.getReference();

                                    // Update the status field to "attending"
                                    participantRef.update("status", "attending").addOnSuccessListener(aVoid -> {
                                        Log.d("EntrantEventAcceptDescriptionActivity", "Status updated to attending");
                                        // finish after status is updated
                                        finish();

                                    }).addOnFailureListener(e -> Log.e("EntrantEventAcceptDescriptionActivity", "Error updating status", e));
                                }
                            }
                            else {
                                Log.e("EntrantEventAcceptDescriptionActivity", "No participant document found for this event and user");
                            }
                        }).addOnFailureListener(e -> Log.e("EntrantEventAcceptDescriptionActivity", "Error retrieving participant document", e));
                    }
                    else {
                        Log.e("EntrantEventAcceptDescriptionActivity", "Failed to retrieve user ID");
                    }
                });
            }

            finish();
        });

        cancelButton.setOnClickListener(v -> {
            if(status.equals("selected")) {
                Database.getCurrentUser(user_id -> {
                    if (user_id != null) {
                        // check for entrants with the given user id and events with the given event id
                        fdb.collection("participants").whereEqualTo("entrant", user_id).whereEqualTo("event", event_id).get().addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    String eventId = doc.getString("event");

                                    DocumentReference participantRef = doc.getReference();

                                    // Update the status field to "cancelled" and draw another entrant from the waitlist if so.
                                    participantRef.update("status", "cancelled").addOnSuccessListener(aVoid -> {
                                        Log.d("EntrantEventAcceptDescriptionActivity", "Status updated to cancelled");
                                        //Draw a new entrant once they cancelled
                                        event.drawOneEntrant();
                                        // finish after status is updated

                                    }).addOnFailureListener(e -> Log.e("EntrantEventAcceptDescriptionActivity", "Error updating status", e));
                                }
                            }
                            else {
                                Log.e("EntrantEventAcceptDescriptionActivity", "No participant document found for this event and user");
                            }
                        }).addOnFailureListener(e -> Log.e("EntrantEventAcceptDescriptionActivity", "Error retrieving participant document", e));
                    }
                    else {
                        Log.e("EntrantEventAcceptDescriptionActivity", "Failed to retrieve user ID");
                    }
                });
            }
            finish();
        });
    }

    private void getPermissionAndJoin() {
        // request permissions every time

        Log.d("event_details", "getPermissionAndJoin called");

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                1001);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Logic to handle location object
                                double lat = location.getLatitude();
                                double lng = location.getLongitude();
                                entrantLocation = new GeoPoint(lat, lng);

                                // get current user (asynchronous)
                                db.getCurrentUser(userId -> {
                                    String participantId = userId + event_id;
                                    String status = "waitlisted";

                                    // Add user as a participant
                                    db.addParticipant(participantId, userId, event_id, entrantLocation, status);
                                    startActivity(new Intent(EntrantEventDetails.this, EntrantBaseActivity.class));
                                });
                            }
                        }
                    });
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // FIXME: This isn't ever called for some reason
        Log.d("event_details", "onRequestPermissionsResult called");

        if (requestCode == 1001) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed to get location
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    // Logic to handle location object
                                    double lat = location.getLatitude();
                                    double lng = location.getLongitude();
                                    entrantLocation = new GeoPoint(lat, lng);

                                    // get current user (asynchronous)
                                    db.getCurrentUser(userId -> {
                                        String participantId = userId + event_id;
                                        String status = "waitlisted";

                                        // Add user as a participant
                                        db.addParticipant(participantId, userId, event_id, entrantLocation, status);
                                        startActivity(new Intent(EntrantEventDetails.this, EntrantBaseActivity.class));
                                    });
                                }
                            }
                        });
            } else {
                // Permissions denied, inform the user
                Toast.makeText(this, "Enable location permissions in settings to join event", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
