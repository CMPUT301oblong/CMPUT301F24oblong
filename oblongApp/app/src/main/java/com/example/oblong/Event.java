package com.example.oblong;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {
    private String eventName;
    private String eventDescription;
    private Date eventCloseDate;
    private Integer eventCapacity;

    public Event(String eventName, String eventDescription, Date eventCloseDate, Integer eventCapacity ) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventCloseDate = eventCloseDate;
        this.eventCapacity = eventCapacity;
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

    public Integer getEventCapacity() {
        return eventCapacity;
    }

    public void setEventCapacity(Integer eventCapacity) {
        this.eventCapacity = eventCapacity;
    }
}
