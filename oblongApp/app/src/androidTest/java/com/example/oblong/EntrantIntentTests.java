package com.example.oblong;

import static androidx.test.espresso.Espresso.onView;

import androidx.appcompat.app.AlertDialog;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.oblong.entrant.EntrantProfileEditActivity;
import com.google.firebase.Firebase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EntrantIntentTests {
    @Rule
    public ActivityScenarioRule<RoleSelector> scenario =
            new ActivityScenarioRule<RoleSelector>(RoleSelector.class);
    @Test
    public void addUserInfo() {
        //check addNewUserDialog
        onView(withId(R.id.editTextText))
                .perform(ViewActions.typeText("firstTest lastTest"));
        onView(withId(R.id.editTextTextEmailAddress))
                .perform(ViewActions.typeText("testEmail@gmail.com"));
        onView(withId(R.id.editTextPhone))
                .perform(ViewActions.typeText("7777777777"));
        onView(withText("Add")).perform(click());
    }

    @Test
    public void viewEntrantInfo(){
        //add new user info
        onView(withId(R.id.editTextText))
                .perform(ViewActions.typeText("firstTest lastTest"));
        onView(withId(R.id.editTextTextEmailAddress))
                .perform(ViewActions.typeText("testEmail@gmail.com"));
        onView(withId(R.id.editTextPhone))
                .perform(ViewActions.typeText("7777777777"));
        onView(withText("Add")).perform(click());

        //check profile info displayed
        onView(withText("firstTest lastTest")).check(matches(isDisplayed()));
        onView(withText("testEmail@gmail.com")).check(matches(isDisplayed()));
        onView(withText("7777777777")).check(matches(isDisplayed()));
    }

    //look into espresso database idling resource, db queries take time
    @Test
    public void editEntrantInfo() {

        //add new user info
        onView(withId(R.id.editTextText))
                .perform(ViewActions.typeText("firstTest lastTest"));
        onView(withId(R.id.editTextTextEmailAddress))
                .perform(ViewActions.typeText("testEmail@gmail.com"));
        onView(withId(R.id.editTextPhone))
                .perform(ViewActions.typeText("7777777777"));
        onView(withText("Add")).perform(click());

        //check profile info displayed
        onView(withText("firstTest lastTest")).check(matches(isDisplayed()));
        onView(withText("testEmail@gmail.com")).check(matches(isDisplayed()));
        onView(withText("7777777777")).check(matches(isDisplayed()));

        //update profile info
        onView(withId(R.id.entrant_profile_edit_button)).perform(click());

        onView(withId(R.id.entrant_profile_edit_name_input))
                .perform(ViewActions.clearText(), ViewActions.typeText("updatedFirstTest updatedLastTest"));
        onView(withText("testEmail@gmail.com"))
                .perform(ViewActions.clearText(), ViewActions.typeText("updatedTestEmail@gmail.com"));
        onView(withId(R.id.entrant_profile_edit_phone_input))
                .perform(ViewActions.clearText(), ViewActions.typeText("8888888888"));
        onView(withId(R.id.entrant_profile_edit_save_changes_button)).perform(click());

        //check updates were saved
        onView(withText("updatedFirstTest updatedLastTest")).check(matches(isDisplayed()));
        onView(withText("updatedTestEmail@gmail.com")).check(matches(isDisplayed()));
        onView(withText("8888888888")).check(matches(isDisplayed()));
    }
}
/*
//Waits for db queries to finish
        scenario.getScenario().onActivity(activity -> {
            ActivityIdlingResource activityIdlingResource = activity.getActivityIdlingResource();
            // Register the IdlingResource to make sure Espresso waits for the activity to be idle
            IdlingRegistry.getInstance().register(activityIdlingResource);
        });
    *** placed inside test, when want test to wait ***

//requires:
    ActivityIdlingResourceClass --> java class needs to be created
    private ActivityIdlingResource activityIdlingResource;
    activityIdlingResource = new ActivityIdlingResource(); *** to be set right before db stuff ***
    activityIdlingResource.setIdleState(true); *** set when done and want test to continue ***
    *** all need to be set in java classes ***
 */