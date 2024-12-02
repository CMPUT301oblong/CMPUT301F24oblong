package com.example.oblong.organizer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Activity class for creating displaying an organizers events.
 *
 * <p>This activity retrieves a list of an organizer's events, using Firebase to fetch relevant data.
 * It then populates a ListView with these events for display.</p>
 */
public class organizer_my_events_fragment extends Fragment {

    private ListView eventList;
    private ArrayList<Event> eventsDataList;
    private OrganizerMyEventsArrayAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference organizersRef;
    private CollectionReference createdRef;
    private String user_id;

    /**
     * Inflates the fragment's layout.
     *
     * @param inflater The LayoutInflater object used to inflate views.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Bundle containing the fragment's previously saved state, if any.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_my_events, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView} has returned.
     *
     * <p>This method initializes Firebase Firestore references and sets up
     * the ListView adapter for displaying events. It also retrieves the current user's ID
     * to fetch the organizer's events.</p>
     *
     * @param view The View returned by {@link #onCreateView}.
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Initialize Relevant Firebase Firestore and collections
        db = FirebaseFirestore.getInstance();
        organizersRef = db.collection("organizers");
        createdRef = db.collection("created");

        eventList = view.findViewById(R.id.my_events_list); // Corrected line
       eventsDataList = new ArrayList<>();

        adapter = new OrganizerMyEventsArrayAdapter(getContext(), eventsDataList);
       eventList.setAdapter(adapter);

        // Obtain userID on fragment creation
        Database.getCurrentUser(user_id -> {
            if (user_id != null) {
                this.user_id = user_id;
                // Fetch events for the user
                fetchEvents(user_id);
            } else {
                Log.e("organizer_my_events_fragment", "Failed to retrieve user ID");
            }
        });


    }

    /**
     * Fetches the organizer's events from Firestore.
     *
     * <p>This method queries the "organizers" collection to find the user id to query the "created"
     * collection to check if the organizer has created any events. If the organizer has created any events,
     * then the eventsDataList is cleared of any previous data. For each event, a new Event object is created
     * and populated with the event information from Firebase, then the object is added to the eventDataList
     * and the adapter is notified.</p>
     */
    private void fetchEvents(String user_id) {
        // TODO: Fetch events the organizer has created
        organizersRef.whereEqualTo("user", user_id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->{
            if(!queryDocumentSnapshots.isEmpty()) {
                Log.d("Firestore", String.format("organizer's user: %s, facility: %s", user_id, queryDocumentSnapshots.getDocuments().get(0).get("facility")));
                createdRef.whereEqualTo("facility", queryDocumentSnapshots.getDocuments().get(0).get("facility"))
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots1 ->  {
                            if (queryDocumentSnapshots.isEmpty()) {
                                Log.e("Firestore", "There is no events");
                                return;
                            }
                            else{
                                eventsDataList.clear();
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots1) {
                                    String eventId = doc.getString("event");
                                    Log.d("Firestore", String.format("event(%s) fetched", eventId));
                                    Event event = new Event(eventId);
                                    db.collection("events")
                                            .document(eventId)
                                            .get()
                                            .addOnCompleteListener(task -> {
                                                DocumentSnapshot data = task.getResult();
                                                event.setEventName(data.getString("name"));
                                                event.setEventCloseDate(data.getDate("dateAndTime"));
                                                event.setPoster(data.getString("poster"));
                                                eventsDataList.add(event);
                                                adapter.notifyDataSetChanged();
                                            });

                                }

                            }
                        });
            }

        });
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     *
     * <p>This method fetches the organizer's events using their user id.</p>
     */
    @Override
    public void onResume() {
        super.onResume();
        fetchEvents(user_id);
    }
}