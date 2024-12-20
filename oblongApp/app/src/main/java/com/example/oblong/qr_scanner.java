package com.example.oblong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.entrant.EntrantBaseActivity;
import com.example.oblong.entrant.EntrantEventDescriptionActivity;
import com.example.oblong.entrant.EntrantEventDetails;
import com.example.oblong.entrant.EntrantJoinEventActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code qr_scanner} class handles QR code scanning functionality, retrieves event details
 * from Firebase Firestore, and navigates the user to the appropriate activity based on the
 * association of the scanned event.
 *
 * <p>This class is used to scan QR codes and extract event information from Firestore using the
 * unique ID encoded in the QR code. If the event is already associated with the user, they are
 * navigated to the event description page; otherwise, they are directed to the event joining page.
 */
public class qr_scanner extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final Database db = new Database();
    String status;



    /**
     * Initializes the Firebase instances and checks for camera permissions.
     * If permission is granted, initializes the QR code scanner.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.custom_capture_layout);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.qr_title_text), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            initQRCodeScanner();
        }
    }

    /**
     * Initializes the QR code scanner using {@code IntentIntegrator}.
     * Configures the scanner to scan QR codes with specific orientations and prompts.
     */
    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan a QR code");
        integrator.setCaptureActivity(CustomCaptureActivity.class); // use custom layout
        integrator.initiateScan();
    }

    /**
     * Handles the result of the QR code scan. If content is found, processes the scanned data.
     *
     * @param requestCode The request code originally supplied to {@link #startActivityForResult}.
     * @param resultCode  The result code returned by the child activity through its {@code setResult()}.
     * @param data        An Intent, which can return result data to the caller.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
                this.onBackPressed();

            } else {
                // Retrieve the eventID from Firestore via the qrID stored in the qr code before calling handleScannedData
                retrieveEventID(result.getContents(), eventID -> handleScannedData(eventID));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Checks the association of the scanned event with the user in Firestore.
     * Based on the association, directs to the appropriate activity.
     *
     * @param event The unique ID of the event extracted from the QR code.
     */
    private void handleScannedData(String event) {
        db.getCurrentUser(userId -> {
            String participant = userId + event;
            // must be atomic since multiple "threads" could access at same time
            AtomicBoolean isAttending = new AtomicBoolean(false);
            FirebaseFirestore datab = FirebaseFirestore.getInstance();
            datab.collection("participants").whereEqualTo("event", event).whereEqualTo("entrant", userId).get().addOnSuccessListener(task ->{
                List<DocumentSnapshot> results = task.getDocuments();
                if (!results.isEmpty()) {
                    if (Objects.equals(results.get(0).getString("status"), "attending") || Objects.equals(results.get(0).getString("status"), "waitlisted") || Objects.equals(results.get(0).getString("status"), "selected")) {
                        status = results.get(0).getString("status");
                        isAttending.set(true);
                    }
                }

                retrieveEventDetails(event, isAttending.get());
            });
        });

    }

    private void retrieveEventID(String qrID, OnEventIDRetrievedListener listener) {
        FirebaseFirestore datab = FirebaseFirestore.getInstance();
        datab.collection("events")
                .whereEqualTo("qrID", qrID) // Search by qrID
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        String eventID = querySnapshot.getDocuments().get(0).getId(); // Get the document name
                        listener.onEventIDRetrieved(eventID); // Pass the eventID to the listener
                    } else {
                        Toast.makeText(this, "Event not found for QR ID", Toast.LENGTH_SHORT).show();
                        finish(); // End the activity if the event is not found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Database", "Error retrieving event by QR ID", e);
                    Toast.makeText(this, "Error retrieving event details", Toast.LENGTH_SHORT).show();
                    finish(); // End the activity if an error occurs
                });
    }
    public interface OnEventIDRetrievedListener {
        void onEventIDRetrieved(String eventID);
    }



    /**
     * Retrieves event details from Firestore based on the event's unique ID.
     * Directs the user to the event description or joining page based on association status.
     *
     * @param event      The unique ID of the event in Firestore.
     * @param isAssociated  {@code true} if the event is associated with the user; otherwise {@code false}.
     */
    private void retrieveEventDetails(String event, boolean isAssociated) {

        db.getEvent(event, results -> {
            if (!isAssociated){
                //Check to see if the event hasn't met the waitlist capacity and we haven't past deadline

                Date currentDate = new Date();

                FirebaseFirestore datab = FirebaseFirestore.getInstance();
                datab.collection("events").document(event).get().addOnSuccessListener(eventData->{
                    Long eventWaitlistCapacity;
                    Timestamp eventTimestamp;
                    if(eventData.contains("waitlistCapacity")){
                        eventWaitlistCapacity = eventData.getLong("waitlistCapacity");
                        eventTimestamp = eventData.getTimestamp("dateAndTime");
                    }else{
                        eventWaitlistCapacity = null;
                        eventTimestamp = eventData.getTimestamp("dateAndTime");
                    }
                    datab.collection("participants").whereEqualTo("event", event).whereEqualTo("status", "waitlisted").get().addOnSuccessListener(task ->{
                        List allWaitlistedUsers = task.getDocuments();
                        if((eventWaitlistCapacity == null || allWaitlistedUsers.size()+1 <= eventWaitlistCapacity) && (currentDate.compareTo(eventTimestamp.toDate()) < 0)){
                            Intent intent = new Intent(this, EntrantJoinEventActivity.class);
                            Log.d("qr_Scanner_event_id", event);
                            launchActivity(event, intent, results, eventData);

                        }else{
                            if(currentDate.compareTo(eventTimestamp.toDate()) > 0){
                                Toast.makeText(getApplicationContext(), "Cannot join, past deadline", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Cannot join, max waitlist capacity", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }
                    });
                });


            }else{
                Intent intent = new Intent(this, EntrantEventDetails.class);
                Event eventObject = new Event(event);
                eventObject.setEventName((String)results.get("name"));
                eventObject.setEventID(event);
                eventObject.setPoster((String)results.get("poster"));
                eventObject.setEventDescription((String)results.get("description"));
                Timestamp eventDate = (Timestamp) results.get("dateAndTime");
                eventObject.setEventCloseDate(eventDate.toDate());
                eventObject.setStatus(status);
                Bundle bundle = new Bundle();
                bundle.putSerializable("EVENT", eventObject);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    private void launchActivity(String event, Intent intent, HashMap<String, Object> results, DocumentSnapshot eventData){
        Event eventObject = new Event(eventData.getId());
        eventObject.setEventName(eventData.getString("name"));
        eventObject.setEventCloseDate(eventData.getDate("dateAndTime"));
        eventObject.setPoster(eventData.getString("poster"));
        eventObject.setEventID(event);
        eventObject.setEventDescription(eventData.getString("description"));
        eventObject.setStatus("NA");


        Bundle bundle = new Bundle();
        results.put("eventID", event);
        bundle.putSerializable("EVENT", eventObject);
        bundle.putSerializable("event", results);
        intent.putExtras(bundle);
        if (results.containsKey("location")) {
            GeoPoint geoPoint = (GeoPoint) results.get("location");
            if (geoPoint != null) {
                HashMap<String, Double> locationMap = new HashMap<>();
                locationMap.put("latitude", geoPoint.getLatitude());
                locationMap.put("longitude", geoPoint.getLongitude());
                results.put("location", locationMap);  // Replace GeoPoint with HashMap
            }
        }

        startActivity(intent);
    }

    /**
     * Called when the user responds to the permission request for camera access.
     * If permission is granted, initializes the QR code scanner.
     *
     * @param requestCode  The request code passed in {@link #requestPermissions}.
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the requested permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}

