package com.example.oblong.organizer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.oblong.R;
import com.example.oblong.entrant.EntrantProfileScreenFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class organizer_base_activity extends AppCompatActivity {

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
