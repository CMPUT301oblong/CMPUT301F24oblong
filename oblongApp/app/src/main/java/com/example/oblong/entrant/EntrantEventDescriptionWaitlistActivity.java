package com.example.oblong.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.Database;
import com.example.oblong.R;

import java.util.HashMap;
/**
 * Activity class for managing the event description page when an entrant is on the waitlist.
 *
 * <p>This activity allows the user to leave the event waitlist and provides a back navigation option.</p>
 */
public class EntrantEventDescriptionWaitlistActivity extends AppCompatActivity {
    /**
     * Initializes the activity, sets up the UI, and handles user interactions.
     *
     * <p>This method sets the content view, applies edge-to-edge display configurations, and
     * initializes the UI components. It sets up the back button to navigate to the event list
     * and a leave button to update the participant status to "cancelled".</p>
     *
     * @param savedInstanceState The saved instance state containing the activity's previously
     *                           saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_event_description_waitlist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize
        Database db = new Database();
        Button joinButton = findViewById(R.id.entrant_event_description_leave_button);
        ImageButton backButton = findViewById(R.id.entrant_event_description_back_button);
        Intent intent = getIntent();
        String event_id = intent.getStringExtra("event");

        /**
         * Handles the click event for the back button.
         *
         * <p>When the user clicks the back button, the activity navigates back to the
         * {@link EntrantEventListActivity}, allowing the user to view the list of events.</p>
         */
        // cancel button listener
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(EntrantEventDescriptionWaitlistActivity.this, EntrantEventListActivity.class));
        });


        /**
         * Handles the click event for the join button.
         *
         * <p>When the user clicks the "Leave" button, the current user's ID is retrieved asynchronously.
         * The participant's status is then updated to "cancelled" in the database, effectively removing
         * them from the event waitlist. The activity then navigates back to the {@link EntrantEventListActivity}.</p>
         */
        // join button listener
        joinButton.setOnClickListener(v -> {
            // get current user (asynchronous)
            db.getCurrentUser(userId -> {
                // once userid is retrieved remove participant
                String participantId = userId + event_id;

                // add user as a participant
                HashMap<String, Object> cancelParticipant = new HashMap<>();
                cancelParticipant.put("status","cancelled");
                db.updateDocument("participants", participantId,cancelParticipant, participant -> {
                    if (participant != null) {
                        // Process data
                        startActivity(new Intent(EntrantEventDescriptionWaitlistActivity.this, EntrantEventListActivity.class));
                    }
                });

            });
        });
    }
}