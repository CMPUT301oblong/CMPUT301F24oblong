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
                finish();
            }
        });

    }

    private void initializeData(Intent intent){
        Bundle bundle = getIntent().getExtras();
        event = (Event) bundle.get("EVENT");
        eventNameTextView.setText(event.getEventName());
        eventDescriptionTextView.setText(event.getEventDescription());
    }

    private void userAccepted(){
        //TO DO:
        //Update Database and add the user as participant in the event
        //Take the user to the event screen
        db = FirebaseFirestore.getInstance();
        String event_id = event.getEventID();

        // Retrieve the current user's ID
        Database.getCurrentUser(user_id -> {
            if (user_id != null) {
                this.user_id = user_id;

                db.collection("participants").whereEqualTo("entrant", user_id).whereEqualTo("event", event_id).get().addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    String eventId = doc.getString("event");

                                    DocumentReference participantRef = doc.getReference();

                                    // Update the status field to "attending"
                                    participantRef.update("status", "attending")
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("EntrantEventAcceptDescriptionActivity", "Status updated to attending");

                                                finish();
                                            })
                                            .addOnFailureListener(e -> Log.e("EntrantEventAcceptDescriptionActivity", "Error updating status", e));
                                }
                            } else {
                                Log.e("EntrantEventAcceptDescriptionActivity", "No participant document found for this event and user");
                            }
                        })
                        .addOnFailureListener(e -> Log.e("EntrantEventAcceptDescriptionActivity", "Error retrieving participant document", e));
            } else {
                Log.e("EntrantEventAcceptDescriptionActivity", "Failed to retrieve user ID");
            }
        });
    }


        //String eventId = QueryDocumentSnapshot doc.getString("event");
}
