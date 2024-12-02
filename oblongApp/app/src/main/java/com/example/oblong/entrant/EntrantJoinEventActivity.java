package com.example.oblong.entrant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.imageUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;

/**
 * Activity class for joining an event as an entrant.
 *
 * <p>This activity displays the details of an event, such as its title, description, date,
 * and poster image, and provides the option for the user to join the event or cancel the action.</p>
 */
public class EntrantJoinEventActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    GeoPoint entrantLocation = new GeoPoint(9, 9);
    private Database db;
    private String event_id;

    /**
     * Initializes the activity, sets up the UI, and handles user interactions.
     *
     * <p>In this method, the activity's layout is set, and window insets are applied to ensure
     * that the content is displayed edge-to-edge. It also retrieves event data from the intent's
     * extras and populates the UI with this data. The join and cancel buttons are set up to
     * perform respective actions when clicked.</p>
     *
     * @param savedInstanceState The saved instance state containing the activity's previously
     *                           saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize
        db = new Database();
        Button joinButton = findViewById(R.id.join_event_join_button);
        Button cancelButton = findViewById(R.id.join_event_cancel_button);
        TextView title = findViewById(R.id.activity_join_event_banner_2);
        TextView desc = findViewById(R.id.activity_join_event_event_description);
        TextView date = findViewById(R.id.join_event_draw_date);
        ImageView poster = findViewById(R.id.join_event_imageView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        boolean locationRequired;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        HashMap<String, Object> event = (HashMap<String, Object>) bundle.get("event");

        title.setText((String)event.get("name"));
        desc.setText((String) event.get("description"));
        Timestamp fireDate = (Timestamp) event.get("dateAndTime");
        String img = (String) event.get("poster");

        //TODO: check if location is required
//        if (event.get("locationRequired") != null && event.get("locationRequired").equals("1")){
//            locationRequired = true;
//            Toast.makeText(this, "Location permissions are required to join this event", Toast.LENGTH_SHORT).show();
//        } else {
        locationRequired = false;
//        }

        date.setText(fireDate.toDate().toString());
        if(Objects.equals(img, "image")){
            img = "iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAKaSURBVHgB7ZlBbuIwFIYfobACiSULhNIbzA2GOcnkBtM5AekJZuYGmZOUnqDcIClCYtkukRDQ95AtGcc4tkkcV+WTLDm2E/1/nOc4LwA3bnxtOpc6RshgMPjV6XRmVKAdllj+rlar/5cGKA2Mx+O43+8/YTWGMCh2u91ss9m8yh2RanRg4om41+st6KmQO0oGptPpTwhLPCemR1puvFMMfBAPjsdj1u12fxdF8Q4eiZH9fj/H+Et4G4vFR3FcKQZwBo7iMYqP8zx/hRZAD6PD4fAmtmFAn2mOqi7SlnjCZNYrDYTOHTgymUy+4zOZYvmGhyOMlQXWM92a3QROM4BxMo+iaMGC6rS0sXqGxubgEWsDbJlNL14wilKaHfCEtQFxWdOMScET1gbwWZ+Jx7TM4l2/F9tYXHjBJQbOlrbtdnsqEiPwhMsjtBSPcY9S4N4pF9toRaq6DsUSljcsOYsrJ1wMpAZjMl0/E0xjaKZiqruasDaAb8dn0KxC+OpPde8CQbyMkwmn9wAKpA1Vgnd6wZreqY7B/GO9Xj9eOk8jnmNtonIzJ2+eXDEQL5LwWazS42UvpBGfsCJjPBPOeyFTdOKFuwyKMRlr19LoDJiIJ1g9UYxTnXtGYwZMxXM0JrQ0YsBWPMfFRO0GXMVzbE3UauBa8RwbE7UZqEs8x9RELQbqFs8xOfdqA02JN+UqA22LJ5wNhCCecM1KBCGecM1KZIou7+IJKwOhiScqDfCcfBvi6UdL1ZjSdpq+rMTUyXA4/IN5+dOvHsX5jYmnG4cJAznLt5THqb7I6KQUwqR0w0oGWE7+BcL7S1Og+Hu5sRQDlJOnj3OqQjgUmAGcqTq0H+wUuBgTDxgT3lKFIiweKdvxz/cvrhs3PgsfKfsY4D61rB4AAAAASUVORK5CYII=";
        }
        poster.setImageBitmap(imageUtils.base64ToBitmap(img));

        event_id = (String)event.get("eventID");

        // cancel button listener
        cancelButton.setOnClickListener(v -> {
            startActivity(new Intent(EntrantJoinEventActivity.this, EntrantBaseActivity.class));
        });

        // join button listener
        joinButton.setOnClickListener(v -> {
            if(locationRequired) {
                getPermissionAndJoin();
            }else{
                // get current user (asynchronous)
                db.getCurrentUser(userId -> {
                    String participantId = userId + event_id;
                    String status = "waitlisted";

                    // Add user as a participant
                    db.addParticipant(participantId, userId, event_id, entrantLocation, status);
                    startActivity(new Intent(EntrantJoinEventActivity.this, EntrantBaseActivity.class));
                });
            }
        });
    }

    private void getPermissionAndJoin() {
        // request permissions every time
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                1001);
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == 1001) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed to get location
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    // Logic to handle location object
                                    double lat = location.getLatitude();
                                    double lng = location.getLongitude();
                                    entrantLocation = new GeoPoint(lat, lng);

                                    // get current user (asynchronous)
                                    db.getCurrentUser(userId -> {
                                        String participantId = userId + event_id;
                                        String status = "waitlisted";

                                        // Add user as a participant
                                        db.addParticipant(participantId, userId, event_id, entrantLocation, status);
                                        startActivity(new Intent(EntrantJoinEventActivity.this, EntrantBaseActivity.class));
                                    });
                                }
                            }
                        });
            } else {
                // Permissions denied, inform the user
                Toast.makeText(this, "Enable location permissions in settings to join event", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
