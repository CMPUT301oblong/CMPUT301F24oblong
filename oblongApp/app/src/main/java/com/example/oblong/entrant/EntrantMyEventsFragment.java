package com.example.oblong.entrant;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EntrantMyEventsFragment extends Fragment {

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

        db = FirebaseFirestore.getInstance();
        participantsRef = db.collection("participants");
        eventsRef = db.collection("events");

        eventList = view.findViewById(R.id.my_events_list); // Corrected line
        eventsDataList = new ArrayList<>();

        adapter = new EntrantMyEventsArrayAdapter(getContext(), eventsDataList);
        eventList.setAdapter(adapter);

        // Obtain userID on fragment creation
        Database.getCurrentUser(user_id -> {
            if (user_id != null) {
                this.user_id = user_id;
                // Fetch events for the user
                fetchEvents();
            } else {
                Log.e("EntrantMyEventsFragment", "Failed to retrieve user ID");
            }
        });
    }

    private void fetchEvents() {
        // Fetch events where the user is a participant
        participantsRef.whereEqualTo("entrant", user_id).whereEqualTo("status", "attending").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                Log.d("EntrantMyEventsFragment", String.format("Searching for documents for %s", user_id));
                eventsDataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String eventId = doc.getString("event");
                    if (eventId != null) {  // Check if the event field is not null
                        Log.d("EntrantMyEventsFragment", String.format("Found Event ID: %s", eventId));

                        // Fetch the event document
                        eventsRef.document(eventId).get().addOnSuccessListener(eventDocumentSnapshot -> {
                            if (eventDocumentSnapshot.exists()) {
                                Event event = new Event(eventDocumentSnapshot.getId());
                                event.setEventName(eventDocumentSnapshot.getString("name"));
                                event.setEventCloseDate(eventDocumentSnapshot.getDate("dateAndTime"));
                                eventsDataList.add(event);
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e("EntrantMyEventsFragment", "Event document not found for ID: " + eventId);
                            }
                        }).addOnFailureListener(e -> Log.e("EntrantMyEventsFragment", "Error fetching event document", e));
                    } else {
                        Log.e("EntrantMyEventsFragment", "Event ID is null in participant document: " + doc.getId());
                    }
                }
            }
        }).addOnFailureListener(e -> Log.e("EntrantMyEventsFragment", "Error fetching participants", e));
    }

}