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
