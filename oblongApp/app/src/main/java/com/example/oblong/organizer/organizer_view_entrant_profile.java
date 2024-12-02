package com.example.oblong.organizer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oblong.R;

/**
 * Activity class for viewing an entrant's profile.
 */
public class organizer_view_entrant_profile extends AppCompatActivity {

    /**
     * Initializes the activity, sets up the user interface to view the entrants profile.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_view_entrant_profile);
    }
}