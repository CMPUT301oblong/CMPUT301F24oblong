package com.example.oblong;


import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The {@code Notification} class handles the notifications that the organizer sends to users
 * Also retrieves and stores the notification data within the Firebase using the notifID
 */

public class Notification {
    private String label;
    private String content;
    private String target;
    private ArrayList<String> targetList;
    private String eventID;
    private String notifID;

    /**
     * The {@code Notification} constructor retrieves notification data belonging to an notifID
     * @param notifID
     */
    public Notification(String notifID){
        this.notifID = notifID;
        Database db = new Database();
        db.getNotification(notifID, data -> {
            if(data!=null) {
                Log.d("notif label", (String) data.get("title"));
                setNotifInformation(data);
            }
            else{
                Log.d("notification", "notification not found");
            }
        });
    }

    /**
     * The {@code setNotifInformation} method sets the notification data to the instance variables
     * in the Firebase
     * @param data HashMap object containing the notification data
     */
    private void setNotifInformation(HashMap<String, Object> data){
        this.eventID = (String) data.get("event");
        Log.d("notif event", this.eventID);

        this.label = (String) data.get("title");
        Log.d("notif title", this.label);

        this.content = (String) data.get("text");
        Log.d("notif content", this.content);

        this.target = (String) data.get("target");
        Log.d("notif target", this.target);

        this.targetList = new ArrayList<String>((List<String>) data.get("target list"));
        Log.d("notif target list", String.valueOf(this.targetList));
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
     * @param label String containing text for the title of a notification
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * The {@code getContent} method returns the content of a notification
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * The {@code setContent} method sets the content of the notification
     * @param content String containing text for the content of a notification
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * The {@code getTarget} method returns the target of the notification
     * @return
     */
    public String getTarget() { return target; }

    /**
     * The {@code setTarget} method sets the target of the notification
     * @param target String containing the target status of a notification
     */
    public void setTarget(String target){ this.target = target; }

    /**
     * The {@code getNotifID} method returns the notifID of the notification
     * @return
     */
    public String getNotifID(){ return notifID; }

    /**
     * The {@code getEventID} method returns the eventID of the notification
     * @return
     */
    public String getEventID(){ return eventID; }

    /**
     * The {@code setEventID} method sets the eventID of the notification
     * @param eventID String containing the eventID of the eevent associated with a notification
     */
    public void setEventID(String eventID){ this.eventID = eventID; }

    /**
     * The {@code getTargetList} method gets the targetList of the notification
     * @return
     */
    public ArrayList<String> getTargetList() {
        return targetList;
    }

    /**
     * The {@code getTargetList} method sets the targetList of the notification
     * @param targetList ArrayList of entrantIDs
     */
    public void setTargetList(ArrayList<String> targetList) {
        this.targetList = targetList;
    }
}
