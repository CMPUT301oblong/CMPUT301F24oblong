package com.example.oblong.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.inputValidator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class organizer_create_notification_activity extends AppCompatActivity {
    private String eventID;
    private HashMap<String, Object> notif = new HashMap<>();;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_create_notification);

        EditText newLabelText = findViewById(R.id.organizer_new_notification_label_inputText);
        EditText newContentText = findViewById(R.id.organizer_new_notification_body_inputText);
        Spinner notificationTargetSpinner = findViewById(R.id.notification_spinner);
        Button cancelButton = findViewById(R.id.organizer_notification_cancel_button);
        Button sendButton = findViewById(R.id.organizer_send_notification_button);
        db = FirebaseFirestore.getInstance();

        //TODO Receive eventID from view_even_screen
        //assuming event data passed through intent/bundle
        //String eventID = "1";
        Intent intent = getIntent();
        getEventID();

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
            String targets = notificationTargetSpinner.getItemAtPosition(notificationTargetSpinner
                    .getSelectedItemPosition()).toString();
            inputValidator validator = new inputValidator(this);
            if(validator.validateCreateNotification(label, content, targets)){
                notif.put("event", eventID);
                notif.put("targets", targets);
                notif.put("text", content);
                notif.put("title", label);
                db.collection("notification").add(notif)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("adding notif", "DocumentSnapshot notification written with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("adding notif", "Error adding notification document", e);
                            }
                        });
            }
        });
    }

    //gets event id from bundle
    private void getEventID(){
        Bundle bundle = getIntent().getExtras();
        Event event = (Event) bundle.get("EVENT");
        eventID = event.getEventID();
    }


}