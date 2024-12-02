package com.example.oblong;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.oblong.admin.AdminBaseActivity;
import com.example.oblong.entrant.EntrantBaseActivity;

import org.junit.Rule;
import org.junit.Test;

public class AdminNavigationTest {
    @Rule
    public ActivityScenarioRule<AdminBaseActivity> activityRule =
            new ActivityScenarioRule<>(AdminBaseActivity.class);

    @Test
    public void testAllEntrantsNav(){
        // See if the AdminBaseActivity is launched. Default on profile
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_name_text)).check(matches(isDisplayed()));

        //click on all entrants
        onView(withId(R.id.admin_all_entrants_button)).perform(click());

        //see if AdminProfileBrowserActivity is launched
        onView(withId(R.id.admin_event_browser_title)).check(matches(isDisplayed()));
    }

    @Test
    public void testAllEventsNav(){
        // See if the AdminBaseActivity is launched. Default on profile
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_name_text)).check(matches(isDisplayed()));

        //click on all events
        onView(withId(R.id.admin_all_events_button)).perform(click());

        //see if AdminEventBrowserFragment is launched
        onView(withId(R.id.admin_event_browser_title)).check(matches(isDisplayed()));
    }

    @Test
    public void testAllImagesNav(){
        // See if the AdminBaseActivity is launched. Default on profile
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_name_text)).check(matches(isDisplayed()));

        //click on all images
        onView(withId(R.id.admin_all_images_button)).perform(click());

        //see if AdminImageBrowserFragment is launched
        onView(withId(R.id.admin_image_browser_title)).check(matches(isDisplayed()));
    }
}
