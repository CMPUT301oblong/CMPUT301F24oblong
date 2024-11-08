package com.example.oblong;


import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;

/**
 * The {@code Notification} class handles the notifications that the organizer sends to users
 * Also retreives and stores the notification data within the Firebase using the evntID
 */

public class Notification {
    private String label;
    private String content;
    private String targets;
    private String eventID;

    /*
    public Notification(String label, String content, String targets) {
        this.label = label;
        this.content = content;
        this.targets = targets;
    }*/

    /**
     * The {@code Notification} method retrieves notification data belonging to an eventID
     * @param eventID
     */
    public Notification(String eventID){
        this.eventID = eventID;
        Database db = new Database();
        db.getNotification(eventID, data -> {
            if(data!=null) {
                Log.d("notif label", (String) data.get("title"));
                setNotifInformation(data);
            }
            else{
                Log.d("event", "event not found");
            }
        });
    }

    /**
     * The {@code setNotifInformation} method sets the notification data to the instance variables
     * in the Firebase
     * @param data
     */
    private void setNotifInformation(HashMap<String, Object> data){
        this.label = (String) data.get("title");
        Log.d("notif title", this.label);

        this.content = (String) data.get("text");
        Log.d("notif content", this.content);

        this.targets = (String) data.get("targets");
        Log.d("notif targets", this.targets);
    }

    /**
     * The {@code getLabel} method returns the label of the notification
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * The {@code setLabel} method sets the label of the notification
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * The {@code getContent} method returns the content of the notification
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * The {@code setContent} method sets the content of the notification
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * The {@code getTargets} method returns the targets of the notification
     * @return
     */
    public String getTargets() { return targets; }

    /**
     * The {@code setTargets} method sets the targets of the notification
     * @param targets
     */
    public void setTargets(String targets){ this.targets = targets; }

    /**
     * The {@code getEventID} method returns the eventID of the notification
     * @return
     */
    public String getEventID(){ return eventID; }

    /**
     * The {@code setEventID} method sets the eventID of the notification
     * @param eventID
     */
    public void setEventID(String eventID) { this.eventID = eventID; }
}
