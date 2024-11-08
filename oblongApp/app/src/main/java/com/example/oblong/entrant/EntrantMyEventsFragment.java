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
import java.util.HashSet;
import java.util.Set;

public class EntrantMyEventsFragment extends Fragment {

    private ListView eventList;
    private ArrayList<Event> eventsDataList;
    private EntrantMyEventsArrayAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference participantsRef;
    private CollectionReference eventsRef;
    private String user_id;
    private ListenerRegistration participantListener;

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

        eventList = view.findViewById(R.id.my_events_list);
        eventsDataList = new ArrayList<>();
        adapter = new EntrantMyEventsArrayAdapter(getContext(), eventsDataList);
        eventList.setAdapter(adapter);

        Database.getCurrentUser(user_id -> {
            if (user_id != null) {
                this.user_id = user_id;
                attachParticipantListener(); // Attach listener for real-time updates
            } else {
                Log.e("EntrantMyEventsFragment", "Failed to retrieve user ID");
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
        // Ensure only one listener is active
        if (participantListener != null) {
            participantListener.remove();
        }

        participantListener = participantsRef.whereEqualTo("entrant", user_id)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("EntrantMyEventsFragment", "Error listening for updates", e);
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

            if (eventId != null && eventIds.add(eventId) && "attending".equals(status)) {
                eventsRef.document(eventId).get().addOnSuccessListener(eventDocumentSnapshot -> {
                    if (eventDocumentSnapshot.exists()) {
                        Event event = new Event(eventDocumentSnapshot.getId());
                        event.setEventName(eventDocumentSnapshot.getString("name"));
                        event.setEventCloseDate(eventDocumentSnapshot.getDate("dateAndTime"));

                        eventsDataList.add(event);
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(e -> Log.e("EntrantMyEventsFragment", "Error fetching event document", e));
            }
        }

        adapter.notifyDataSetChanged();
    }
}
