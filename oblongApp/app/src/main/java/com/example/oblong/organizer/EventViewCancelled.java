package com.example.oblong.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EventViewCancelled extends AppCompatActivity{
    private String eventID;
    private TextView eventName;
    private FirebaseFirestore db;
    private ListView attendeesList;
    private ImageButton cancelButton;
    private ArrayList<Map<String, String>> attendees;
    private AttendeesArrayAdapter attendeesAdapter;
    private String event_name;
    private String combined_name;
    private Database myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_entrant_attendees);
        cancelButton = findViewById(R.id.organizer_entrant_attendees_cancel_button);
        attendeesList = findViewById(R.id.event_attendees_event_list_view);
        attendees = new ArrayList<>();
        attendeesAdapter = new AttendeesArrayAdapter(this, attendees);
        attendeesList.setAdapter(attendeesAdapter);
        eventName = findViewById(R.id.event_attendees_event_name);


        db = FirebaseFirestore.getInstance();
        myDb = new Database();

        Bundle bundle = getIntent().getExtras();
        Event event = (Event) bundle.get("EVENT");

        if (event != null) {
            eventID = event.getEventID();
            event_name = event.getEventName();
            combined_name = event_name + " Cancelled";
            eventName.setText(combined_name);
        } else {
            Log.d("EventViewCancelled", "Event object is null");
        }

        fetchAttendees();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void fetchAttendees() {
        // check participants are a part of event and their status is selected or accepted
        db.collection("participants").whereEqualTo("event", eventID).whereIn("status", Arrays.asList("cancelled", "canceled")).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String entrantId = document.getString("entrant");
                    String status = document.getString("status");


                    myDb.getUser(entrantId, participantResult->{
                        if (participantResult != null) {
                            String entrantName = (String) participantResult.get("name");
                            if (entrantName != null && status != null) {
                                Map<String, String> attendee = new HashMap<>();
                                attendee.put("name", entrantName);
                                attendee.put("status", status);
                                attendees.add(attendee);
                                attendeesAdapter.notifyDataSetChanged();

                            }
                        }else{
                            Log.d("a", "AWFAWFAWFAWFAWFA: USER does NOT EXIST");
                        }
                    });

                }
            } else {
                Log.d("EventViewAttendees", "Couldn't fetch attendees");
            }
        });
    }
}
