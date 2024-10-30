package com.example.oblong;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    }

    private void populateEventData() {

        String eventName = getIntent().getStringExtra("event name");
        String eventDescription = getIntent().getStringExtra("event description");
        String drawDate = getIntent().getStringExtra("event draw date");

        eventNameTextView.setText(eventName);
        eventDescriptionTextView.setText(eventDescription);
        eventDrawDateTextView.setText("Draw Date: " + drawDate);
    }
}
