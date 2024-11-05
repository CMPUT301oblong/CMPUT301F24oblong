package com.example.oblong;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

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
                    selectedFragment = new EntrantUpcomingWaitlistedEventsFragment();
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
