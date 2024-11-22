package com.example.oblong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.util.Log;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import com.example.oblong.Database;

public class EntrantProfilePermissionTests {

    Database db = new Database();


    /**
     * Tests disabled geoPermissions for an entrant
     */
    @Test
    public void testGeoPermissionsFalse() throws InterruptedException {
        db.addEntrant("locationEnabled", false, false, "465413541");

        TimeUnit.SECONDS.sleep(10);
        db.getEntrant("locationEnabled", entrant->{
            assertFalse((boolean)entrant.get("locationEnabled"));
        });
    }


    /**
     * Tests disabled notifications for an entrant
     */
    @Test
    public void testNotifPermissionsFalse() throws InterruptedException {
        db.addEntrant("684531684531", false, false, "351351");

        TimeUnit.SECONDS.sleep(10);
        db.getEntrant("684531684531", entrant->{
            assertFalse((boolean)entrant.get("notificationsEnabled"));
        });
    }


    /**
     * Tests disabled geoPermissions for an entrant
     */
    @Test
    public void testGeoPermissionsTrue() throws InterruptedException {
        db.addEntrant("98645135648", true, false, "9864513215648541");

        TimeUnit.SECONDS.sleep(10);
        db.getEntrant("98645135648", entrant->{
            assertTrue((boolean)entrant.get("locationEnabled"));
        });
    }


    /**
     * Tests disabled notifications for an entrant
     */
    @Test
    public void testNotifPermissionsTrue() throws InterruptedException {
        db.addEntrant("864531648531", false, true, "86435");

        TimeUnit.SECONDS.sleep(10);
        db.getEntrant("864531648531", entrant->{
            assertTrue((boolean)entrant.get("notificationsEnabled"));
        });
    }
}