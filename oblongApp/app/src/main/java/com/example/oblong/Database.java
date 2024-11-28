package com.example.oblong;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.oblong.admin.AdminUserProfileView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@code Database} This class handles sending and fetching data from the Firebase
 */
public class Database {
    private final CollectionReference users;
    private final CollectionReference events;
    private final CollectionReference entrants;
    private final CollectionReference facilities;
    private final CollectionReference notifications;
    private final CollectionReference organizers;
    private final CollectionReference participants;
    private String user_id;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * The {@code Database} method assigns the variables to the Firebase collections
     */
    public Database() {
        users = db.collection("users");
        events = db.collection("events");
        entrants = db.collection("entrants");
        facilities = db.collection("facilities");
        notifications = db.collection("notifications");
        organizers = db.collection("organizers");
        participants = db.collection("participants");
    }

    /**
     * {@interface OnDataReceivedListener} Called when data is successfully received.
     * @param <T>
     */
    // Interface to handle getting data
    public interface OnDataReceivedListener<T> {
        // onDataRecieved called when db request is complete (or errors)
        void onDataReceived(T data);
    }

    // IMPORTANT NOTE:
    // firebase functions are asynchronous, calling these functions
    // only initiates the requests, functions to retrieve data need
    // to be called like this example (user is a hashmap):
    //
    //    db.getUser("0", user -> {
    //            if (user != null) {
    //                // Process data
    //                Log.d("user", "User Name: " + user.get("name")); // print user's name to console
    //            } else {
    //                Log.d("user", "User not found or an error occurred.");
    //            }
    //        });

