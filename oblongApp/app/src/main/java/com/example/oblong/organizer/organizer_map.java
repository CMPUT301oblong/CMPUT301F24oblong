package com.example.oblong.organizer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;

//https://sanaebadi97.medium.com/learn-how-to-work-with-osm-map-in-android-app-ac42f933cbd3
public class organizer_map extends AppCompatActivity {

    private MapView map;
    private IMapController mapController;
    private Event event;
    private String eventId;
    private Database db;


    private static final String TAG = "OsmActivity";


    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceBundle){
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.map_organizer);
        db = new Database();

        ImageView backArrow = findViewById(R.id.backArrow);

        map = findViewById(R.id.osmmap);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(5);
        GeoPoint startPoint = new GeoPoint(53.52750236977495,  -113.52881864947085);
        mapController.setCenter(startPoint);

        // add entrants to the map
        addEntrants();


        backArrow.setOnClickListener(view ->{
            this.onBackPressed();
        });
    }

    /**
     * Adds a marker to the map at the specified GeoPoint with the given title.
     *
     * @param location The GeoPoint where the marker will be placed.
     * @param title    The title of the marker.
     */
    private void addMarker(GeoPoint location, String title) {
        Marker marker = new Marker(map);
        marker.setPosition(location); // Set marker location
        marker.setTitle(title); // Set marker title (name)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); // Anchor point
        map.getOverlays().add(marker); // Add marker to the map
        map.invalidate(); // Refresh the map
    }


    private void addEntrants(){
        Bundle bundle = getIntent().getExtras();
        event = (Event) bundle.get("EVENT");
        eventId = event.getEventID();

        HashMap<String, Object> conditions = new HashMap<>();
        // condition: the user id is 0 (can put multiple conditions in hashmap)
        conditions.put("event", eventId);
        // from the "organizers" collection, find facility meeting conditions
        db.query("participants", conditions, participants -> {
            if (participants != null) {

                Log.d("user", "Organizer results: " + participants);
                for (int i = 0; i < participants.size(); i++){
                    com.google.firebase.firestore.GeoPoint point = (com.google.firebase.firestore.GeoPoint) participants.get(i).get("location");
                    Log.d("user", "Organizer results: " + point);

                    double lat = point.getLatitude();
                    double lng = point.getLongitude();

                    GeoPoint entrantPin = new GeoPoint(lat,  lng);
                    String userId = (String) participants.get(i).get("entrant");
                    db.getUser(userId, user -> {
                        if (user != null) {
                            // Process data
                            Log.d("user", "User Name: " + user.get("name")); // print user's name to console
                            addMarker(entrantPin, (String)user.get("name"));
                        } else {
                            Log.d("user", "User not found or an error occurred.");
                        }
                    });
                }
            } else {
                Log.d("user", "User not found or an error occurred.");
            }
        });
    }

}
