package com.example.oblong.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.imageUtils;
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
public class EntrantEventBrowser extends Fragment {

    // My events list
    private ListView verticalList;
    private ArrayList<Event> myEventsDataList;
    private EntrantVerticalEventsArrayAdapter myEventsAdapter;

    // All events list
    private HorizontalScrollView horizontalList;
    private LinearLayout horizontalListLinearLayout;
    private ArrayList<Event> allEventsDataList;


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
        return inflater.inflate(R.layout.event_browser_fragment, container, false);
    }


    /**
     * Called immediately after {@link #onCreateView} has returned.
     *
     * <p>This method initializes Firebase Firestore references and sets up
     * the ListView myEventsAdapter for displaying events. It also retrieves the current user's ID
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

        verticalList = view.findViewById(R.id.verticalList);
        myEventsDataList = new ArrayList<>();
        myEventsAdapter = new EntrantVerticalEventsArrayAdapter(this.getContext(), myEventsDataList);
        verticalList.setAdapter(myEventsAdapter);

        horizontalList = view.findViewById(R.id.horizontalList);
        horizontalListLinearLayout = view.findViewById(R.id.horizontalListLinearLayout);
        allEventsDataList = new ArrayList<>();

        TextView interestedEventsViewAll = view.findViewById(R.id.interestedEventsViewAll);
        interestedEventsViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EntrantUpcomingEventsFragment.class);
            startActivity(intent);
        });
        TextView attendingEventsViewAll = view.findViewById(R.id.attendingEventsViewAll);
        attendingEventsViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EntrantMyEventsFragment.class);
            startActivity(intent);
        });


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
                        populateHorizontalList(allEventsDataList);
                        horizontalListLinearLayout.invalidate();
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
        myEventsDataList.clear(); // Clear the list before adding events that match criteria
        allEventsDataList.clear();

        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
            String eventId = doc.getString("event");
            String status = doc.getString("status");

            if(eventId != null && eventIds.add(eventId)) {
                if ("attending".equals(status)) {
                    eventsRef.document(eventId).get().addOnSuccessListener(eventDocumentSnapshot -> {
                        if (eventDocumentSnapshot.exists()) {
                            Event event = new Event(eventDocumentSnapshot.getId());
                            event.setEventName(eventDocumentSnapshot.getString("name"));
                            event.setEventCloseDate(eventDocumentSnapshot.getDate("dateAndTime"));
                            event.setPoster(eventDocumentSnapshot.getString("poster"));
                            myEventsDataList.add(event);
                            myEventsAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(e -> Log.e("EntrantMyEventsFragment", "Error fetching event document", e));
                }
                if (("selected".equals(status)) || ("waitlisted".equals(status))) {
                    eventsRef.document(eventId).get().addOnSuccessListener(eventDocumentSnapshot -> {
                        if (eventDocumentSnapshot.exists()) {
                            Event event = new Event(eventDocumentSnapshot.getId());
                            event.setEventName(eventDocumentSnapshot.getString("name"));
                            event.setEventCloseDate(eventDocumentSnapshot.getDate("dateAndTime"));
                            event.setPoster(eventDocumentSnapshot.getString("poster"));
                            event.setStatus(status);
                            allEventsDataList.add(event);

                            populateHorizontalList(allEventsDataList);
                            horizontalListLinearLayout.invalidate();
                        }
                    });
                }
            }
        }

        myEventsAdapter.notifyDataSetChanged();
    }

    private void populateHorizontalList(ArrayList<Event> events) {
        horizontalListLinearLayout.removeAllViews(); // Clear existing views

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (Event event : events) {
            // Inflate a custom item layout for the horizontal list
            View itemView = inflater.inflate(R.layout.entrant_horizontal_event_item, horizontalListLinearLayout, false);

            // Set up the view elements
            TextView eventName = itemView.findViewById(R.id.horizontalEventName);
            ImageView eventPoster = itemView.findViewById(R.id.eventPoster);
            TextView drawDate = itemView.findViewById(R.id.drawDate);
            TextView drawTime = itemView.findViewById(R.id.drawTime);
            TextView statusTextView = itemView.findViewById(R.id.entranteventstatus);

            eventName.setText(event.getEventName());
            if(event.getPoster() != null) {
                eventPoster.setImageBitmap(imageUtils.base64ToBitmap(event.getPoster()));
            }
            drawDate.setText(event.getEventCloseMon());
            drawTime.setText(event.getEventCloseDay());
            statusTextView.setText(event.getStatus());


            itemView.setOnClickListener(v -> {
                // Handle click event, e.g., navigate to event details
                if(event.getStatus().contains("waitlisted")) {
                    Intent intent = new Intent(getContext(), EntrantEventDescriptionActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("EVENT", event);
                    intent.putExtras(bundle);

                    startActivity(intent);

                    Log.d("button", String.format("%s button clicked", event.getEventName()));
                }
                else if(event.getStatus().contains("selected")) {

                    Intent intent = new Intent(getContext(), EntrantEventAcceptDescriptionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("EVENT", event);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            });

            // Add the view to the LinearLayout
            horizontalListLinearLayout.addView(itemView);
        }
    }

}
