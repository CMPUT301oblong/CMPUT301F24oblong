package com.example.oblong.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
 * The {@code AdminEventBrowserFragment} class is responsible for displaying a list of events
 * in the admin interface. It fetches event data from a Firebase Firestore database and displays
 * it in a {@link ListView}. Administrators can click on individual events to view detailed
 * information about them in a new activity.
 * <p>
 * This fragment uses a custom adapter {@link AdminUserEventArrayAdapter} to bind the event data
 * to the list view and implements a Firebase snapshot listener to update the event list
 * dynamically when changes occur in the database.
 * </p>
 */
public class AdminEventBrowserFragment extends Fragment {

    private ListView eventList;
    private ArrayList<Object> eventDataList;
    private AdminUserEventArrayAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           The LayoutInflater used to inflate the fragment's view.
     * @param container          The parent view that this fragment will be attached to.
     * @param savedInstanceState Saved state from a previous instance of this fragment (if any).
     * @return The inflated View for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_event_browser, container, false);
    }

    /**
     * Called after the fragment's view has been created. Sets up the ListView, initializes
     * Firebase components, and starts listening for changes in the "events" collection.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState Saved state from a previous instance of this fragment (if any).
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        eventList = view.findViewById(R.id.admin_event_browser_list); // Corrected line
        TextView titleText = view.findViewById(R.id.admin_event_browser_title);
        titleText.setText("Event Browser");
        eventDataList = new ArrayList<>();

//        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, eventDataList);
        adapter = new AdminUserEventArrayAdapter(getContext(), eventDataList);
        eventList.setAdapter(adapter);

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                                             {
                                                 // Handle item click event
                                                 Log.d("AdminEventBrowserFragment", "Item clicked: " + ((Event) eventDataList.get(i)).getEventID());
                                                 Event event = (Event) eventDataList.get(i);
                                                 Intent intent = new Intent(getActivity(), AdminEventView.class);
                                                 Bundle bundle = new Bundle();
                                                 bundle.putSerializable("event", event);
                                                 intent.putExtras(bundle);

                                                 startActivity(intent);
                                             }
                                         });


        // Fetch Items from Firebase
        fetchEvents();
    }
    /**
     * Fetches event data from the "events" collection in the database. Updates the event list
     * dynamically when changes occur in the database.
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
                    eventDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        try {
                            String eventId = doc.getId();
                            String eventName = doc.getString("name");
                            Log.d("Firestore", String.format("event(%s, %s) fetched", eventId, eventName));
                            Event event = new Event(eventId);
                            event.setEventName(eventName);
                            event.setEventCloseDate(doc.getDate("dateAndTime"));
                            eventDataList.add(event);
                        } catch (Exception e) {
                            ;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}