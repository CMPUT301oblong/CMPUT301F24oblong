package com.example.oblong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerNotificationTest {
    private Database db = new Database();
    private FirebaseFirestore fdb = FirebaseFirestore.getInstance();

    //tests Database addNotification() method
    @Test
    public void testAddNotification() throws InterruptedException {
        String id = "ONTtestnotification";
        db.addNotification(id, "OrganizerNotificationTestEvent", "Organizer Notification Test event",
                "ONT test", "waitlisted", new String[]{"OrganizerNotificationTestEntrant1"});
        TimeUnit.SECONDS.sleep(5);
        db.getNotification(id, notif -> {
            assertEquals("ONT test", notif.get("title"));
        });
        TimeUnit.SECONDS.sleep(3);
    }

    //tests Database getNotification() method
    @Test
    public void testGetNotification() throws InterruptedException {
        String id = "ONTtestnotification";
        TimeUnit.SECONDS.sleep(5);
        db.getNotification(id, notif -> {
            assertEquals("ONT test", notif.get("title"));
        });
        TimeUnit.SECONDS.sleep(5);
    }

    //tests ability to get all participants associated an event
    @Test
    public void testGetAllParticipants() throws InterruptedException {
        String expectedParticipants = "OrganizerNotificationTestEntrant1, OrganizerNotificationTestEntrant2, " +
                "OrganizerNotificationTestEntrant3, OrganizerNotificationTestEntrant4";
        setUp();
        TimeUnit.SECONDS.sleep(3);
        fdb.collection("participants").whereEqualTo("event", "OrganizerNotificationTestEvent")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> participants= new ArrayList<String>();
                    queryDocumentSnapshots.forEach(doc -> participants.add(doc.getString("entrant")));
                    String result = TextUtils.join(", ", participants);
                    assertTrue(expectedParticipants.contains(result));
                }).addOnFailureListener(e -> {
                    Log.e("test", "query failure");
                });
        TimeUnit.SECONDS.sleep(3);
    }

    //tests ability to get participants waitlisted on an event
    @Test
    public void testGetWaitlisted() throws InterruptedException {
        String expectedParticipants = "OrganizerNotificationTestEntrant1";
        setUp();
        TimeUnit.SECONDS.sleep(3);
        fdb.collection("participants").whereEqualTo("event", "OrganizerNotificationTestEvent")
                .whereEqualTo("status", "waitlisted").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot doc: value){
                            if(doc.get("entrant") != null){
                                assertEquals(0, expectedParticipants.compareTo(doc.getString("entrant")));
                            }
                        }
                    }
                });
        TimeUnit.SECONDS.sleep(3);
    }

    //tests ability to get participants selected for an event
    @Test
    public void testGetSelected() throws InterruptedException {
        String expectedParticipants = "OrganizerNotificationTestEntrant2";
        setUp();
        TimeUnit.SECONDS.sleep(3);
        fdb.collection("participants").whereEqualTo("event", "OrganizerNotificationTestEvent")
                .whereEqualTo("status", "selected").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot doc: value){
                            if(doc.get("entrant") != null){
                                assertEquals(0, expectedParticipants.compareTo(doc.getString("entrant")));
                            }
                        }
                    }
                });
        TimeUnit.SECONDS.sleep(3);
    }

    //tests ability to get participants cancelled for an event
    @Test
    public void testGetCancelled() throws InterruptedException {
        String expectedParticipants = "OrganizerNotificationTestEntrant4";
        setUp();
        TimeUnit.SECONDS.sleep(3);
        fdb.collection("participants").whereEqualTo("event", "OrganizerNotificationTestEvent")
                .whereEqualTo("status", "cancelled").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot doc: value){
                            if(doc.get("entrant") != null){
                                assertEquals(0, expectedParticipants.compareTo(doc.getString("entrant")));
                            }
                        }
                    }
                });
        TimeUnit.SECONDS.sleep(3);
    }

    //tests ability to get participants accepted for an event
    @Test
    public void testGetAccepted() throws InterruptedException {
        String expectedParticipants = "OrganizerNotificationTestEntrant3";
        setUp();
        TimeUnit.SECONDS.sleep(3);
        fdb.collection("participants").whereEqualTo("event", "OrganizerNotificationTestEvent")
                .whereEqualTo("status", "accepted").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot doc: value){
                            if(doc.get("entrant") != null){
                                assertEquals(0, expectedParticipants.compareTo(doc.getString("entrant")));
                            }
                        }
                    }
                });
        TimeUnit.SECONDS.sleep(3);
    }

    //test sending notification to waitlisted entrants
    @Test
    public void testSendNotificationWaitlisted() throws InterruptedException {
        String id = "ONTTestNotificationWaitlisted2";
        db.addNotification(id, "OrganizerNotificationTestEvent", "Organizer Notification Test event",
                "ONT test", "waitlisted", new String[]{});
        TimeUnit.SECONDS.sleep(5);
        fdb.collection("participants").whereEqualTo("event", "OrganizerNotificationTestEvent")
                .whereEqualTo("status", "waitlisted").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot doc: value){
                            if(doc.get("entrant") != null){
                                Map<String, Object> data = new HashMap<>();
                                data.put("target list", Arrays.asList(new String[]{(String)doc.get("entrant")}));
                                fdb.collection("notifications").document(id)
                                        .set(data, SetOptions.merge());
                            }
                        }
                    }
                });
        TimeUnit.SECONDS.sleep(3);
        db.getNotification(id, n -> {
            assertEquals(Arrays.asList("OrganizerNotificationTestEntrant1"), n.get("target list"));
        });
        TimeUnit.SECONDS.sleep(3);
    }

    //test sending notification to selected entrants
    @Test
    public void testSendNotificationSelected() throws InterruptedException {
        String id = "ONTTestNotificationSelected2";
        db.addNotification(id, "OrganizerNotificationTestEvent", "Organizer Notification Test event",
                "ONT test", "selected", new String[]{});
        TimeUnit.SECONDS.sleep(5);
        fdb.collection("participants").whereEqualTo("event", "OrganizerNotificationTestEvent")
                .whereEqualTo("status", "selected").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot doc: value){
                            if(doc.get("entrant") != null){
                                Map<String, Object> data = new HashMap<>();
                                data.put("target list", Arrays.asList(new String[]{(String)doc.get("entrant")}));
                                fdb.collection("notifications").document(id)
                                        .set(data, SetOptions.merge());
                            }
                        }
                    }
                });
        TimeUnit.SECONDS.sleep(5);
        db.getNotification(id, n -> {
            assertEquals(Arrays.asList("OrganizerNotificationTestEntrant2"), n.get("target list"));
        });
        TimeUnit.SECONDS.sleep(3);
    }

    //tests sending notifications to cancelled entrants
    @Test
    public void testSendNotificationCancelled() throws InterruptedException {
        String id = "ONTTestNotificationCancelled2";
        db.addNotification(id, "OrganizerNotificationTestEvent", "Organizer Notification Test event",
                "ONT test", "cancelled", new String[]{});
        TimeUnit.SECONDS.sleep(5);
        fdb.collection("participants").whereEqualTo("event", "OrganizerNotificationTestEvent")
                .whereEqualTo("status", "cancelled").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot doc: value){
                            if(doc.get("entrant") != null){
                                Map<String, Object> data = new HashMap<>();
                                data.put("target list", Arrays.asList(new String[]{(String)doc.get("entrant")}));
                                fdb.collection("notifications").document(id)
                                        .set(data, SetOptions.merge());
                            }
                        }
                    }
                });
        TimeUnit.SECONDS.sleep(3);
        db.getNotification(id, n -> {
            assertEquals(Arrays.asList("OrganizerNotificationTestEntrant4"), n.get("target list"));
        });
        TimeUnit.SECONDS.sleep(3);
    }

    //tests sending notifications to accepted entrants
    @Test
    public void testSendNotificationAccepted() throws InterruptedException {
        String id = "ONTTestNotificationAccepted2";
        db.addNotification(id, "OrganizerNotificationTestEvent", "Organizer Notification Test event",
                "ONT test", "accepted", new String[] {});
        TimeUnit.SECONDS.sleep(5);
        fdb.collection("participants").whereEqualTo("event", "OrganizerNotificationTestEvent")
                .whereEqualTo("status", "accepted").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot doc: value){
                            if(doc.get("entrant") != null){
                                Map<String, Object> data = new HashMap<>();
                                data.put("target list", Arrays.asList(new String[]{(String) doc.get("entrant")}));
                                fdb.collection("notifications").document(id)
                                        .set(data, SetOptions.merge());
                            }
                        }
                    }
                });
        TimeUnit.SECONDS.sleep(3);
        db.getNotification(id, n -> {
            assertEquals(Arrays.asList("OrganizerNotificationTestEntrant3"), n.get("target list") );
        });
        TimeUnit.SECONDS.sleep(3);
    }

    //add entrants w/ different statuses to participate in same event
    public void setUp(){
        addPeople("OrganizerNotificationTestEntrant1", "OrganizerNotificationTestEntrant1",
                "OrganizerNotificationTestEvent", "waitlisted");
        addPeople("OrganizerNotificationTestEntrant2", "OrganizerNotificationTestEntrant2",
                "OrganizerNotificationTestEvent", "selected");
        addPeople("OrganizerNotificationTestEntrant3", "OrganizerNotificationTestEntrant3",
                "OrganizerNotificationTestEvent", "accepted");
        addPeople("OrganizerNotificationTestEntrant4", "OrganizerNotificationTestEntrant4",
                "OrganizerNotificationTestEvent", "cancelled");
        addEvent("OrganizerNotificationTestEvent");
    }

    //adds participants to database
    public void addPeople(String id, String entrant, String event, String status){
        HashMap<String, Object> participant = new HashMap<>();
        // create a new participant and store the id
        participant.put("entrant", entrant);
        participant.put("event", event);
        participant.put("location", new GeoPoint(0,0));
        participant.put("status", status);
        fdb.collection("participants").document(id).set(participant).addOnSuccessListener(aVoid -> Log.d("database", "participant added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding participant", e));
    }

    //adds event to database
    public void addEvent(String id){
        HashMap<String, Object> event = new HashMap<>();
        event.put("capacity", "20");
        event.put("dateAndTime", "0000");
        event.put("description", "Organizer Notification Test event");
        event.put("location", "location");
        event.put("poster", "poster");
        fdb.collection("events").document(id).set(event).addOnSuccessListener(aVoid -> Log.d("database", "event added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding event", e));
    }
}
