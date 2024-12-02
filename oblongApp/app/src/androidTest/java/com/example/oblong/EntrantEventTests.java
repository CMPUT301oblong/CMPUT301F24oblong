package com.example.oblong;


import static org.junit.Assert.assertEquals;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Test;

public class EntrantEventTests {
    private Database db = new Database();
    private FirebaseFirestore fdb = FirebaseFirestore.getInstance();

    @Test
    public void testGetEntrantEvent(){
        setUp();
        fdb.collection("entrants").whereEqualTo("user", "EntrantEventTest").get()
            .addOnSuccessListener(entrant -> {
                fdb.collection("participants").whereEqualTo("entrant", "EntrantEventTest")
                    .get().addOnSuccessListener(p -> {
                        for(QueryDocumentSnapshot doc : p){
                            fdb.collection("events").document("EntrantEventTestEvent").get()
                                .addOnSuccessListener(event -> {
                                    assertEquals(doc.get("event"), event.getId());
                                });
                        }
                    });
            });
    }

    private void setUp(){
        db.addEntrant("EntrantEventTest", false, true, "");
        db.addEvent("EntrantEventTestEvent", "10", "", "",
                new GeoPoint(0,0), "", "");
        db.addParticipant("EntrantEventTestEntrantEventTestEvent", "EntrantEventTest",
                "EntrantEventTestEvent", new GeoPoint(0,0), "waitlisted");
    }
}
