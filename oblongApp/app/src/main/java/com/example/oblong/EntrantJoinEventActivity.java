package com.example.oblong;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;


public class EntrantJoinEventActivity extends AppCompatActivity {
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView;
    private TextView eventDrawDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_event);


        eventNameTextView = findViewById(R.id.activity_join_event_banner_2);
        eventDescriptionTextView = findViewById(R.id.activity_join_event_event_description);
        eventDrawDateTextView = findViewById(R.id.join_event_draw_date);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize
        Database db = new Database();
        Button joinButton = findViewById(R.id.join_event_join_button);
        String event_id = "TEST2";

        // join button listener
        joinButton.setOnClickListener(v -> {
            // get current user (asynchronous)
            db.getCurrentUser(userId -> {
                // once userid is retrieved add participant
                String participantId = userId + event_id;
                String event = "event_id"; // to be replaced with the event id passed in thru intent from QR scanner
                String location = "[0° N, 0° E]"; // ?
                String status = "attending";

                // add user as a participant
                db.addParticipant(participantId, userId, event, location, status);
                startActivity(new Intent(EntrantJoinEventActivity.this, EntrantEventList.class));
            });
        });
    }



    private static void joinEvent(){
// TODO: Make it so that when the user presses the Join button they are added to the Firebase
    }

    public static void setCancelButtonNavigation(Button cancelButton, Context context) {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, qr_scanner.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public static void setJoinButtonNavigation(Button cancelButton, Context context) {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinEvent();
                Intent intent = new Intent(context, EntrantEventDescriptionWaitlistActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

}

