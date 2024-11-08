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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EntrantUpcomingEventsFragment extends Fragment {

    private ListView eventList;
    private ArrayList<Event> eventsDataList;
    private EntrantAllEventsArrayAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference participantsRef;
    private CollectionReference eventsRef;
    private String user_id;
    private ListenerRegistration participantListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_entrant_event_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        participantsRef = db.collection("participants");
        eventsRef = db.collection("events");

        eventList = view.findViewById(R.id.activity_entrant_all_events_list);
        eventsDataList = new ArrayList<>();
        adapter = new EntrantAllEventsArrayAdapter(getContext(), eventsDataList);
        eventList.setAdapter(adapter);

        Database.getCurrentUser(user_id -> {
            if (user_id != null) {
                this.user_id = user_id;
                attachParticipantListener();
            } else {
                Log.e("EntrantUpcomingEventsFragment", "Failed to retrieve user ID");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (participantListener != null) {
            participantListener.remove();
        }
    }

    private void attachParticipantListener() {
        if (participantListener != null) {
            participantListener.remove();
        }

        participantListener = participantsRef.whereEqualTo("entrant", user_id)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("EntrantUpcomingEventsFragment", "Error listening for updates", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        updateEventList(queryDocumentSnapshots);
                    }
                });
    }

    private void updateEventList(QuerySnapshot queryDocumentSnapshots) {
        Set<String> eventIds = new HashSet<>();
        eventsDataList.clear(); // Clear the list before adding events that match criteria

        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
            String eventId = doc.getString("event");
            String status = doc.getString("status");

            if (eventId != null && eventIds.add(eventId) && Arrays.asList("selected", "waitlisted").contains(status)) {
                eventsRef.document(eventId).get().addOnSuccessListener(eventDocumentSnapshot -> {
                    if (eventDocumentSnapshot.exists()) {
                        Event event = new Event(eventDocumentSnapshot.getId());
                        event.setEventName(eventDocumentSnapshot.getString("name"));
                        event.setEventCloseDate(eventDocumentSnapshot.getDate("dateAndTime"));
                        event.setStatus(status);

                        eventsDataList.add(event);
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(e -> Log.e("EntrantUpcomingEventsFragment", "Error fetching event document", e));
            }
        }

        adapter.notifyDataSetChanged();
    }
}
