package com.example.oblong.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.imageUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

public class EntrantEventDetails extends AppCompatActivity {

    private ImageButton backButton;
    private Button cancelButton;
    private Button proceedButton;

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

    private void fetchEventDetails() {

        // FIXME: SOMETHING IN HERE IS BROKEN

        Database db = new Database();
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

        String event_id = event.getEventID();

        // Set click listeners
        proceedButton = findViewById(R.id.proceed_button);
        cancelButton = findViewById(R.id.cancel_button);

        String status = event.getStatus();
//         Setup Text in proceed Button
        if(status == null) {
            proceedButton.setText("JOIN EVENT");
        }
        switch (status) {
            case "attending":
                proceedButton.setText("LEAVE EVENT");
                break;
            case "waitlisted":
                proceedButton.setText("LEAVE WAITLIST");
                break;
            case "selected":
                proceedButton.setText("ACCEPT INVITE");
                // Set CancelButton to be wide enough to fit text
                ViewGroup.LayoutParams params = cancelButton.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                cancelButton.setLayoutParams(params);
                cancelButton.setText("REJECT INVITE");

                break;
        }

        proceedButton.setOnClickListener(v -> {
            // If Attending, then Leave Event
            if(status.equals("attending")) {
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
}
