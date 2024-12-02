package com.example.oblong.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.oblong.R;
import com.example.oblong.organizer.organizer_profile_fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class AdminBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChipNavigationBar bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setMenuResource(R.menu.admin_bottom_nav_menu);

        bottomNavigationView.setOnItemSelectedListener(itemId -> {
            Fragment selectedFragment = null;

            if (itemId == R.id.admin_all_entrants_button) {
                selectedFragment = new AdminProfileBrowserActivity();
            } else if (itemId == R.id.admin_all_events_button) {
                selectedFragment = new AdminEventBrowserFragment();
            } else if (itemId == R.id.admin_all_images_button) {
                // TODO: setup image list
//                       selectedFragment = new admin_image_list();
                selectedFragment = new AdminImageBrowserFragment();
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
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminProfileActivity())
                    .commit();
        }
    }
}
