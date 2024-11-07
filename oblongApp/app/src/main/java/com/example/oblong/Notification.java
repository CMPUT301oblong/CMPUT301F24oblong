package com.example.oblong;


import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;

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

    private void setNotifInformation(HashMap<String, Object> data){
        this.label = (String) data.get("title");
        Log.d("notif title", this.label);

        this.content = (String) data.get("text");
        Log.d("notif content", this.content);

        this.targets = (String) data.get("targets");
        Log.d("notif targets", this.targets);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTargets() { return targets; }

    public void setTargets(String targets){ this.targets = targets; }

    public String getEventID(){ return eventID; }

    public void setEventID(String eventID) { this.eventID = eventID; }
}
