package com.example.oblong;

import com.google.firebase.Timestamp;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class EventTests {

    private Event event;
    private Date eventCloseDate;

    @Before
    public void setUp() {
        eventCloseDate = new Date();
        event = new Event("1");
        event.setEventName("Sample Event");
        event.setEventDescription("This is a sample description.");
        event.setEventCloseDate(eventCloseDate);
        event.setEventCapacity(100L);
        event.setPoster("samplePosterURL");
    }



    @Test
    public void testSetAndGetEventName() {
        event.setEventName("New Event Name");
        assertEquals("New Event Name", event.getEventName());
    }


    @Test
    public void testSetAndGetEventDescription() {
        event.setEventDescription("Updated description.");
        assertEquals("Updated description.", event.getEventDescription());
    }


    @Test
    public void testSetAndGetEventCloseDate() {
        Date newDate = new Date();
        event.setEventCloseDate(newDate);
        String datePattern = "dd/MM/yyyy";
        DateFormat closeDateFormat = new SimpleDateFormat(datePattern);
        assertEquals(closeDateFormat.format(newDate), event.getEventCloseDate());
    }


    @Test
    public void testSetAndGetEventCapacity() {
        event.setEventCapacity(150L);
        assertEquals(150, event.getEventCapacity());
    }



    @Test
    public void testSetAndGetEventID() {
        event.setEventID("2");
        assertEquals("2", event.getEventID());
    }


    @Test
    public void testSetAndGetPoster() {
        // Test with a valid poster URL
        String posterUrl = "updatedPosterURL";
        event.setPoster(posterUrl);
        assertEquals(posterUrl, event.getPoster());

        // Test with a short poster URL
        String shortPoster = "short";
        event.setPoster(shortPoster);
        String defaultPoster = "iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAKaSURBVHgB7ZlBbuIwFIYfobACiSULhNIbzA2GOcnkBtM5AekJZuYGmZOUnqDcIClCYtkukRDQ95AtGcc4tkkcV+WTLDm2E/1/nOc4LwA3bnxtOpc6RshgMPjV6XRmVKAdllj+rlar/5cGKA2Mx+O43+8/YTWGMCh2u91ss9m8yh2RanRg4om41+st6KmQO0oGptPpTwhLPCemR1puvFMMfBAPjsdj1u12fxdF8Q4eiZH9fj/H+Et4G4vFR3FcKQZwBo7iMYqP8zx/hRZAD6PD4fAmtmFAn2mOqi7SlnjCZNYrDYTOHTgymUy+4zOZYvmGhyOMlQXWM92a3QROM4BxMo+iaMGC6rS0sXqGxubgEWsDbJlNL14wilKaHfCEtQFxWdOMScET1gbwWZ+Jx7TM4l2/F9tYXHjBJQbOlrbtdnsqEiPwhMsjtBSPcY9S4N4pF9toRaq6DsUSljcsOYsrJ1wMpAZjMl0/E0xjaKZiqruasDaAb8dn0KxC+OpPde8CQbyMkwmn9wAKpA1Vgnd6wZreqY7B/GO9Xj9eOk8jnmNtonIzJ2+eXDEQL5LwWazS42UvpBGfsCJjPBPOeyFTdOKFuwyKMRlr19LoDJiIJ1g9UYxTnXtGYwZMxXM0JrQ0YsBWPMfFRO0GXMVzbE3UauBa8RwbE7UZqEs8x9RELQbqFs8xOfdqA02JN+UqA22LJ5wNhCCecM1KBCGecM1KZIou7+IJKwOhiScqDfCcfBvi6UdL1ZjSdpq+rMTUyXA4/IN5+dOvHsX5jYmnG4cJAznLt5THqb7I6KQUwqR0w0oGWE7+BcL7S1Og+Hu5sRQDlJOnj3OqQjgUmAGcqTq0H+wUuBgTDxgT3lKFIiweKdvxz/cvrhs3PgsfKfsY4D61rB4AAAAASUVORK5CYII=";
        assertEquals(defaultPoster, event.getPoster());
    }

    @Test
    public void testSetAndGetStatus() {
        event.setStatus("Attending");
        assertEquals("Attending", event.getStatus());
    }
}

