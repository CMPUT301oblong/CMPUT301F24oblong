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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
/**
 * Activity for displaying detailed information about an event.
 *
 * <p>This activity provides the event name, description, and image. Users can navigate back to the
 * previous screen or leave the event as a participant.</p>
 */
public class EntrantEventDescriptionActivity extends AppCompatActivity {

    private Event event;
    private TextView eventNameTextView;
    private ImageView eventImageView;
    private TextView eventDescriptionTextView;
    private ImageButton backButton;
    private Button leaveButton;
    private FirebaseFirestore db;
    private CollectionReference participantsRef;
    private CollectionReference eventsRef;


    /**
     * Initializes the activity, sets up the UI components, and handles user interactions.
     *
     * @param savedInstanceState The saved instance state containing the activity's previously
     *                           saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_event_description);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //assign Views
     eventNameTextView = findViewById(R.id.entrant_event_description_event_name);
     eventImageView = findViewById(R.id.entrant_event_description_photo);
     eventDescriptionTextView = findViewById(R.id.entrant_event_description_text);
     backButton = findViewById(R.id.entrant_event_description_back_button);
     leaveButton = findViewById(R.id.entrant_event_description_leave_button);


     //Get Data from Intent
     Intent intent = getIntent();
    initializeData(intent);



     //Back button listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLeft();
            }
        });


    }
    /**
     * Initializes event data by extracting it from the received Intent and setting it to the view components.
     *
     * @param intent The Intent containing event data passed from the previous activity.
     */
    private void initializeData(Intent intent){
        Bundle bundle = getIntent().getExtras();
        event = (Event) bundle.get("EVENT");
        eventNameTextView.setText(event.getEventName());
        eventDescriptionTextView.setText(event.getEventDescription());
    }
    /**
     * Handles the process when a user leaves the event.
     *
     * <p>This method finds the participant entry in the Firestore database based on the current user's ID
     * and the event ID. If found, it removes the participant's document from the "participants" collection
     * and finishes the activity, returning the user to their events list.</p>
     */
    private void userLeft(){
        //TO DO
        //remove participant relation from database
        //kick user back to their events

        //FIND PARTICIPANT RELATION WITH BOTH USERID AND EVENTID
        db = FirebaseFirestore.getInstance();
        participantsRef = db.collection("participants");
        eventsRef = db.collection("events");



        Database.getCurrentUser(new Database.OnDataReceivedListener<String>() {
            /**
             * Callback method invoked when the current user's ID is received.
             *
             * @param data The current user's ID received from the database.
             */
            @Override
            public void onDataReceived(String data) {
                db.collection("participants")
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

}