package com.example.oblong;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

public class EntrantEventDescriptionWaitlistActivity extends AppCompatActivity {

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
        Button joinButton = findViewById(R.id.entrant_event_description_waitlist_leave_button);
        ImageButton backButton = findViewById(R.id.entrant_event_description_waitlist_back_button);
        Intent intent = getIntent();
        String event_id = intent.getStringExtra("event");

        // cancel button listener
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(EntrantEventDescriptionWaitlistActivity.this, EntrantEventListActivity.class));
        });

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