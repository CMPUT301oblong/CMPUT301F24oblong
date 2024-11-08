package com.example.oblong;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * {@code EntrantUpcomingEventActivity} This class handles the upcoming event activity screen for entrants
 */
public class EntrantUpcomingEventActivity extends AppCompatActivity {

    private ArrayList<Event> datalist = new ArrayList<Event>();
    private EventArrayAdapter eventAdapter;

    /**
     * The {@code onCreate} method handles the creation of the activity
     * and initiates the Firebase.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_upcoming_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Event testEvent = new Event("0");
        //datalist.add(testEvent);

        EventArrayAdapter eventAdapter = new EventArrayAdapter(this, datalist);

        ListView eventListView = findViewById(R.id.entrant_upcoming_event_list);
        Button resetList = findViewById(R.id.reset_list);

        resetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventAdapter.notifyDataSetChanged();
            }
        });



        eventListView.setAdapter(eventAdapter);

        //get the database
        Database db = new Database();
        getAllEvents(db);



    }

    /**
     * The {@code getAllEvents} method gets all the events that the user is a participant for from
     * Firebase
     * @param db
     */
    private void getAllEvents(Database db){
        //This statement queries for all eventIDs that the user is a participant for.
        db.getCurrentUser(new Database.OnDataReceivedListener<String>() {
            @Override
            public void onDataReceived(String data) {
                HashMap<String, Object> conditions = new HashMap<>();
                conditions.put("entrant", data);
                db.query("participants", conditions, Participation -> {
                    //
                    if(Participation != null){
                        for(int i = 0; i<Participation.size(); i++){
                            datalist.add(new Event((String)Participation.get(i).get("event"))); //This will give us the all the event ids that user has.
                        }
                        //
                    }});
            }

        });
    }
}