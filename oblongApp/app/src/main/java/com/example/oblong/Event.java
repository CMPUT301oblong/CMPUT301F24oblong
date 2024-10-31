package com.example.oblong;

public class Event {

    private String eventName;
    private String eventDescription;
    private String drawDate;
    private String uniqueID;
    private String imageUrl;

    // Constructor
    public Event(String eventName, String eventDescription, String drawDate, String uniqueID, String imageUrl) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.drawDate = drawDate;
        this.uniqueID = uniqueID;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
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

    public String getDrawDate() {
        return drawDate;
    }

    public void setDrawDate(String drawDate) {
        this.drawDate = drawDate;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
