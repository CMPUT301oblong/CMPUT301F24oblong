package com.example.oblong.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.entrant.EntrantMyEventsArrayAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class organizer_my_events_fragment extends Fragment {

    private ListView eventList;
    private ArrayList<Event> eventsDataList;
    private EntrantMyEventsArrayAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference participantsRef;
    private CollectionReference eventsRef;
    private String user_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_my_events, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Initialize Relevant Firebase Firestore and collections
//        db = FirebaseFirestore.getInstance();
//        participantsRef = db.collection("participants");
//        eventsRef = db.collection("events");
//
//        eventList = view.findViewById(R.id.my_events_list); // Corrected line
//        eventsDataList = new ArrayList<>();
//
//        adapter = new EntrantMyEventsArrayAdapter(getContext(), eventsDataList);
//        eventList.setAdapter(adapter);

        // Obtain userID on fragment creation
        Database.getCurrentUser(user_id -> {
            if (user_id != null) {
                this.user_id = user_id;
                // Fetch events for the user
                fetchEvents();
            } else {
                Log.e("organizer_my_events_fragment", "Failed to retrieve user ID");
            }
        });
    }

    private void fetchEvents() {
        // TODO: Fetch events the organizer has created
    }

}