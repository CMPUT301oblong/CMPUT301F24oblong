package com.example.oblong.entrant;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.oblong.R;
import com.example.oblong.qr_scanner;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

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

        ChipNavigationBar bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setMenuResource(R.menu.new_entrant_nav_menu);

        // Set up listener for navigation bar
        bottomNavigationView.setOnItemSelectedListener(id -> {
            Fragment selectedFragment = null;

            // TODO: Change MYevents and allevents to the same fragment eventually
            if (id == R.id.EventBrowser) {
//                selectedFragment = new EntrantMyEventsFragment();
                selectedFragment = new EntrantEventBrowser();
            } else if (id == R.id.Camera) {
                // Set item selected to null
                bottomNavigationView.setItemSelected(R.id.Camera, false);
//                bottomNavigationView.setItemSelected(R.id.Profile, true);
//                selectedFragment = new EntrantProfileScreenFragment();
                startActivity(new Intent(this, qr_scanner.class));
                return; // Avoid replacing fragment after starting activity
            } else if (id == R.id.Profile) {
                selectedFragment = new EntrantProfileScreenFragment();
            }

            // Replace the current fragment with the selected one
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
        });

        // Load the default fragment on startup
        if (savedInstanceState == null) {
            bottomNavigationView.setItemSelected(R.id.Profile, true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EntrantProfileScreenFragment())
                    .commit();
        }
    }

}
