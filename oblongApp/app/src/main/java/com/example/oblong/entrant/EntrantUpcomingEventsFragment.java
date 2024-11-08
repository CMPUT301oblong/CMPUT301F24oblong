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

/**
 * {@code EntrantUpcomingEventsFragment} This class handles the upcoming events screen for the entrant
 * which displays all the upcoming events for the current user.
 */
public class EntrantUpcomingEventsFragment extends Fragment {

    private ListView eventList;
    private ArrayList<Event> eventsDataList;
    private EntrantAllEventsArrayAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    /**
     * {@code onCreateView} is called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return inflater turns the layout inta a view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_entrant_event_list, container, false);
    }

    /**
     * {@code onViewCreated} is called after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * Calls events that the user is a part of from Firebase.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        eventList = view.findViewById(R.id.activity_entrant_all_events_list); // Corrected line
        eventsDataList = new ArrayList<>();

        adapter = new EntrantAllEventsArrayAdapter(getContext(), eventsDataList);
        eventList.setAdapter(adapter);

        // Fetch Items from Firebase
        fetchEvents();
    }

    /**
     * {fetchEvents} is called to fetch events from Firebase.
     */
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
