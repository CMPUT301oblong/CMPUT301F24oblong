package com.example.oblong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class NotificationUnitTest {
    private Database db = new Database();

    @Test
    public void getNotificationDoesNotExist() throws InterruptedException {
        Notification dne = new Notification("NotificationDoesNotExist");
        TimeUnit.SECONDS.sleep(2);
        assertNull(dne.getContent());
    }

    @Test
    public void getNotification(){
        setUP();
        Notification notification = new Notification("NotificationUnitTest");
        assertEquals("NotificationUnitTest", notification.getNotifID());
    }

    @Test
    public void getNotificationLabel() throws InterruptedException {
        setUP();
        Notification notification = new Notification("NotificationUnitTest");
        TimeUnit.SECONDS.sleep(2);
        assertEquals("NotificationUnitTestTitle", notification.getLabel());
    }

    @Test
    public void getNotificationContent() throws InterruptedException {
        Notification notification = new Notification("NotificationUnitTest");
        TimeUnit.SECONDS.sleep(2);
        assertEquals("NotificationUnitTestContent", notification.getContent());
    }

    @Test
    public void getNotificationEvent() throws InterruptedException {
        Notification notification = new Notification("NotificationUnitTest");
        TimeUnit.SECONDS.sleep(2);
        assertEquals("NotificationUnitTestEvent", notification.getEventID());
    }

    @Test
    public void getNotificationTarget() throws InterruptedException {
        Notification notification = new Notification("NotificationUnitTest");
        TimeUnit.SECONDS.sleep(2);
        assertEquals("NotificationUnitTestTarget", notification.getTarget());
    }

    @Test
    public void getNotificationTargetList() throws InterruptedException {
        Notification notification = new Notification("NotificationUnitTest");
        TimeUnit.SECONDS.sleep(2);
        assertEquals(Arrays.asList("NotificationUnitTestEntrant"), notification.getTargetList());
    }

    @Test
    public void setNotificationLabel() throws InterruptedException {
        setUP();
        Notification notification = new Notification("NotificationUnitTest");
        TimeUnit.SECONDS.sleep(2);
        notification.setLabel("newLabel!");
        assertEquals("newLabel!", notification.getLabel());
    }

    @Test
    public void setNotificationContent() throws InterruptedException {
        Notification notification = new Notification("NotificationUnitTest");
        TimeUnit.SECONDS.sleep(2);
        notification.setContent("new content!");
        assertEquals("new content!", notification.getContent());
    }

    @Test
    public void setNotificationEvent() throws InterruptedException {
        Notification notification = new Notification("NotificationUnitTest");
        TimeUnit.SECONDS.sleep(2);
        notification.setEventID("new event");
        assertEquals("new event", notification.getEventID());
    }

    @Test
    public void setNotificationTarget() throws InterruptedException {
        Notification notification = new Notification("NotificationUnitTest");
        TimeUnit.SECONDS.sleep(2);
        notification.setTarget("new target");
        assertEquals("new target", notification.getTarget());
    }

    @Test
    public void setNotificationTargetList() throws InterruptedException {
        Notification notification = new Notification("NotificationUnitTest");
        TimeUnit.SECONDS.sleep(2);
        ArrayList<String> list = new ArrayList<>();
        list.add("entrant1!");
        notification.setTargetList(list);
        assertEquals(list, notification.getTargetList());
    }

    private void setUP(){
        db.addNotification("NotificationUnitTest", "NotificationUnitTestEvent", "NotificationUnitTestContent",
                "NotificationUnitTestTitle", "NotificationUnitTestTarget",
                Arrays.asList("NotificationUnitTestEntrant").toArray(new String[0]));

    }
}
