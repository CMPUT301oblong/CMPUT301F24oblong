package com.example.oblong.organizer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.oblong.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class organizer_base_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Change the bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.organizer_bottom_nav_menu);

        bottomNavigationView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) navListener);

        // Load the default fragment on startup
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new organizer_profile_fragment())
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
                    // TODO: Implement Organizer MyEvents Fragment
                    selectedFragment = new organizer_my_events_fragment();
                } else if (itemId == R.id.addEvent) {
                    selectedFragment = new organizer_create_event_fragment();
                } else if (itemId == R.id.profile) {
                    selectedFragment = new organizer_profile_fragment();
                }

                // Replace the current fragment with the selected one
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();

                return true;
            };
}
