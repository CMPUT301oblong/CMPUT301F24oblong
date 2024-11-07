package com.example.oblong.organizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oblong.Event;
import com.example.oblong.R;

public class organizer_create_notification_activity extends AppCompatActivity {
    private Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_create_notification);
        Bundle bundle = getIntent().getExtras();

        event = (Event) bundle.get("EVENT");

    }


}