package com.example.oblong.organizer;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.inputValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity class for creating custom notifications to send to certain participants associated with an event.
 *
 * <p>This activity retrieves a list of participants to an event where their status is marked as "waitlisted",
 * "selected", "attending", or "cancelled", using Firebase to fetch relevant data. When the user clicks to send the
 * notification, the text input is validated, a participant list is populated, a notification is added to the
 * database, and the notification id is added to each participant's entrant data in the database</p>
 */
public class organizer_create_notification_activity extends AppCompatActivity {
    private String eventID;
    private Event eventData;
    private ArrayList<Map<String, Object>> participantDocs = new ArrayList<Map<String, Object>>();
    private ArrayList<String> participantList = new ArrayList<String>();
    private FirebaseFirestore fdb;
    private CollectionReference notifications = FirebaseFirestore.getInstance().collection("notifications");
    private Database db = new Database();

    /**
     * Inflates the activity's layout.
     *
     *<p>This method {@code onCreate} initializes Firebase Firestore references and sets up the targetSpinnerAdapter
     * for displaying participant status options. It calls methods for fetching the eventID and participant data
     * and sets button listeners. It takes user input and stores the notification in Firebase. The notification id
     * is sent to each participant notificationsList</p>
     *
     * @param savedInstanceState Bundle containing the fragment's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_create_notification);

        EditText newLabelText = findViewById(R.id.organizer_new_notification_label_inputText);
        EditText newContentText = findViewById(R.id.organizer_new_notification_body_inputText);
        Spinner notificationTargetSpinner = findViewById(R.id.notification_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notification_target_array, android.R.layout.simple_spinner_dropdown_item);//androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationTargetSpinner.setAdapter(adapter);
        Button cancelButton = findViewById(R.id.organizer_notification_cancel_button);
        Button sendButton = findViewById(R.id.organizer_send_notification_button);
        fdb = FirebaseFirestore.getInstance();

        //Get eventID, participants to event with notifications enabled
        getEventID();
        getParticipants();

        //Clicking Cancel button ends create notification activity, should move back to view event screen
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Clicking send button validates text inputs
        //if successful, adds notification to database, adds notification id to notificationList for entrants
        sendButton.setOnClickListener(view -> {
            Log.d("createNotif", "passed getting enabled");
            String label = eventData.getEventName()+": "+newLabelText.getText().toString();
            String content = newContentText.getText().toString();
            String option = notificationTargetSpinner.getItemAtPosition(notificationTargetSpinner
                    .getSelectedItemPosition()).toString();
            String target = null;
            inputValidator validator = new inputValidator(this);
            if(validator.validateCreateNotification(label, content)){
                switch (option){
                    case "Waitlisted Entrants":
                        target = "waitlisted";
                        setParticipantList(target);
                        break;
                    case "Selected Entrants":
                        target = "selected";
                        setParticipantList(target);
                        break;
                    case "Cancelled Entrants":
                        target = "cancelled";
                        setParticipantList(target);
                        break;
                    case "Accepted Entrants":
                        target = "attending";
                        setParticipantList(target);
                        break;
                }
                //participants contains names of participants with status from dropdown (aka target)
                String[] participants = participantList.toArray(new String[0]);
                String newNotifID = notifications.document().getId();

                //add notification to database
                db.addNotification(newNotifID, eventID, content, label, target, participants);

                //add notification id to each entrant notificationList
                for(String entrant: participantList){
                    fdb.collection("entrants").document(entrant)
                            .update("notificationsList", FieldValue.arrayUnion(newNotifID))
                            .addOnSuccessListener(a -> Log.d("participantUpdate", "updated notificationsList"))
                            .addOnFailureListener(a -> Log.d("participantUpdate", "failed to update notificationsList"));
                }
                finish();
            }
        });
    }

    /**
     * {@code getEventID} Fetches eventID from bundle, sent from organizer view event screen
     */
    //gets event id from bundle
    private void getEventID(){
        Bundle bundle = getIntent().getExtras();
        Event event = (Event) bundle.get("EVENT");
        eventID = event.getEventID();
        eventData = new Event(eventID);
    }

    /**
     * {@code getParticipants} Fetches participants from Firestore where the participants' event field is the same as
     * the current eventID and if the entrant has notifications enabled.
     *
     * <p>This method queries the "participants" collection to find documents where the
     * event field matches the current event ID and then fetches the corresponding entrant data. If the entrant
     * has notifications enabled, then the participant document is added to a list.</p>
     */
    //gets participants to event from Firebase
    private void getParticipants(){
        fdb.collection("participants").whereEqualTo("event", eventID).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot doc : task.getResult()){
                            //check if participant has notifications enabled
                            db.getEntrant((String) doc.get("entrant"), entrant -> {
                                if ((Boolean) entrant.get("notificationsEnabled")){
                                    Map<String, Object> d = new HashMap<>();
                                    d.put("entrant", doc.get("entrant"));
                                    d.put("status", doc.get("status"));
                                    participantDocs.add(d);
                                }
                            });
                        }
                    }
                }
        });
    }

    /**
     * {@code setParticipantList} Fetches the entrant name and status from each participant document and adds them to a list
     */
    //adds participant names with status: "target" to participantList
    private void setParticipantList(String target){
        for (Map<String, Object> participant : participantDocs){
            String name = (String) participant.get("entrant");
            String status = (String) participant.get("status");
            if (status.compareTo(target)==0){
                participantList.add(name);
            }
        }
    }
}