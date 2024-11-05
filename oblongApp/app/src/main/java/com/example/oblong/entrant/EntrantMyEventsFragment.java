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

import com.example.oblong.Event;
import com.example.oblong.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EntrantMyEventsFragment extends Fragment {

    private ListView eventList;
    private ArrayList<Event> eventsDataList;
    private EntrantEventArrayAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_entrant_my_events, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        eventList = view.findViewById(R.id.activity_entrant_my_events_list); // Corrected line
        eventsDataList = new ArrayList<>();

        adapter = new EntrantEventArrayAdapter(getContext(), eventsDataList);
        eventList.setAdapter(adapter);

        // Fetch Items from Firebase
        fetchEvents();
    }

    private void fetchEvents() {
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventsDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String eventId = doc.getId();
                        String eventName = doc.getString("name");
                        Log.d("Firestore", String.format("event(%s, %s) fetched", eventId, eventName));
                        Event event = new Event(eventId);
                        event.setEventName(eventName);
                        event.setEventCloseDate(doc.getDate("dateAndTime"));;
                        eventsDataList.add(event);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}