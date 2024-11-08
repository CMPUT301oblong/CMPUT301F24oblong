package com.example.oblong.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
import com.example.oblong.imageUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Base64;
import java.util.HashMap;

/**
 * Activity class for joining an event as an entrant.
 *
 * <p>This activity displays the details of an event, such as its title, description, date,
 * and poster image, and provides the option for the user to join the event or cancel the action.</p>
 */
public class EntrantJoinEventActivity extends AppCompatActivity {
    /**
     * Initializes the activity, sets up the UI, and handles user interactions.
     *
     * <p>In this method, the activity's layout is set, and window insets are applied to ensure
     * that the content is displayed edge-to-edge. It also retrieves event data from the intent's
     * extras and populates the UI with this data. The join and cancel buttons are set up to
     * perform respective actions when clicked.</p>
     *
     * @param savedInstanceState The saved instance state containing the activity's previously
     *                           saved state, if any.
     */
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
        TextView title = findViewById(R.id.activity_join_event_banner_2);
        TextView desc = findViewById(R.id.activity_join_event_event_description);
        TextView date = findViewById(R.id.join_event_draw_date);
        ImageView poster = findViewById(R.id.join_event_imageView);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        HashMap<String, Object> event = (HashMap<String, Object>) bundle.get("event");
        title.setText((String)event.get("name"));
        desc.setText((String) event.get("description"));
        Timestamp fireDate = (Timestamp) event.get("dateAndTime");
        String img = (String) event.get("poster");

        date.setText(fireDate.toDate().toString());
        poster.setImageBitmap(imageUtils.base64ToBitmap(img));

        String event_id = (String)event.get("id");


        // cancel button listener
        cancelButton.setOnClickListener(v -> {
            startActivity(new Intent(EntrantJoinEventActivity.this, EntrantBaseActivity.class));
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
                startActivity(new Intent(EntrantJoinEventActivity.this, EntrantBaseActivity.class));
            });
        });
    }
}
