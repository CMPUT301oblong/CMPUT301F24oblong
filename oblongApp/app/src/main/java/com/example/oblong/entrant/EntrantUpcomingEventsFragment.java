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

/**
 * {@code EntrantUpcomingEventsFragment} This class handles the upcoming events screen for the entrant
 * which displays all the upcoming events for the current user.
 */
public class EntrantUpcomingEventsFragment extends Fragment {

    private ListView eventList;
    private ArrayList<Event> eventsDataList;
    private EntrantAllEventsArrayAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference participantsRef;
    private CollectionReference eventsRef;
    private String user_id;
    private ListenerRegistration participantListener;

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

    /**
     * Fetches events from Firestore where the current entrants status is marked as "selected" or "waitlisted".
     *
     * <p>This method queries the "participants" collection to find documents where the
     * entrant field matches the current user ID and the status is "selected" or "waitlisted". For each
     * participant document, it fetches the corresponding event document from the "events"
     * collection and adds it to the list of events to be displayed.</p>
     */
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
