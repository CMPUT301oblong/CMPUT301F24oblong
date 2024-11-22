package com.example.oblong;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;

import java.util.concurrent.TimeUnit;
import org.junit.Test;
import com.example.oblong.Database;
import com.example.oblong.entrant.EntrantBaseActivity;
import com.example.oblong.entrant.EntrantJoinEventActivity;
import com.google.firebase.firestore.GeoPoint;


public class WaitlistUnitTest {






        Database db = new Database();
    @Test
    public void testRemoveWaitlist() throws InterruptedException {

        String participantId = "019263481723491" + "event_idTest_klashuernlfvksdju";
        GeoPoint location = new GeoPoint(0, 0);
        String status = "cancelled";

        db.addParticipant(participantId, "019263481723491", "event_idTest_klashuernlfvksdju", location, status);
        TimeUnit.SECONDS.sleep(10);
        db.getParticipants(participantId, participant -> {

            assertEquals(participant.get("status"), "cancelled");


        });
    }
        @Test
        public void testJoinWaitlist() throws InterruptedException {
            db.addUser("9999999999", "Waitlist Test", "waitlist@test.com","entrant",null,null);

            String participantId = "9999999999" + "event_idTest_klashuernlfvksdju";
            GeoPoint location = new GeoPoint(0, 0);
            String status = "waitlisted";

            db.addParticipant(participantId, "9999999999", "event_idTest_klashuernlfvksdju", location, status);
            TimeUnit.SECONDS.sleep(10);
            db.getParticipants(participantId, participant -> {
                assertEquals(participant.get("status"), "waitlisted");


            });
        }




    }

