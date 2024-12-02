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

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Database myDb;


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


        myDb = new Database();
        db = FirebaseFirestore.getInstance();

        //Intent intent = getIntent();

        Bundle bundle = getIntent().getExtras();
        Event event = (Event) bundle.get("EVENT");
        if (event != null) {
            eventID = event.getEventID();
            event_name = event.getEventName();
            combined_name = event_name + " Waitlist";
            eventName.setText(combined_name);
        } else {
            Log.d("EventWaitingList", "Event object is null");
        }

        fetchWaitlistedEntrants();


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    // fetches all entrants that are signed up for the event
    private void fetchWaitlistedEntrants() {
        // check participants are a part of event and their status is waitlisted
        db.collection("participants").whereEqualTo("event", eventID).whereEqualTo("status", "waitlisted").get().addOnCompleteListener(task -> {
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
                                waitListedEntrants.add(entrantName);
                                waitListAdapter.notifyDataSetChanged();

                            }
                        }else{
                            Log.d("a", "AWFAWFAWFAWFAWFA: USER does NOT EXIST");
                        }
                    });

                }
            } else {
                Log.d("EventWaitingList", "Couldn't fetch waitlisted entrants");
            }
                });
    }
}