    /**
     * The {@code getCurrentUser} method retrieves the user ID from Firebase
     * @param listener
     */
    // Retrieve the user ID asynchronously
    public static void getCurrentUser(OnDataReceivedListener<String> listener) {
        FirebaseInstallations.getInstance().getId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String userId = task.getResult();
                Log.d("Database", "User ID: " + userId);
                listener.onDataReceived(userId); // Pass the userId back to the listener
            } else {
                Log.e("Database", "Failed to retrieve user ID");
                listener.onDataReceived("0"); // return 0 on failure
            }
        });
    }


    /**
     * The {@code updateDocument} method updates a document in Firebase
     * @param collection
     * @param document_id
     * @param updates
     * @param listener
     */
    public void updateDocument(String collection, String document_id, HashMap<String, Object> updates, OnDataReceivedListener<Boolean> listener) {
        db.collection(collection).document(document_id)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Database", "Document updated successfully in: " + collection);
                    listener.onDataReceived(true); // success
                })
                .addOnFailureListener(e -> {
                    Log.d("Database", "Document failed to update from: " + collection);
                    listener.onDataReceived(false); // fail
                });
    }

    /**
     * The {@code getParticipant} method retrieves a participant data from Firebase
     * @param id
     * @param listener
     */
    public void getParticipants(String id, OnDataReceivedListener<HashMap<String, Object>> listener) {
        participants.document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                HashMap<String, Object> participant = (HashMap<String, Object>) documentSnapshot.getData();
                listener.onDataReceived(participant); // pass data to OnDataReceivedListener
            } else {
                listener.onDataReceived(null); // invalid id
            }
        }).addOnFailureListener(e -> {
            Log.w("Database", "Error retrieving participant", e);
            listener.onDataReceived(null); // error
        });
    }

    /**
     * The {@code getOrganizer} method retrieves an organizer data from Firebase
     * @param id
     * @param listener
     */
    public void getOrganizer(String id, OnDataReceivedListener<HashMap<String, Object>> listener) {
        organizers.document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                HashMap<String, Object> organizer = (HashMap<String, Object>) documentSnapshot.getData();
                listener.onDataReceived(organizer); // pass data to OnDataReceivedListener
            } else {
                listener.onDataReceived(null); // invalid id
            }
        }).addOnFailureListener(e -> {
            Log.w("Database", "Error retrieving organizer", e);
            listener.onDataReceived(null); // error
        });
    }

    /**
     * {@code getNotification} method retrieves a notification data from Firebase}
     * @param id
     * @param listener
     */
    public void getNotification(String id, OnDataReceivedListener<HashMap<String, Object>> listener) {
        notifications.document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                HashMap<String, Object> notification = (HashMap<String, Object>) documentSnapshot.getData();
                listener.onDataReceived(notification); // pass data to OnDataReceivedListener
            } else {
                listener.onDataReceived(null); // invalid id
            }
        }).addOnFailureListener(e -> {
            Log.w("Database", "Error retrieving notification", e);
            listener.onDataReceived(null); // error
        });
    }

    /**
     * The {@code getFacility} method retrieves a facility data from Firebase
     * @param id
     * @param listener
     */
    public void getFacility(String id, OnDataReceivedListener<HashMap<String, Object>> listener) {
        facilities.document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                HashMap<String, Object> facility = (HashMap<String, Object>) documentSnapshot.getData();
                listener.onDataReceived(facility); // pass data to OnDataReceivedListener
            } else {
                listener.onDataReceived(null); // invalid id
            }
        }).addOnFailureListener(e -> {
            Log.w("Database", "Error retrieving facility", e);
            listener.onDataReceived(null); // error
        });
    }

    /**
     * The {@code getEntrant} method retrieves an entrant data from Firebase
     * @param id
     * @param listener
     */
    public void getEntrant(String id, OnDataReceivedListener<HashMap<String, Object>> listener) {
        entrants.document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                HashMap<String, Object> entrant = (HashMap<String, Object>) documentSnapshot.getData();
                listener.onDataReceived(entrant); // pass data to OnDataReceivedListener
            } else {
                listener.onDataReceived(null); // invalid id
            }
        }).addOnFailureListener(e -> {
            Log.w("Database", "Error retrieving entrant", e);
            listener.onDataReceived(null); // error
        });
    }

    /**
     * The {@code getEvent} method retrieves an event data from Firebase
     * @param id
     * @param listener
     */
    public void getEvent(String id, OnDataReceivedListener<HashMap<String, Object>> listener) {
        events.document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                HashMap<String, Object> event = (HashMap<String, Object>) documentSnapshot.getData();
                listener.onDataReceived(event); // pass data to OnDataReceivedListener
            } else {
                listener.onDataReceived(null); // invalid id
            }
        }).addOnFailureListener(e -> {
            Log.w("Database", "Error retrieving event", e);
            listener.onDataReceived(null); // error
        });
    }

    /**
     * The {@code getUser} method retrieves user data from Firebase
     * @param id
     * @param listener
     */
    public void getUser(String id, OnDataReceivedListener<HashMap<String, Object>> listener) {
        users.document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                HashMap<String, Object> user = (HashMap<String, Object>) documentSnapshot.getData();
                listener.onDataReceived(user); // pass data to OnDataReceivedListener
            } else {
                listener.onDataReceived(null); // invalid id
            }
        }).addOnFailureListener(e -> {
            Log.w("Database", "Error retrieving user", e);
            listener.onDataReceived(null); // error
        });
    }


    // example of using a custom query: finding organizers with user id 0:
    //
    // HashMap<String, Object> conditions = new HashMap<>();
    //    // condition: the user id is 0 (can put multiple conditions in hashmap)
    //    conditions.put("user", "0");
    //    // from the "organizers" collection, find facility meeting conditions
    //    db.query("organizers", conditions, Organizer -> {
    //        if (Organizer != null) {
    //            // results will be returned in a list since there could be
    //            // multiple results for a query.
    //            // to get just one, use .get(index), then to get a specific field, use
    //            // .get(field) on the result
    //            Log.d("user", "Organizer results: " + Organizer.get(0).get("facility"));
    //        } else {
    //            Log.d("user", "User not found or an error occurred.");
    //        }
    //    });
    /**
     * The {@code query} method queries a collection in Firebase
     * @param collectionName
     * @param conditions
     * @param listener
     */
    public void query(String collectionName, HashMap<String, Object> conditions, OnDataReceivedListener<List<HashMap<String, Object>>> listener) {
        CollectionReference collection = FirebaseFirestore.getInstance().collection(collectionName);
        Query query = collection;

        for (Map.Entry<String, Object> condition : conditions.entrySet()) {
            // make a query based on each of the conditions
            query = query.whereEqualTo(condition.getKey(), condition.getValue());
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<HashMap<String, Object>> results = new ArrayList<>();
            queryDocumentSnapshots.forEach(documentSnapshot -> results.add((HashMap<String, Object>) documentSnapshot.getData()));
            listener.onDataReceived(results); // pass data to OnDataReceivedListener
        }).addOnFailureListener(e -> {
            Log.w("Database", "Error executing custom query", e);
            listener.onDataReceived(null); // error
        });
    }

    /**
     * The {@code addParticipant} method adds a participant to Firebase
     * @param id
     * @param entrant
     * @param event
     * @param location
     * @param status
     */
    public void addParticipant(String id, String entrant, String event, GeoPoint location, String status){
        HashMap<String, Object> participant = new HashMap<>();
        // create a new participant and store the id
        participant.put("entrant", entrant);
        participant.put("event", event);
        participant.put("location", location);
        participant.put("status", status);
        participants.document(id).set(participant).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }

    /**
     * The {@code addOrganizer} method adds an organizer to Firebase
     * @param id
     * @param facility
     * @param user
     */
    public void addOrganizer(String id, String facility, String user){
        HashMap<String, String> organizer = new HashMap<>();
        // create a new organizer and store the id
        organizer.put("facility", facility);
        organizer.put("user", user);
        organizers.document(id).set(organizer).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }

    /**
     * The {@code addNotification} method adds a notification to Firebase
     * @param id
     * @param event
     * @param text
     * @param title
     */
    public void addNotification(String id, String event, String text, String title, String target,
                                String[] targetList){
        HashMap<String, Object> notification = new HashMap<>();
        // create a new notification and store the id
        notification.put("event", event);
        notification.put("text", text);
        notification.put("title", title);
        notification.put("target", target);
        notification.put("target list", Arrays.asList(targetList));
        if(id == null){
            notifications.add(notification).addOnSuccessListener(aVoid -> Log.d("database", "Notification added successfully"))
                    .addOnFailureListener(e -> Log.w("database", "Error adding notification", e));
        }else{
            notifications.document(id).set(notification).addOnSuccessListener(aVoid -> Log.d("database", "Notification added successfully"))
                    .addOnFailureListener(e -> Log.w("database", "Error adding notification", e));
        }

    }

    /**
     * The {@code addFacility} method adds a facility to Firebase
     * @param id
     * @param email
     * @param name
     * @param phone
     * @param photo
     */
    public void addFacility(String id, String email, String name, String phone, String photo){
        HashMap<String, String> facility = new HashMap<>();
        // create a new facility and store the id
        facility.put("email", email);
        facility.put("name", name);
        facility.put("phone", phone);
        facility.put("photo", photo);
        facilities.document(id).set(facility).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }

    /**
     * The {@code addEntrant} method adds an entrant to Firebase
     * @param id
     * @param locationEnabled
     * @param notificationsEnabled
     * @param user
     */
    public void addEntrant(String id, boolean locationEnabled, boolean notificationsEnabled, String user){
        HashMap<String, Object> entrant = new HashMap<>();
        // create a new entrant and store the id
        entrant.put("locationEnabled", locationEnabled);
        entrant.put("notificationsEnabled", notificationsEnabled);
        entrant.put("user", user);
        entrants.document(id).set(entrant).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }

    /**
     * The {@code addUser} method adds a user to Firebase
     * @param id
     * @param name
     * @param email
     * @param type
     * @param phone
     * @param profilePhoto
     */
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

    /**
     * The {@code addEvent} method adds an event to Firebase
     * @param id
     * @param capacity
     * @param dateAndTime
     * @param description
     * @param location
     * @param poster
     */
    public void addEvent(String id, String capacity, String dateAndTime, String description, GeoPoint location, String poster){
        HashMap<String, Object> event = new HashMap<>();
        // create a new event and store at the id
        event.put("capacity", capacity);
        event.put("dateAndTime", dateAndTime);
        event.put("description", description);
        event.put("location", location);
        event.put("poster", poster);
        events.document(id).set(event).addOnSuccessListener(aVoid -> Log.d("database", "User added successfully"))
                .addOnFailureListener(e -> Log.w("database", "Error adding user", e));
    }

    /*
     * Completely remove user from the database.
     * That includes: as a participant, organizer/entrant, their facility, and their events.
     */
    public void deleteUser(Context context, User viewed_user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Get rid of line if this still works (I moved this method from another file) - Mike
        String user_type = viewed_user.getUserType();

        Log.d("user", "deleting " + user_type + " " + viewed_user.getName());
        // Delete user from roles
        db.collection("entrants").document(viewed_user.getId()).delete();
        if ("organizer".equals(user_type)) {
            db.collection("organizers").document(viewed_user.getId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String facility_id = documentSnapshot.getString("facility");

                        // Delete events associated with facility
                        db.collection("created").whereEqualTo("facility", facility_id).get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                        String event_id = snapshot.getId();
                                        snapshot.getReference().delete();
                                        db.collection("events").document(event_id).delete();
                                    }

                                    // Delete user facility
                                    if (facility_id != null) {
                                        db.collection("facilities").document(facility_id).delete()
                                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to delete facility", e));
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to delete events", e));

                        db.collection("organizers").document(viewed_user.getId()).delete()
                                .addOnSuccessListener(unused -> Log.d("Firestore", "Organizer deleted"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to delete organizer", e));
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to get facility", e));

        }
        // Delete user from participants
        db.collection("participants").whereEqualTo("entrant", viewed_user.getId()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        db.collection("participants").document(snapshot.getId()).delete()
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to delete participant", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to retrieve participants", e));

        // Delete user from the users collection
        db.collection("users").document(viewed_user.getId()).delete()
                .addOnSuccessListener(unused -> Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to delete user", e));
    }

    /*
     * Completely remove event from the database.
     */
    public void deleteEvent(Context context, Event event) {
        // Delete event from the "created" collection
        db.collection("created").whereEqualTo("event", event.getEventID()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                db.collection("created").document(snapshot.getId()).delete()
                        .addOnFailureListener(e -> Log.e("Firestore", "Failed to delete event", e));
            }

            // Delete event from the "events" collection
            db.collection("events").document(event.getEventID()).delete()
                    .addOnSuccessListener(unused -> Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show());
        });

        // Delete participants of the event ("participants" collection)
        db.collection("participants").whereEqualTo("event", event.getEventID()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                db.collection("participants").document(snapshot.getId()).delete()
                        .addOnFailureListener(e -> Log.e("Firestore", "Failed to delete participant", e));
            }
        });

        // Delete notifications associated with the event ("notification" collection)
        db.collection("notification").whereEqualTo("event", event.getEventID()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                db.collection("notification").document(snapshot.getId()).delete()
                        .addOnFailureListener(e -> Log.e("Firestore", "Failed to delete notification", e));
            }
        });
    }
}
