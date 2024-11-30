package com.example.oblong.organizer;

import static android.app.PendingIntent.getActivity;

import android.os.Bundle;
import android.text.TextUtils;
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

        //Get eventID and participants to event
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
        //if successful, adds notification to database
        sendButton.setOnClickListener(view -> {
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
                String[] participants = participantList.toArray(new String[participantList.size()]);
                String newNotifID = notifications.document().getId();
                db.addNotification(newNotifID, eventID, content, label, target, participants);
                for(Map<String, Object> p : participantDocs){
                    p.put("notificationList", Arrays.asList(newNotifID));
                    fdb.collection("participants").document((String) p.get("id"))
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
                                Map<String, Object> d = new HashMap<>(doc.getData());
                                d.put("id", doc.getId());
                                participantDocs.add(d);
                            }
                        }
                    }
                });
    }

    //adds participants with status: "target" to participantList
    private void setParticipantList(String target){
        for (Map<String, Object> participant : participantDocs){
            String name = (String) participant.get("entrant");
            String status = (String) participant.get("status");
            Log.d("checkStatus", "status is: "+status);
            Log.d("checkName", "name is: "+name);
            if (status.compareTo(target)==0){
                participantList.add(name);
            }
        }
    }

}