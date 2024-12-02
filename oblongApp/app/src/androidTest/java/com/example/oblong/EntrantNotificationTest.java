package com.example.oblong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EntrantNotificationTest {
    private Database db = new Database();
    private FirebaseFirestore fdb = FirebaseFirestore.getInstance();

    @Test
    public void testAddNotificationToEnabled() throws InterruptedException {
        setUP();
        TimeUnit.SECONDS.sleep(3);
        //get notification from db
        db.getNotification("EntrantNotificationTestNotification1", notification ->{
            //get participants associated with event
            fdb.collection("participants").whereEqualTo("event", "EntrantNotificationTestEvent")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for(QueryDocumentSnapshot doc : task.getResult()){
                            //get the entrant information
                            db.getEntrant((String) doc.get("entrant"), entrant -> {
                                if((Boolean) entrant.get("notificationsEnabled")){
                                    //add notification id to entrant notificationsList
                                    fdb.collection("entrants").document((String) doc.get("entrant"))
                                            .update("notificationsList",
                                                    FieldValue.arrayUnion("EntrantNotificationTestNotification1"));
                                }
                            });
                        }
                    }
                });
        });
        TimeUnit.SECONDS.sleep(3);
        CountDownLatch latch = new CountDownLatch(2);
        db.getEntrant("EntrantNotificationTest1", entrant -> {
            assertNull(entrant.get("notificationsList"));
            latch.countDown();
        });
        db.getEntrant("EntrantNotificationTest2", entrant -> {
            assertEquals(Arrays.asList("EntrantNotificationTestNotification1"), entrant.get("notificationsList"));
            latch.countDown();
        });
        latch.await(10, TimeUnit.SECONDS); //Wait up to 10 seconds for latch
    }

    private void setUP(){
        db.addNotification("EntrantNotificationTestNotification1", "EntrantNotificationTestEvent",
                "EntrantNotificationTestContent", "EntrantNotificationTestTitle",
                "enabled",
                new String[0]);
        db.addEntrant("EntrantNotificationTest1", false, false,
                "EntrantNotificationTest");
        db.addEntrant("EntrantNotificationTest2", false, true,
                "EntrantNotificationTest");
        db.addParticipant("EntrantNotificationTest1","EntrantNotificationTest1",
                "EntrantNotificationTestEvent", new GeoPoint(0,0), "waitlisted");
        db.addParticipant("EntrantNotificationTest2","EntrantNotificationTest2",
                "EntrantNotificationTestEvent", new GeoPoint(0,0), "waitlisted");
    }
}
