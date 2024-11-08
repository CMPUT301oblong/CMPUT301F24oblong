package com.example.oblong;

import static androidx.test.espresso.Espresso.onView;

import androidx.appcompat.app.AlertDialog;
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EntrantIntentTests {
    @Rule
    public ActivityScenarioRule<RoleSelector> scenario =
            new ActivityScenarioRule<RoleSelector>(RoleSelector.class);
    @Test
    public void addUserInfo() throws InterruptedException {
        //check addNewUserDialog
        Thread.sleep(2000);
        onView(withId(R.id.editTextText))
                .perform(ViewActions.typeText("firstTest lastTest"));
        onView(withId(R.id.editTextTextEmailAddress))
                .perform(ViewActions.typeText("testEmail@gmail.com"));
        onView(withId(R.id.editTextPhone))
                .perform(ViewActions.typeText("7777777777"));
        onView(withText("Add")).perform(click());
    }

    @Test
    public void viewEntrantInfo() throws InterruptedException {
        //add new user info
        Thread.sleep(2000);
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

    @Test
    public void editEntrantInfo(){

    }
}
