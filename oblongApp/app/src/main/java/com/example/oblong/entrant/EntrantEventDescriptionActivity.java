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

public class EntrantEventDescriptionActivity extends AppCompatActivity {

    private Event event;
    private TextView eventNameTextView;
    private ImageView eventImageView;
    private TextView eventDescriptionTextView;
    private ImageButton backButton;
    private Button leaveButton;



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

    private void initializeData(Intent intent){
        Bundle bundle = getIntent().getExtras();
        event = (Event) bundle.get("EVENT");
        eventNameTextView.setText(event.getEventName());
        eventDescriptionTextView.setText(event.getEventDescription());
    }

    private void userLeft(){
        //TO DO
        //remove participant relation from database
        //kick user back to their events

        finish();
    }

}