package com.example.oblong.organizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.entrant.EntrantEventDescriptionActivity;
import com.example.oblong.qr_generator;

public class organizer_view_event_screen extends AppCompatActivity {

    private TextView eventNameDisplay;
    private TextView eventDescriptionDisplay;
    private TextView maxCapacityDisplay;
    private ImageView backButton;
    private Event event;
    private Button notificationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_view_event_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventNameDisplay = findViewById(R.id.organizer_view_event_name);
        eventDescriptionDisplay = findViewById(R.id.activity_organizer_view_event_event_description_text);
        maxCapacityDisplay = findViewById(R.id.activity_organizer_view_event_event_description_max_capacity);
        backButton=findViewById(R.id.imageView2);
        notificationButton = findViewById(R.id.activity_organizer_view_event_event_description_setup_notification_button);

        qr_generator qr = new qr_generator();
        Intent intent = getIntent();
        Bitmap code = qr.generateQRCode(intent.getStringExtra("event"));
        ImageView qrCode = findViewById(R.id.activity_organizer_view_event_event_description_qr_code_display);

        // https://stackoverflow.com/questions/30027242/set-bitmap-to-imageview
        qrCode.setImageBitmap(code);

        initializeData(intent);

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(organizer_view_event_screen.this, organizer_create_notification_activity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("EVENT", event);
                intentNotification.putExtras(bundle);

                startActivity(intentNotification);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initializeData(Intent intent){
        Bundle bundle = getIntent().getExtras();
        event = (Event) bundle.get("EVENT");
        eventNameDisplay.setText(event.getEventName());
        eventDescriptionDisplay.setText(event.getEventDescription());
        maxCapacityDisplay.setText(Long.toString(event.getEventCapacity()));
    }
}


