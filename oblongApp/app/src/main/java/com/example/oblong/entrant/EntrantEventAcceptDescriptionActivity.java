package com.example.oblong.entrant;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.oblong.Event;
import com.example.oblong.R;

public class EntrantEventAcceptDescriptionActivity extends AppCompatActivity {
    private Event event;
    private TextView eventNameTextView;
    private ImageView eventImageView;
    private TextView eventDescriptionTextView;
    private Button acceptButton;
    private Button cancelButton;

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
    }
}
