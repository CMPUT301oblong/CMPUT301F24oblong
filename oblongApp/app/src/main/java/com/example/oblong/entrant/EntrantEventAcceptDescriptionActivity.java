package com.example.oblong.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;

/**
 * This activity represents the screen where an entrant can view the event details
 * and choose to accept the invitation to participate in the event.
 *
 * <p>It displays the event name, description, and an accept button. If the user chooses
 * to accept, their status as a participant is updated in the Firestore database.</p>
 */
public class EntrantEventAcceptDescriptionActivity extends AppCompatActivity {
    private Event event;
    private TextView eventNameTextView;
    private ImageView eventImageView;
    private TextView eventDescriptionTextView;
    private Button acceptButton;
    private Button cancelButton;
    private CollectionReference participantsRef;
    private FirebaseFirestore db;
    private String user_id;

    /**
     * Called when the activity is first created.
     *
     * <p>This method initializes the view components, retrieves the event data from the intent,
     * and sets up button listeners for accepting or canceling the event invitation.</p>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data supplied by onSaveInstanceState.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_accept_description);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Set up views
        eventNameTextView = findViewById(R.id.activity_accept_event_banner_2);
        eventDescriptionTextView = findViewById(R.id.activity_accept_event_event_description);
        eventImageView = findViewById(R.id.accept_event_imageView);
        acceptButton = findViewById(R.id.accept_event_accept_button);
        cancelButton =findViewById(R.id.accept_event_cancel_button);

        //Get Data from Intent
        Intent intent = getIntent();
        initializeData(intent);

        //accept button listener
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAccepted();
            }
        });

        //cancel button listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCancelled();
            }
        });

    }
    /**
     * Initializes event data from the Intent.
     *
     * <p>This method retrieves the event object from the Intent's extras, and sets the event
     * name and description in the corresponding TextViews.</p>
     *
     * @param intent The Intent containing the event data passed from the previous activity.
     */
    private void initializeData(Intent intent){
        Bundle bundle = getIntent().getExtras();
        event = (Event) bundle.get("EVENT");
        eventNameTextView.setText(event.getEventName());
        eventDescriptionTextView.setText(event.getEventDescription());
    }

    /**
     * Handles the acceptance of an event invitation by the user.
     *
     * <p>This method retrieves the current user's ID and checks the "participants" collection
     * in Firestore to find a document where the "entrant" field matches the user's ID and the
     * "event" field matches the current event's ID. If a matching document is found, it updates
     * the "status" field to "attending".</p>
     *
     * <p>Logs any errors encountered during the Firestore operations, such as failing to update
     * the participant status or retrieving the participant document.</p>
     */
    private void userAccepted(){
        db = FirebaseFirestore.getInstance();
        String event_id = event.getEventID();

        // Retrieve the current user's ID
        Database.getCurrentUser(user_id -> {
            if (user_id != null) {
                this.user_id = user_id;

                // check for entrants with the given user id and events with the given event id
                db.collection("participants").whereEqualTo("entrant", user_id).whereEqualTo("event", event_id).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String eventId = doc.getString("event");

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
    private void userCancelled(){
        db = FirebaseFirestore.getInstance();
        String event_id = event.getEventID();

        // Retrieve the current user's ID
        Database.getCurrentUser(user_id -> {
            if (user_id != null) {
                this.user_id = user_id;

                // check for entrants with the given user id and events with the given event id
                db.collection("participants").whereEqualTo("entrant", user_id).whereEqualTo("event", event_id).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String eventId = doc.getString("event");

                            DocumentReference participantRef = doc.getReference();

                            // Update the status field to "cancelled" and draw another entrant from the waitlist if so.
                            participantRef.update("status", "cancelled").addOnSuccessListener(aVoid -> {
                                Log.d("EntrantEventAcceptDescriptionActivity", "Status updated to cancelled");

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
}
