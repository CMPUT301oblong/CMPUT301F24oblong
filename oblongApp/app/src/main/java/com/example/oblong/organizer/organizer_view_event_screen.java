package com.example.oblong.organizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.oblong.entrant.EntrantEventDescriptionActivity;
import com.example.oblong.imageUtils;
import com.example.oblong.qr_generator;
import com.example.oblong.Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class organizer_view_event_screen extends AppCompatActivity {

    private TextView eventNameDisplay;
    private TextView eventDescriptionDisplay;
    private TextView maxCapacityDisplay;
    private ImageView backButton;
    private ImageView qrCode;
    private ImageView poster;
    private Event event;
    private Button notificationButton;
    private Button drawButton;
    private String eventId;
    private final Database db = new Database();

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
        backButton = findViewById(R.id.imageView2);
        qrCode = findViewById(R.id.activity_organizer_view_event_event_description_qr_code_display);
        poster = findViewById(R.id.activity_organizer_viewevent_event_description_poster);
        notificationButton = findViewById(R.id.activity_organizer_view_event_event_description_setup_notification_button);
        drawButton = findViewById(R.id.draw_button);

        Intent intent = getIntent();


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

        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseParticipants(eventId);
            }
        });

    }

    private void chooseParticipants(String eventId){
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("event", eventId);
        conditions.put("status", "waitlisted");
        List<String> entrantPool = new ArrayList<String>();
        db.query("participants", conditions, results ->{
            for(int i = 0; i < results.size(); i++){
                // get all entrants that signed up for event
                entrantPool.add((String)results.get(i).get("entrant"));
            }
            List<String> selectedParticipants =  entrantPool;
            if(event.getEventCapacity() < entrantPool.size()){
                // choose random participants if pool is larger than capacity
                Collections.shuffle(entrantPool);
                selectedParticipants = entrantPool.subList(0, event.getEventCapacity());
            }
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("status", "selected");
            for(int i = 0; i < selectedParticipants.size(); i++) {
                // set each participant's status as selected
                db.updateDocument("participants", selectedParticipants.get(i)+eventId, updates, v->{});
            }
        });
    }

    private void initializeData(Intent intent){
        Bundle bundle = getIntent().getExtras();
        event = (Event) bundle.get("EVENT");
        eventNameDisplay.setText(event.getEventName());
        eventDescriptionDisplay.setText(event.getEventDescription());
        maxCapacityDisplay.setText(Long.toString(event.getEventCapacity()));
        poster.setImageBitmap(imageUtils.base64ToBitmap(event.getPoster()));
        qr_generator qr = new qr_generator();
        eventId = event.getEventID();
        Bitmap code = qr.generateQRCode(eventId);

        // https://stackoverflow.com/questions/30027242/set-bitmap-to-imageview
        qrCode.setImageBitmap(code);
    }
}


