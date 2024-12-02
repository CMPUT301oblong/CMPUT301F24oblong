package com.example.oblong;

import static org.junit.Assert.assertEquals;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class OrganizerEventTests {
    private Database db = new Database();
    private FirebaseFirestore fdb = FirebaseFirestore.getInstance();

    @Test
    public void testGetOrganizerEvent(){
        setUp();
        fdb.collection("organizers").whereEqualTo("user", "OrganizerEventTest").get()
                .addOnSuccessListener(organizer -> {
                    for(QueryDocumentSnapshot orgDoc: organizer){
                        fdb.collection("created").whereEqualTo("facility", orgDoc.get("facility"))
                            .get().addOnSuccessListener(c -> {
                                for(QueryDocumentSnapshot doc : c){
                                    fdb.collection("events").document((String) doc.get("event")).get()
                                        .addOnSuccessListener(events -> {
                                            assertEquals(doc.get("event"), events.getId());
                                        });
                                }
                            });
                    }

                });
    }

    private void setUp(){
        db.addOrganizer("OrganizerEventTest", "OrganizerEventTestFacility", "OrganizerEventTest");
        db.addEvent("OrganizerEventTestEvent", "10", "", "",
                new GeoPoint(0,0), "", "");
        Map<String, Object> created = new HashMap<>();
        created.put("event", "OrganizerEventTestEvent");
        created.put("facility", "OrganizerEventTestFacility");
        fdb.collection("created").add(created);
    }
}
