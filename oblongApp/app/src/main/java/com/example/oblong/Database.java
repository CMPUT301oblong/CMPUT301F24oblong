package com.example.oblong;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Database {
    private final CollectionReference users;
    private final CollectionReference events;
    private final CollectionReference entrants;
    private final CollectionReference facilities;
    private final CollectionReference notifications;
    private final CollectionReference organizers;
    private final CollectionReference participants;

    public Database() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        events = db.collection("events");
        entrants = db.collection("entrants");
        facilities = db.collection("facilities");
        notifications = db.collection("notifications");
        organizers = db.collection("organizers");
        participants = db.collection("participants");
    }

    public void addParticipant(String id, String entrant, String event, String location, String status){
        HashMap<String, String> participant = new HashMap<>();
        // create a new user and store the id
        participant.put("entrant", entrant);
        participant.put("event", event);
        participant.put("location", location);
        participant.put("status", status);
        participants.document(id).set(participant).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }

    public void addOrganizer(String id, String facility, String user){
        HashMap<String, String> organizer = new HashMap<>();
        // create a new user and store the id
        organizer.put("facility", facility);
        organizer.put("user", user);
        organizers.document(id).set(organizer).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }

    public void addNotification(String id, String event, String text, String title){
        HashMap<String, String> notification = new HashMap<>();
        // create a new user and store the id
        notification.put("event", event);
        notification.put("text", text);
        notification.put("title", title);
        notifications.document(id).set(notification).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }

    public void addFacility(String id, String email, String name, String phone, String photo){
        HashMap<String, String> facility = new HashMap<>();
        // create a new user and store the id
        facility.put("email", email);
        facility.put("name", name);
        facility.put("phone", phone);
        facility.put("photo", photo);
        facilities.document(id).set(facility).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }

    public void addEntrant(String id, boolean locationEnabled, boolean notificationsEnabled, String user){
        HashMap<String, Object> entrant = new HashMap<>();
        // create a new user and store the id
        entrant.put("locationEnabled", locationEnabled);
        entrant.put("notificationsEnabled", notificationsEnabled);
        entrant.put("user", user);
        entrants.document(id).set(entrant).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }

    public void addUser(String id, String name, String email, String type, String phone, String profilePhoto){
        HashMap<String, String> user = new HashMap<>();
        // create a new user and store the id
        user.put("name", name);
        user.put("email", email);
        user.put("type", type);
        user.put("phone", phone);
        user.put("profilePhoto", profilePhoto);
        users.document(id).set(user).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }

    public void addEvent(String id, String capacity, String dateAndTime, String description, String location, String poster){
        HashMap<String, String> event = new HashMap<>();
        // create a new event and store at the id
        event.put("capacity", capacity);
        event.put("dateAndTime", dateAndTime);
        event.put("description", description);
        event.put("location", location);
        event.put("poster", poster);
        events.document(id).set(event).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }



}
