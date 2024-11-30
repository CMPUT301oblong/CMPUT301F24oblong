package com.example.oblong.organizer;

import static android.app.PendingIntent.getActivity;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class organizer_create_notification_activity extends AppCompatActivity {
    private String eventID;
    private ArrayList<Map<String, Object>> participantDocs = new ArrayList<Map<String, Object>>();
    private ArrayList<String> entrantEnabledNotifications = new ArrayList<>();
    private ArrayList<String> preCheckParticipantList = new ArrayList<String>();
    private ArrayList<String> postCheckParticipantList = new ArrayList<>();
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

        //Get eventID, participants to event, and entrants with notifications enabled
        getEventID();
        Log.d("createNotif", "passed getting event ID");
        getParticipants();
        Log.d("createNotif", "passed getting participants");

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
            getEnabledNotificationsEntrants();
            Log.d("createNotif", "passed getting enabled");
            String label = newLabelText.getText().toString();
            String content = newContentText.getText().toString();
            String option = notificationTargetSpinner.getItemAtPosition(notificationTargetSpinner
                    .getSelectedItemPosition()).toString();
            String target = null;
            inputValidator validator = new inputValidator(this);
            if(validator.validateCreateNotification(label, content)){
                switch (option){
                    case "Waitlisted Entrants":
                        target = "waitlisted";
                        setPreCheckParticipantList(target);
                        break;
                    case "Selected Entrants":
                        target = "selected";
                        setPreCheckParticipantList(target);
                        break;
                    case "Cancelled Entrants":
                        target = "cancelled";
                        setPreCheckParticipantList(target);
                        break;
                    case "Accepted Entrants":
                        target = "attending";
                        setPreCheckParticipantList(target);
                        break;
                }
                setPostCheckParticipantList();
                //participants contains names of participants with notifications enabled
                String[] participants = postCheckParticipantList.toArray(new String[postCheckParticipantList.size()]);
                String newNotifID = notifications.document().getId();
                db.addNotification(newNotifID, eventID, content, label, target, participants);
                for(String entrant: postCheckParticipantList){
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
                                Map<String, Object> d = new HashMap<>();
                                d.put("entrant", doc.get("entrant"));
                                d.put("status", doc.get("status"));
                                //d.put("id", doc.getId());
                                participantDocs.add(d);
                            }
                        }
                    }
                });
    }

    //gets participants with notificationsEnabled: True from Firebase
    private void getEnabledNotificationsEntrants(){
        List<String> participants = new ArrayList<String>();
        for(Map<String, Object> p : participantDocs){
            participants.add((String) p.get("entrant"));
        }
        fdb.collection("entrants").whereIn("user", participants).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot doc : task.getResult()){
                            if((Boolean) doc.get("notificationsEnabled")){
                                entrantEnabledNotifications.add((String) doc.get("user"));
                            }
                        }
                    }
                }).addOnFailureListener(a -> Log.d("fetchEnabled", "catastrophic failure"));
    }

    //adds participant names with status: "target" to preCheckParticipantList
    private void setPreCheckParticipantList(String target){
        for (Map<String, Object> participant : participantDocs){
            String name = (String) participant.get("entrant");
            String status = (String) participant.get("status");
            if (status.compareTo(target)==0){
                preCheckParticipantList.add(name);
            }
        }
    }

    //adds participant names from preCheckParticipantList to postCheckParticipantList if notificationsEnabled: True
    private void setPostCheckParticipantList(){
        for(String participant: preCheckParticipantList){
            if(entrantEnabledNotifications.contains(participant)){
                postCheckParticipantList.add(participant);
            }
        }
    }

}