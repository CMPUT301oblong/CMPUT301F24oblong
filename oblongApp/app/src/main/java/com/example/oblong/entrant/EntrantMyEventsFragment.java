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


/**
 * Fragment class for displaying the events that the user is participating in.
 *
 * <p>This fragment retrieves a list of events where the user is marked as attending,
 * using Firebase to fetch relevant data. It then populates a list view
 * with these events for display.</p>
 */
public class EntrantMyEventsFragment extends Fragment {

    private ListView eventList;
    private ArrayList<Event> eventsDataList;
    private EntrantMyEventsArrayAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference participantsRef;
    private CollectionReference eventsRef;
    private String user_id;
    private ListenerRegistration participantListener;

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
     * and starts fetching the user's participating events using a snapshot listener.</p>
     *
     * @param view The View returned by {@link #onCreateView}.
     * @param savedInstanceState The saved instance state bundle.
     */
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

    /**
   * Cleans up resources when the view is destroyed.
   *
   * <p>This method removes the Firestore snapshot listener to stop listening for 
   * participant updates when the fragment's view is destroyed.</p>
   */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (participantListener != null) {
            participantListener.remove();
        }
    }

    /**
   * Attaches a Firestore snapshot listener to the "participants" collection to track real-time updates.
   *
   * <p>This method listens for changes in the participant data where the "entrant" field matches
   * the current user's ID. On any change in the participant documents, it triggers a callback to 
   * fetch the latest list of events the user is attending, ensuring the displayed event list is 
   * always up-to-date.</p>
   *
   * <p>To prevent multiple active listeners, it first checks if an existing listener is attached
   * and removes it if necessary.</p>
   */
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

    /**
     * Fetches events from Firestore where the current entrants status is marked as "attending".
     *
     * <p>This method queries the "participants" collection to find documents where the
     * entrant field matches the current user ID and the status is "attending". For each
     * participant document, it fetches the corresponding event document from the "events"
     * collection and adds it to the list of events to be displayed.</p>
     */
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
