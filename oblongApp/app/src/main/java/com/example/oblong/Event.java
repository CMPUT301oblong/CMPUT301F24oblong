package com.example.oblong;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Event implements Serializable {
    private String eventID;
    private String eventName;
    private String eventDescription;
    private Date eventCloseDate;
    private Long eventCapacity;
    private Long eventWaitlistCapacity;
    private String poster;
    private String attendingStatus;
    private String qrID;


    /*public Event(String eventID, String eventName, String eventDescription, Date eventCloseDate, Long eventCapacity ) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventCloseDate = eventCloseDate;
        this.eventCapacity = eventCapacity;
    }*/

    /**
     * The {@code Event} method retrieves an event data from Firebase
     * @param eventID
     */
    public Event(String eventID){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventDoc = db.collection("events").document(eventID);

        eventDoc.get().addOnCompleteListener(task -> {
            DocumentSnapshot data = task.getResult();
            this.eventCapacity = (Long) data.getLong("capacity");
            Log.d("event cap", Long.toString(this.eventCapacity));

            this.eventName = (String) data.getString("name");
            Log.d("event name", this.eventName);

            if(data.contains("waitlistCapacity")){
                this.eventWaitlistCapacity = data.getLong("waitlistCapacity");
            }else {
                this.eventWaitlistCapacity = null;
            }

            Timestamp timestamp = (Timestamp) data.getTimestamp("dateAndTime");
            Date date = timestamp.toDate();
            this.eventCloseDate = date;

            //this.eventCloseDate =  newEvent.get("dateAndTime").toDate();
            this.eventDescription = (String) data.get("description");

            setPoster(data.getString("poster"));

            this.eventID = eventID;

            this.qrID = data.getString("qrID");
        });



        /*db.getEvent(eventID,data -> {
            if(data!=null) {
                Log.d("event name", (String) data.get("name"));
                setEventInformation(data);
            }
            else{
                Log.d("event", "event not found");
            }
        });*/

    }


    /**
     * The {@code setEventInformation} method sets the event information
     * @param data
     */
    private void setEventInformation(HashMap<String, Object> data){
        this.eventCapacity = (Long) data.get("capacity");
        Log.d("event cap", Long.toString(this.eventCapacity));

        this.eventName = (String) data.get("name");
        Log.d("event name", this.eventName);

        Timestamp timestamp = (Timestamp) data.get("dateAndTime");
        Date date = timestamp.toDate();
        this.eventCloseDate = date;

        //this.eventCloseDate =  newEvent.get("dateAndTime").toDate();
        this.eventDescription = (String) data.get("description");
    }

    public void drawOneEntrant(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("participants").whereEqualTo("event", eventID).whereEqualTo("status", "waitlisted").get().addOnSuccessListener(task ->{
            List<DocumentSnapshot> allWaitlisted = task.getDocuments();
            if (allWaitlisted.size() != 0) {
                int amountOfWaitlisted = task.getDocuments().size();
                Random rand = new Random();
                int selectedUser = rand.nextInt(amountOfWaitlisted);
                String documentID = allWaitlisted.get(selectedUser).getId();
                db.collection("participants").document(documentID).update("status", "selected");

                //get new selected entrant 
                String entrant = (String) db.collection("participants").document(documentID).get().getResult().get("entrant");

                //create selected notification
                Database tempD = new Database();
                String newNotifIDSelected = db.collection("notifications").document().getId();
                String label = this.eventName+": Congratulations! You've been selected!";
                String content = "Congratulations on being selected to attend our event! Please accept your invitation " +
                        "by visiting your \"Events\" tab and viewing the details for our event.";
                tempD.addNotification(newNotifIDSelected, this.eventID, content, label, "Selected", Arrays.asList(entrant).toArray(new String[0]));

                //add selected notification to entrant notificationsList
                HashMap<String, Object> entrantUpdate = new HashMap<>();
                entrantUpdate.put("notificationsList", FieldValue.arrayUnion(newNotifIDSelected));
                tempD.updateDocument("entrants", entrant, entrantUpdate, v -> {});
            }
        });
    }

    /**
     * The {@code getEventName} method gets the event name
     * @return
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * The {@code setEventName} method sets the event name
     * @param eventName
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * The {@code getEventDescription} method gets the event description
     * @return
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * The {@code setEventDescription} method sets the event description
     * @param eventDescription
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /**
     * The {@code getEventCloseDate} method gets the event close date
     * @return
     */
    public String getEventCloseDate() {
        String datePattern = "dd/MM/yyyy";
        DateFormat closeDateFormat = new SimpleDateFormat(datePattern);
        return closeDateFormat.format(this.eventCloseDate);
    }
    public String getEventCloseLong() {
        String datePattern = "dd MMM yyyy";
        DateFormat closeDateFormat = new SimpleDateFormat(datePattern);
        return closeDateFormat.format(this.eventCloseDate);
    }

    public String getEventCloseMon() {
        String datePattern = "MMM";
        DateFormat closeDateFormat = new SimpleDateFormat(datePattern);
        return closeDateFormat.format(this.eventCloseDate);
    }

    public String getEventCloseDay() {
        String datePattern = "dd";
        DateFormat closeDateFormat = new SimpleDateFormat(datePattern);
        return closeDateFormat.format(this.eventCloseDate);
    }

    public String getEventCloseTime() {
        String timePattern = "HH:mm";
        DateFormat closeTimeFormat = new SimpleDateFormat(timePattern);
        return closeTimeFormat.format(this.eventCloseDate);
    }

    /**
     * The {@code setEventCloseDate} method sets the event close date
     * @param eventCloseDate
     */
    public void setEventCloseDate(Date eventCloseDate) {
        this.eventCloseDate = eventCloseDate;
    }

    /**
     * The {@code getEventCapacity} method gets the event capacity
     * @return
     */
    public int getEventCapacity() {
        return Math.toIntExact(eventCapacity);
    }

    /**
     * The {@code setEventCapacity} method sets the event capacity
     * @param eventCapacity
     */
    public void setEventCapacity(Long eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    /**
     * The {@code getEventID} method gets the event ID
     * @return
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * The {@code setEventID} method sets the event ID
     * @param eventID
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * The {@code getPoster} method gets the poster
     * @return
     */
    public String getPoster() {
        return poster;
    }

    /**
     * The {@code setPoster} method sets the poster
     * @param poster
     */
    public void setPoster(String poster) {
        if (poster.length() < 10) {
            this.poster = "iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAKaSURBVHgB7ZlBbuIwFIYfobACiSULhNIbzA2GOcnkBtM5AekJZuYGmZOUnqDcIClCYtkukRDQ95AtGcc4tkkcV+WTLDm2E/1/nOc4LwA3bnxtOpc6RshgMPjV6XRmVKAdllj+rlar/5cGKA2Mx+O43+8/YTWGMCh2u91ss9m8yh2RanRg4om41+st6KmQO0oGptPpTwhLPCemR1puvFMMfBAPjsdj1u12fxdF8Q4eiZH9fj/H+Et4G4vFR3FcKQZwBo7iMYqP8zx/hRZAD6PD4fAmtmFAn2mOqi7SlnjCZNYrDYTOHTgymUy+4zOZYvmGhyOMlQXWM92a3QROM4BxMo+iaMGC6rS0sXqGxubgEWsDbJlNL14wilKaHfCEtQFxWdOMScET1gbwWZ+Jx7TM4l2/F9tYXHjBJQbOlrbtdnsqEiPwhMsjtBSPcY9S4N4pF9toRaq6DsUSljcsOYsrJ1wMpAZjMl0/E0xjaKZiqruasDaAb8dn0KxC+OpPde8CQbyMkwmn9wAKpA1Vgnd6wZreqY7B/GO9Xj9eOk8jnmNtonIzJ2+eXDEQL5LwWazS42UvpBGfsCJjPBPOeyFTdOKFuwyKMRlr19LoDJiIJ1g9UYxTnXtGYwZMxXM0JrQ0YsBWPMfFRO0GXMVzbE3UauBa8RwbE7UZqEs8x9RELQbqFs8xOfdqA02JN+UqA22LJ5wNhCCecM1KBCGecM1KZIou7+IJKwOhiScqDfCcfBvi6UdL1ZjSdpq+rMTUyXA4/IN5+dOvHsX5jYmnG4cJAznLt5THqb7I6KQUwqR0w0oGWE7+BcL7S1Og+Hu5sRQDlJOnj3OqQjgUmAGcqTq0H+wUuBgTDxgT3lKFIiweKdvxz/cvrhs3PgsfKfsY4D61rB4AAAAASUVORK5CYII=";
        } else {
            this.poster = poster;
        }
    }

    public void setStatus(String status) {
        this.attendingStatus = status;
    }

    public String getStatus() {
        return attendingStatus;
    }

    public String setQrID(String qrID){
        return this.qrID = qrID;
    }

    public String getQrID() {
        return qrID;
    }
}
