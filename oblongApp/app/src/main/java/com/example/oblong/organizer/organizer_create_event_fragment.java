package com.example.oblong.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.oblong.Database;
import com.example.oblong.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class organizer_create_event_fragment extends Fragment {

    private EditText eventNameInput;
    private EditText eventDescriptionInput;
    private Button uploadImageButton;
    private EditText maxCapacityInput;
    private Button createEventButton;
    private Button cancelButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_organizer_create_event_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: Set up any views or logic here

        eventNameInput = view.findViewById(R.id.event_name_entry);
        eventDescriptionInput = view.findViewById(R.id.event_description_entry);
        uploadImageButton = view.findViewById(R.id.upload_button);
        maxCapacityInput = view.findViewById(R.id.capacity_dropdown);
        createEventButton = view.findViewById(R.id.create_event_button);
        cancelButton = view.findViewById(R.id.event_creation_cancel_button);

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get all info user entered:
                String eventName = eventNameInput.getText().toString();
                String eventDescription = eventDescriptionInput.getText().toString();
                Long maxCapacity = Long.parseLong(maxCapacityInput.getText().toString());


                //When we click this we want to take all this info and put it into database
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> event = new HashMap<>();

                event.put("capacity", maxCapacity);
                event.put("dateAndTime", FieldValue.serverTimestamp());
                event.put("description", eventDescription);
                event.put("drawDate", FieldValue.serverTimestamp());
                event.put("location", new GeoPoint(0,0));
                event.put("name", eventName);
                event.put("poster", "image");

                db.collection("events").add(event).addOnSuccessListener(documentReference -> {
                    String eventID = documentReference.getId();

                //Now we want to put the organizer in a created relation with this event
                    Database.getCurrentUser(new Database.OnDataReceivedListener<String>() {
                        @Override
                        public void onDataReceived(String data) {
                            db.collection("organizers").whereEqualTo("user",data).get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                       String organizerFacility = queryDocumentSnapshots.getDocuments().get(0).getString("facility");

                                       Map<String, Object> eventCreation = new HashMap<>();
                                       eventCreation.put("event", eventID);
                                       eventCreation.put("facility", organizerFacility);

                                       db.collection("created").add(eventCreation);
                                    });
                        }
                    });
                });

            }
        });

    }
}