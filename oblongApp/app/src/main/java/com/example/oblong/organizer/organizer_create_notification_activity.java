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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class organizer_create_notification_activity extends AppCompatActivity {
    private String eventID;
    //private HashMap<String, Object> notif = new HashMap<>();
    private ArrayList<Map<String, Object>> participantDocs = new ArrayList<Map<String, Object>>();
    private ArrayList<String> participantList = new ArrayList<String>();
    private FirebaseFirestore fdb;
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
            if(validator.validateCreateNotification(label, content, option)){
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
                //String participants = TextUtils.join(", ", participantList);
                String[] participants = participantList.toArray(new String[participantList.size()]);
                db.addNotification(null, eventID, content, label, target, participants);
            }
            finish();
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
                                participantDocs.add(doc.getData());
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