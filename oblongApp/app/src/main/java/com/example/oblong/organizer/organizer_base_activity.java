package com.example.oblong.organizer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.oblong.R;
import com.example.oblong.entrant.EntrantProfileScreenFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

/**
 * The main activity for the organizer's section of the app.
 *
 * <p>This activity serves as the base for the organizer's interface, using a BottomNavigationView
 * to navigate between different fragments: "My Events", "Add Event", and "Profile".</p>
 *
 * <p>The activity initializes with the profile screen as the default fragment and provides
 * navigation for the user to switch between different sections of the app.</p>
 */
public class organizer_base_activity extends AppCompatActivity {

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

        ChipNavigationBar bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setMenuResource(R.menu.organizer_bottom_nav_menu);

        bottomNavigationView.setOnItemSelectedListener(itemId -> {
            Fragment selectedFragment = null;

            if (itemId == R.id.myEvents) {
                selectedFragment = new organizer_my_events_fragment();
            } else if (itemId == R.id.addEvent) {
                selectedFragment = new organizer_create_event_fragment();
            } else if (itemId == R.id.profile) {
                selectedFragment = new organizer_profile_fragment();
            }

            // Replace the current fragment with the selected one
            if(selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
        });

        // Load the default fragment on startup
        if (savedInstanceState == null) {
            bottomNavigationView.setItemSelected(R.id.profile, true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new organizer_profile_fragment())
                    .commit();
        }
    }
}
