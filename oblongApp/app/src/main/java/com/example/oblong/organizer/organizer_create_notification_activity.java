package com.example.oblong.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oblong.R;

public class organizer_create_notification_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_create_notification);

        EditText newLabelText = findViewById(R.id.organizer_new_notification_label_inputText);
        EditText newContentText = findViewById(R.id.organizer_new_notification_body_inputText);
        Spinner notificationTargetSpinner = findViewById(R.id.notification_spinner);
        Button sendNotificationButton = findViewById(R.id.organizer_send_notification_button);
        Button cancelButton = findViewById(R.id.organizer_notification_cancel_button);
        Button sendButton = findViewById(R.id.organizer_send_notification_button);

        //Clicking Cancel button moves back to organizer view event screen currently
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(organizer_create_notification_activity.this,
                        organizer_view_event_screen.class);
                startActivity(intent);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String label = newLabelText.getText().toString();
                String content = newContentText.getText().toString();

            }
        });
    }
}