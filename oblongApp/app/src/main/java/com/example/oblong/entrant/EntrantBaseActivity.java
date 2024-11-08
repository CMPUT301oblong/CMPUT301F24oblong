package com.example.oblong.entrant;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.oblong.R;
import com.example.oblong.qr_scanner;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * The main activity for the entrant's section of the app.
 *
 * <p>This activity serves as the base for the entrant's interface, using a BottomNavigationView
 * to navigate between different fragments: "My Events", "All Events", QR Scanner, and "Profile".</p>
 *
 * <p>The activity initializes with the profile screen as the default fragment and provides
 * navigation for the user to switch between different sections of the app.</p>
 */
public class EntrantBaseActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     *
     * <p>This method sets up the main layout, initializes the BottomNavigationView,
     * and sets the default fragment to be the profile screen. It also sets up the
     * navigation listener for handling user interactions with the BottomNavigationView.</p>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data supplied by onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) navListener);

        // Load the default fragment on startup
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EntrantProfileScreenFragment())
                    .commit();
        }
        // Set the selected item in the BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.profile);
    }

    private final NavigationBarView.OnItemSelectedListener navListener =
            (BottomNavigationView.OnNavigationItemSelectedListener) item -> {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.myEvents) {
                    selectedFragment = new EntrantMyEventsFragment();
                } else if (itemId == R.id.allEvents) {
                    selectedFragment = new EntrantUpcomingEventsFragment();
                } else if (itemId == R.id.camera) {
                    startActivity(new Intent(this, qr_scanner.class));
                } else if (itemId == R.id.profile) {
                    selectedFragment = new EntrantProfileScreenFragment();
                }

                // Replace the current fragment with the selected one
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();

                return true;
            };
}
