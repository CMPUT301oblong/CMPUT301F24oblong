package com.example.oblong.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.oblong.R;
import com.example.oblong.organizer.organizer_profile_fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

/**
 * The {@code AdminBaseActivity} class serves as the base activity for the administrator interface
 * of the application. It provides navigation functionality using a bottom navigation bar to
 * switch between different fragments: Admin Profile Browser, Admin Event Browser, and Admin Image Browser.
 * <p>
 * This activity uses a {@link ChipNavigationBar} for navigation, allowing the administrator
 * to browse and manage entrants, events, and images. On app launch, it defaults to loading
 * the Admin Profile Fragment.
 * </p>
 * <p>
 * Fragments loaded by this activity include:
 * <ul>
 *     <li>{@link AdminProfileBrowserActivity} - For browsing all entrants.</li>
 *     <li>{@link AdminEventBrowserFragment} - For managing and viewing all events.</li>
 *     <li>{@link AdminImageBrowserFragment} - For managing and viewing uploaded images.</li>
 * </ul>
 * </p>
 */
public class AdminBaseActivity extends AppCompatActivity {

    /**
     * Called when the activity is created. Initializes the bottom navigation bar and sets up
     * listeners for navigation events to switch between fragments. If no state is saved, it
     * defaults to loading the Admin Profile Fragment.
     *
     * @param savedInstanceState If the activity is being re-initialized after being shut down
     *                           or closed, this parameter contains the data most recently supplied
     *                           in {@link #onSaveInstanceState(Bundle)}. Otherwise, it is null.
     */
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
