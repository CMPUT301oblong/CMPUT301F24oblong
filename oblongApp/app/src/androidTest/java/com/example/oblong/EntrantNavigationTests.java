package com.example.oblong;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.oblong.entrant.EntrantBaseActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EntrantNavigationTests {
    @Rule
    public ActivityScenarioRule<EntrantBaseActivity> activityRule =
            new ActivityScenarioRule<>(EntrantBaseActivity.class);

    @Test
    public void testMyEvents(){
        //Check entrant profile info screen is displayed
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.entrant_profile_name)).check(matches(isDisplayed()));
        onView(withId(R.id.entrant_profile_email)).check(matches(isDisplayed()));

        //move to entrant view my events screen
        onView(withId(R.id.myEvents)).perform(click());
        onView(withId(R.id.interestedEvents)).check(matches(isDisplayed()));

    }

    @Test
    public void testProfileScreen() {
        //Check entrant profile info screen is displayed
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.entrant_profile_name)).check(matches(isDisplayed()));
        onView(withId(R.id.entrant_profile_email)).check(matches(isDisplayed()));

        //move back to profile screen
        onView(withId(R.id.Profile)).perform(click());
        onView(withId(R.id.entrant_profile_name)).check(matches(isDisplayed()));
        onView(withId(R.id.entrant_profile_email)).check(matches(isDisplayed()));
    }
}
