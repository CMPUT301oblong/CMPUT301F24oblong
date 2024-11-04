package com.example.oblong;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Date;

public class EntrantUpcomingEventActivity extends AppCompatActivity {

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

        //Testing the list view
        Date date = new Date();
        Event testEvent = new Event("testEvent", "null", date, 1);

        ArrayList<Event> datalist = new ArrayList<Event>();
        datalist.add(testEvent);

        ListView eventListView = findViewById(R.id.entrant_upcoming_event_list);

        EventArrayAdapter eventAdapter = new EventArrayAdapter(this, datalist);

        eventListView.setAdapter(eventAdapter);



        //

    }
}