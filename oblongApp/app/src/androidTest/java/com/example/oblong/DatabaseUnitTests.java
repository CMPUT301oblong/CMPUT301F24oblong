package com.example.oblong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabaseUnitTests {

    private Database db;

    @Before
    public void setUp() {
        db = new Database();
    }

    @Test
    public void testAddParticipant() throws InterruptedException {
        String id = "testId";
        String entrant = "testEntrant";
        String event = "testEvent";
        GeoPoint location = new GeoPoint(1.0, 2.0);
        String status = "entrant";

        // Set up a latch to wait for the asynchronous operation to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Add participant to the database (asynchronous operation)
        db.addParticipant(id, entrant, event, location, status);

        // Retrieve participant from the database (asynchronous operation)
        db.getParticipants(id, participant -> {
            if (participant != null) {
                // Assert that the entrant and event are the correct values
                assertTrue(participant.get("entrant").equals(entrant));
                assertTrue(participant.get("event").equals(event));
            }

            // Decrement the latch to indicate the test is complete
            latch.countDown();
        });

        // Wait for the asynchronous callback to finish
        latch.await(10, TimeUnit.SECONDS);  // Wait up to 10 seconds for the callback
    }

    @Test
    public void testAddEvent() throws InterruptedException {
        String id = "testId";
        String capacity = "100";
        String dateAndTime = "2023-11-20 12:00:00";
        String description = "testDescription";
        GeoPoint location = new GeoPoint(1.0, 2.0);
        String poster = null;

        // Set up a latch to wait for the asynchronous operation to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Add event to the database (asynchronous operation)
        db.addEvent(id, capacity, dateAndTime, description, location, poster);

        // Retrieve event from the database (asynchronous operation)
        db.getEvent(id, event -> {
            if (event != null) {
                // Assert that the capacity and description are the correct values
                assertTrue(event.get("capacity").equals(capacity));
                assertTrue(event.get("description").equals(description));
            }

            // Decrement the latch to indicate the test is complete
            latch.countDown();
        });

        // Wait for the asynchronous callback to finish
        latch.await(10, TimeUnit.SECONDS);  // Wait up to 10 seconds for the callback
    }

    @Test
    public void testAddUser() throws InterruptedException {
        String id = "testId";
        String name = "testName";
        String email = "testEmail";
        String type = "testType";
        String phone = "testPhone";
        String profilePicture = null;

        // Set up a latch to wait for the asynchronous operation to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Add user to the database (asynchronous operation)
        db.addUser(id, name, email, type, phone, profilePicture);

        // Retrieve user from the database (asynchronous operation)
        db.getUser(id, user -> {
            if (user != null) {
                // Assert that the name and email are the correct values
                assertTrue(user.get("name").equals(name));
                assertTrue(user.get("email").equals(email));
            }

            // Decrement the latch to indicate the test is complete
            latch.countDown();
        });

        // Wait for the asynchronous callback to finish
        latch.await(10, TimeUnit.SECONDS);  // Wait up to 10 seconds for the callback
    }

    @Test
    public void testAddFacility() throws InterruptedException {
        String id = "testId";
        String email = "testEmail";
        String name = "testName";
        String phone = "testPhone";
        String profilePicture = null;

        // Set up a latch to wait for the asynchronous operation to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Add facility to the database (asynchronous operation)
        db.addFacility(id, email, name, phone, profilePicture);

        // Retrieve facility from the database (asynchronous operation)
        db.getFacility(id, facility -> {
            if(facility != null) {
                // Assert that the name and email are the correct values
                assertTrue(facility.get("name").equals(name));
                assertTrue(facility.get("email").equals(email));
            }

            // Decrement the latch to indicate the test is complete
            latch.countDown();
        });

        // Wait for the asynchronous callback to finish
        latch.await(10, TimeUnit.SECONDS);  // Wait up to 10 seconds for the callback
    }
}
