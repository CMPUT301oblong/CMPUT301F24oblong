package com.example.oblong;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Event {
    private String eventName;
    private String eventDescription;
    private Date eventCloseDate;
    private Long eventCapacity;


    /*public Event(String eventName, String eventDescription, Date eventCloseDate, Long eventCapacity ) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventCloseDate = eventCloseDate;
        this.eventCapacity = eventCapacity;
    }*/

    public Event(String eventID){
        //Access database

        Database db = new Database();

        db.getEvent(eventID,data -> {
            if(data!=null) {
                Log.d("event name", (String) data.get("name"));
                setEventInformation(data);
            }
            else{
                Log.d("event", "event not found");
            }
        });

    }

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

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventCloseDate() {
        String datePattern = "dd/MM/yyyy";
        DateFormat closeDateFormat = new SimpleDateFormat(datePattern);
        return closeDateFormat.format(this.eventCloseDate);
    }

    public void setEventCloseDate(Date eventCloseDate) {
        this.eventCloseDate = eventCloseDate;
    }

    public Long getEventCapacity() {
        return eventCapacity;
    }

    public void setEventCapacity(Long eventCapacity) {
        this.eventCapacity = eventCapacity;
    }
}