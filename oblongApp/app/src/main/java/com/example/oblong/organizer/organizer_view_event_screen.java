package com.example.oblong.organizer;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.entrant.EntrantEventDescriptionActivity;
import com.example.oblong.entrant.EntrantProfileEditActivity;
import com.example.oblong.imageUtils;
import com.example.oblong.qr_generator;
import com.example.oblong.Database;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
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
    private Button waitlistButton;
    private Button attendeesButton;
    private Button drawButton;
    private Button cancelButton;
    private ImageView uploadPosterButton;
    private String eventId;
    private String qrID;
    private final Database db = new Database();
    private FirebaseFirestore fdb;


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

        fdb = FirebaseFirestore.getInstance();
        eventNameDisplay = findViewById(R.id.organizer_view_event_name);
        uploadPosterButton = findViewById(R.id.activity_organizer_view_event_event_description_upload_icon);
        eventDescriptionDisplay = findViewById(R.id.activity_organizer_view_event_event_description_text);
        maxCapacityDisplay = findViewById(R.id.activity_organizer_view_event_event_description_max_capacity);
        backButton = findViewById(R.id.imageView2);
        qrCode = findViewById(R.id.activity_organizer_view_event_event_description_qr_code_display);
        poster = findViewById(R.id.activity_organizer_viewevent_event_description_poster);
        notificationButton = findViewById(R.id.activity_organizer_view_event_event_description_setup_notification_button);
        waitlistButton = findViewById(R.id.activity_organizer_view_event_event_description_view_waitlist_button);
        attendeesButton = findViewById(R.id.activity_organizer_view_event_event_description_view_attendees_button);
        drawButton = findViewById(R.id.draw_button);
        cancelButton = findViewById(R.id.cancel_entrants_button);


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

        waitlistButton.setOnClickListener(v -> {
            Intent intentWaitlist = new Intent(organizer_view_event_screen.this, EventWaitingList.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("EVENT", event);
            intentWaitlist.putExtras(bundle);

            startActivity(intentWaitlist);
        });

        attendeesButton.setOnClickListener(v -> {
            Intent intentAttendees = new Intent(organizer_view_event_screen.this, EventViewAttendees.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("EVENT", event);
            intentAttendees.putExtras(bundle);

            startActivity(intentAttendees);
        });

        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseParticipants(eventId);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEntrants(eventId);
            }
        });

        uploadPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForImage();
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
    private void cancelEntrants(String eventId) {

        // query for participants from specific event with either "selected" or "waitlisted" status
        fdb.collection("participants").whereEqualTo("event", eventId).whereIn("status", Arrays.asList( "selected", "waitlisted")).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String documentId = document.getId();

                    // Update the status to "cancelled"
                    fdb.collection("participants").document(documentId).update("status", "cancelled");
                }
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
        qrID = event.getQrID();
        qr_generator qr = new qr_generator();
        eventId = event.getEventID();
        Bitmap code = qr.generateQRCode(qrID);

        // https://stackoverflow.com/questions/30027242/set-bitmap-to-imageview
        qrCode.setImageBitmap(code);
    }

    private void askForImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a poster"), 200);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                Uri imageUri = data.getData();
                if (null != imageUri) {

                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                        String imageBase64 = imageUtils.bitmapToBase64(imageBitmap);
                        event.setPoster(imageBase64);

                        //Update database
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        database.collection("events").document(event.getEventID()).update("poster", imageBase64);
                        poster.setImageBitmap(imageUtils.base64ToBitmap(event.getPoster()));



                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private Context requireContext() {
        return getBaseContext();
    }

}


