package com.example.oblong;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.oblong.entrant.EntrantProfileScreenFragment;
import com.example.oblong.organizer.organizer_base_activity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerNavigationTest {

    @Rule
    public ActivityScenarioRule<organizer_base_activity> activityRule = new ActivityScenarioRule<>(organizer_base_activity.class);

    @Test
    public void testOrganizerMyEventsNav() {
        // See if the organizer_base_activity is launched. Default on profile
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.profile)).check(matches(isDisplayed()));

        // Click on my events
        onView(withId(R.id.myEvents)).perform(click());
        // Check if the organizer_my_events_fragment is launched
        onView(withId(R.id.interestedEvents)).check(matches(isDisplayed()));
    }

    @Test
    public void testOrganizerCreateEventNav() {
        // See if the organizer_base_activity is launched. Default on profile
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.profile)).check(matches(isDisplayed()));

        // Click on Add Event
        onView(withId(R.id.addEvent)).perform(click());
        // Check if the organizer_create_event_fragment is launched
        onView(withId(R.id.create_event_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testOrganizerProfileNav() {
        // See if the organizer_base_activity is launched. Default on profile
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.profile)).check(matches(isDisplayed()));

        // Click on Profile
        onView(withId(R.id.profile)).perform(click());
        // Check if the organizer_profile_fragment is launched
        onView(withId(R.id.delete_user_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testOrganizerProfileEdit() {
        // Should access the user profile edit
        onView(withId(R.id.profile)).perform(click());
        onView(withId(R.id.profile_edit_button)).check(matches(isDisplayed()));

        // Click on profile edit button and navigate to that activity to CANCEL
        onView(withId(R.id.profile_edit_button)).perform(click());
        onView(withId(R.id.entrant_profile_edit_save_changes_button)).check(matches(isDisplayed()));
        onView(withId(R.id.entrant_profile_edit_cancel_button)).check(matches(isDisplayed()));
        onView(withId(R.id.entrant_profile_edit_cancel_button)).perform(click());

        // Check if back in organizer profile menu
        onView(withId(R.id.profile_edit_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testOrganizerEntrantView() {
        // Should access the user profile edit
        onView(withId(R.id.profile_edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_user_button)).perform(click());

        // Check if went to entrant view
        onView(withId(R.id.entrant_profile_name)).check(matches(isDisplayed()));
        onView(withId(R.id.entrant_profile_email)).check(matches(isDisplayed()));
        onView(withId(R.id.entrant_profile_phone)).check(matches(isDisplayed()));
    }

    @Test
    public void testOrganizerEditFacility() {
        // Open the facility profile edit
        onView(withId(R.id.facility_edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.facility_edit_button)).perform(click());

        // Edit facility name
        onView(withId(R.id.facility_name_editText))
                .check(matches(isDisplayed()))
                .perform(clearText(), typeText("facilityTest"), closeSoftKeyboard());

        // Edit facility email
        onView(withId(R.id.facility_email_editText))
                .check(matches(isDisplayed()))
                .perform(clearText(), typeText("testfacility@gmail.com"), closeSoftKeyboard());

        // Edit facility phone
        onView(withId(R.id.facility_phone_editText))
                .check(matches(isDisplayed()))
                .perform(clearText(), typeText("7801111111"), closeSoftKeyboard());

        // Save changes
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_button)).perform(click());

        // Verify navigation back to profile
        onView(withId(R.id.facility_edit_button)).check(matches(isDisplayed()));

        // Verify changes are displayed
        onView(withId(R.id.facility_name))
                .check(matches(withText("facilityTest")));
        onView(withId(R.id.facility_phoneno))
                .check(matches(withText("7801111111")));
        onView(withId(R.id.facility_email))
                .check(matches(withText("testfacility@gmail.com")));
    }


}
