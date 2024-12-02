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

public class organizer_create_notification_activity extends AppCompatActivity {
    private String eventID;
    private ArrayList<Map<String, Object>> participantDocs = new ArrayList<Map<String, Object>>();
    private ArrayList<String> participantList = new ArrayList<String>();
    private FirebaseFirestore fdb;
    private CollectionReference notifications = FirebaseFirestore.getInstance().collection("notifications");
    private Database db = new Database();

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
            String label = eventID+": "+newLabelText.getText().toString();
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

    //gets event id from bundle
    private void getEventID(){
        Bundle bundle = getIntent().getExtras();
        Event event = (Event) bundle.get("EVENT");
        eventID = event.getEventID();
    }

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