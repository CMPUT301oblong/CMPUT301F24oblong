package com.example.oblong.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.Database;
import com.example.oblong.R;
import com.google.firebase.firestore.GeoPoint;

public class EntrantJoinEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize
        Database db = new Database();
        Button joinButton = findViewById(R.id.join_event_join_button);
        Button cancelButton = findViewById(R.id.join_event_cancel_button);
        Intent intent = getIntent();
        String event_id = intent.getStringExtra("event");

        // cancel button listener
        cancelButton.setOnClickListener(v -> {
            startActivity(new Intent(EntrantJoinEventActivity.this, EntrantEventListActivity.class));
        });

        // join button listener
        joinButton.setOnClickListener(v -> {
            // get current user (asynchronous)
            db.getCurrentUser(userId -> {
                // once userid is retrieved add participant
                String participantId = userId + event_id;
                GeoPoint location = new GeoPoint(0, 0);
                String status = "attending";

                // add user as a participant
                db.addParticipant(participantId, userId, event_id, location, status);
                startActivity(new Intent(EntrantJoinEventActivity.this, EntrantEventListActivity.class));
            });
        });
    }
}
