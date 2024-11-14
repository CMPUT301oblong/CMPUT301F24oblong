package com.example.oblong.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.oblong.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AdminBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Change the bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.admin_bottom_nav_menu);
        // Set none to selected

        bottomNavigationView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) navListener);

        // Load the default fragment on startup
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminProfileActivity())
                    .commit();
        }
        // Set the selected item in the BottomNavigationView
//        bottomNavigationView.setSelectedItemId(R.id.admin_all_entrants_button);
    }

    private final NavigationBarView.OnItemSelectedListener navListener =
            (BottomNavigationView.OnNavigationItemSelectedListener) item -> {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.admin_all_entrants_button) {
                    selectedFragment = new AdminProfileBrowserActivity();
                } else if (itemId == R.id.admin_all_events_button) {
                    selectedFragment = new AdminEventBrowserFragment();
                } else if (itemId == R.id.admin_all_images_button) {
                    // TODO: setup image list
//                    selectedFragment = new admin_image_list();
                }

                // Replace the current fragment with the selected one
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();

                return true;
            };
}
