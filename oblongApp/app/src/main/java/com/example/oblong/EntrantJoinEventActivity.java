package com.example.oblong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
// TODO: Add entrants to database if they join the event

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
        populateEventData();

        Button cancelButton = findViewById(R.id.join_event_cancel_button);
        setCancelButtonNavigation(cancelButton, this);

        Button joinButton = findViewById(R.id.join_event_join_button);
        setJoinButtonNavigation(joinButton,this);
    }

    private void populateEventData() {

        String eventName = getIntent().getStringExtra("event name");
        String eventDescription = getIntent().getStringExtra("event description");
        String drawDate = getIntent().getStringExtra("event draw date");

        eventNameTextView.setText(eventName);
        eventDescriptionTextView.setText(eventDescription);
        eventDrawDateTextView.setText("Draw Date: " + drawDate);
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

