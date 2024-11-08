package com.example.oblong.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oblong.Event;
import com.example.oblong.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EventWaitingList extends AppCompatActivity {
    private String eventID;
    private TextView eventName;
    private FirebaseFirestore db;
    private ListView waitList;
    private ImageButton cancelButton;
    private ArrayList<String> waitListedEntrants;
    private ArrayAdapter<String> waitListAdapter;
    private String event_name;
    private String combined_name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_entrant_waitlist);
        cancelButton = findViewById(R.id.organizer_entrant_waitlist_cancel_button);
        waitList = findViewById(R.id.waitlist_list_view);
        waitListedEntrants = new ArrayList<>();
        waitListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, waitListedEntrants);
        waitList.setAdapter(waitListAdapter);
        eventName = findViewById(R.id.event_waitlist_event_name);
        combined_name = event_name + "Waitlist";

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        getEventID();
        getEventName();

        if (eventID != null){
            eventName.setText(combined_name);
        }
        else{
            Log.d("EventWaitingList", "No event found");
        }

        fetchWaitlistedEntrants();


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //gets event id from bundle
    private void getEventID() {
        Bundle bundle = getIntent().getExtras();
        Event event = (Event) bundle.get("EVENT");
        eventID = event.getEventID();
    }
    //gets event name from bundle
    private void getEventName() {
        Bundle bundle = getIntent().getExtras();
        Event event = (Event) bundle.get("EVENT");
        event_name = event.getEventName();
    }

    // fetches all entrants that are signed up for the event
    private void fetchWaitlistedEntrants() {
        // check participants are a part of event and their status is waitlisted
        db.collection("participants").whereEqualTo("event", eventID).whereEqualTo("status", "waitlisted").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String entrantName = document.getString("entrant");
                    if (entrantName != null) {
                        waitListedEntrants.add(entrantName);
                    }
                }
                waitListAdapter.notifyDataSetChanged();
            } else {
                Log.d("EventWaitingList", "Couldn't fetch waitlisted entrants");
            }
                });
    }
}
