package com.example.oblong.organizer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.imageUtils;
import com.example.oblong.qr_generator;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Activity class representing the screen displaying an organizer's event data.
 *
 * <p>This activity retrieves event information, using Firebase to fetch relevant data and populates views
 * with this information for display. The user can choose to draw or cancel entrants, view waitlist, attending,
 * and cancelled participants lists, add a poster, create a notification, or view a map of the locations of
 * participants.</p>
 */
public class organizer_view_event_screen extends AppCompatActivity {

    private TextView eventNameDisplay;
    private TextView eventDescriptionDisplay;
    private TextView maxCapacityDisplay;
    private ImageView backButton;
    private ImageView qrCode;
    private ImageView poster;
    private Event event;
    private Button notificationButton;
    private Button waitlistButton;
    private Button attendeesButton;
    private Button cancelledButton;
    private Button drawButton;
    private Button cancelButton;
    private Button mapButon;
    private ImageView uploadPosterButton;
    private String eventId;
    private String qrID;
    private final Database db = new Database();
    private FirebaseFirestore fdb;

    /**
     * Called when the activity is first created.
     *
     * <p>This method initializes the view components, retrieves the event data from Firebase,
     * and sets up button listeners.</p>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data supplied by onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_view_event_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fdb = FirebaseFirestore.getInstance();
        eventNameDisplay = findViewById(R.id.organizer_view_event_name);
        uploadPosterButton = findViewById(R.id.activity_organizer_view_event_event_description_upload_icon);
        eventDescriptionDisplay = findViewById(R.id.activity_organizer_view_event_event_description_text);
        maxCapacityDisplay = findViewById(R.id.activity_organizer_view_event_event_description_max_capacity);
        backButton = findViewById(R.id.backArrow);
        qrCode = findViewById(R.id.activity_organizer_view_event_event_description_qr_code_display);
        poster = findViewById(R.id.activity_organizer_viewevent_event_description_poster);
        notificationButton = findViewById(R.id.activity_organizer_view_event_event_description_setup_notification_button);
        waitlistButton = findViewById(R.id.activity_organizer_view_event_event_description_view_waitlist_button);
        attendeesButton = findViewById(R.id.activity_organizer_view_event_event_description_view_attendees_button);
        drawButton = findViewById(R.id.draw_button);
        cancelButton = findViewById(R.id.cancel_entrants_button);
        mapButon = findViewById(R.id.view_map);
        cancelledButton = findViewById(R.id.view_cancelled);


        Intent intent = getIntent();


        initializeData(intent);

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(organizer_view_event_screen.this, organizer_create_notification_activity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("EVENT", event);
                intentNotification.putExtras(bundle);

                startActivity(intentNotification);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        waitlistButton.setOnClickListener(v -> {
            Intent intentWaitlist = new Intent(organizer_view_event_screen.this, EventWaitingList.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("EVENT", event);
            intentWaitlist.putExtras(bundle);

            startActivity(intentWaitlist);
        });

        attendeesButton.setOnClickListener(v -> {
            Intent intentAttendees = new Intent(organizer_view_event_screen.this, EventViewAttendees.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("EVENT", event);
            intentAttendees.putExtras(bundle);

            startActivity(intentAttendees);
        });

        cancelledButton.setOnClickListener(v -> {
            Intent intentAttendees = new Intent(organizer_view_event_screen.this, EventViewCancelled.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("EVENT", event);
            intentAttendees.putExtras(bundle);

            startActivity(intentAttendees);
        });

        mapButon.setOnClickListener(v -> {
            Intent intentMap = new Intent(organizer_view_event_screen.this, organizer_map.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("EVENT", event);
            intentMap.putExtras(bundle);

            startActivity(intentMap);
        });

        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseParticipants(eventId);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEntrants(eventId);
            }
        });

        uploadPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForImage();
            }
        });

    }

    /**
     * {@code chooseParticipants} is called in {@link #onCreate(Bundle)}
     * fetches data from the Firebase for the participant's data and their entrant data
     *
     * <p>This method randomly selects at most, eventCapacity participants and sets their status
     * to "selected" and sends selected and non-selected participants a notification.
     * String Arrays of the selected and non-selected participants are created,
     * and the corresponding notifications are added to the database. The participant and their entrant
     * document are updated with the new status and notification id.</p>
     */
    private void chooseParticipants(String eventId){
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("event", eventId);
        conditions.put("status", "waitlisted");
        List<String> entrantPool = new ArrayList<String>();
        db.query("participants", conditions, results ->{
            for(int i = 0; i < results.size(); i++){
                // get all entrants that signed up for event
                entrantPool.add((String)results.get(i).get("entrant"));
            }
            List<String> selectedParticipants =  entrantPool;
            if(event.getEventCapacity() < entrantPool.size()){
                // choose random participants if pool is larger than capacity
                Collections.shuffle(entrantPool);
                selectedParticipants = entrantPool.subList(0, event.getEventCapacity());
            }

            //create selected notification
            String newNotifIDSelected = fdb.collection("notifications").document().getId();
            String label = event.getEventName()+": Congratulations! You've been selected!";
            String content = "Congratulations on being selected to attend our event! Please accept your invitation " +
                    "by visiting your \"Upcoming Events\" tab and clicking \"Accept Invitation\" for our event";
            String[] notifSelected = selectedParticipants.toArray(new String[0]);

            //create not selected notification
            String newNotifIDNotSelected = fdb.collection("notifications").document().getId();
            String label2 = event.getEventName()+": Sorry! You weren't selected!";
            String content2 = "Unfortunately, you were not selected to attend our event. But don't fret! " +
                    "You may have a chance to be selected if someone declines their invitation!";
            //Use set subtraction to get list of participants not selected
            Set<String> selectedSet = new HashSet<String>(selectedParticipants);
            Set<String> notSelectedSet = new HashSet<>(entrantPool);
            notSelectedSet.removeAll(selectedSet);
            String[] notSelectedParticipants = notSelectedSet.toArray(new String[0]);

            //add the notifications to the database
            db.addNotification(newNotifIDSelected, eventId, content, label, "Selected", notifSelected);
            db.addNotification(newNotifIDNotSelected, eventId, content2, label2, "Not Selected", notSelectedParticipants);

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("status", "selected");

            HashMap<String, Object> entrantUpdate = new HashMap<>();
            entrantUpdate.put("notificationsList", FieldValue.arrayUnion(newNotifIDSelected));
            for(int i = 0; i < selectedParticipants.size(); i++) {
                // set each participant's status as selected
                db.updateDocument("participants", selectedParticipants.get(i)+eventId, updates, v->{});

                //add selected notification id to selectedParticipants (entrants) notificationsList
                db.updateDocument("entrants", selectedParticipants.get(i), entrantUpdate, v->{});
            }
            //add not selected notification id to notSelectedParticipants (entrants) notificationsList
            entrantUpdate.put("notificationsList", FieldValue.arrayUnion(newNotifIDNotSelected));
            for (String notSelectedParticipant : notSelectedParticipants) {
                db.updateDocument("entrants", notSelectedParticipant, entrantUpdate, v -> {});
            }
        });

    }
    /**
     * {@code cancelEntrants} is called in {@link #onCreate(Bundle)}
     * fetches data from the Firebase for the participant's data and their entrant data
     *
     * <p>This method selects all participants whose status is "waitlisted" or "selected" and updates their
     * status to "cancelled". A notification is created and added to the database, and a String Array of the
     * cancelled entrants is created. The participants' entrant documents are updated with the notification id</p>
     */
    private void cancelEntrants(String eventId) {
        List<String> cancelledList = new ArrayList<>();

        // query for participants from specific event with either "selected" or "waitlisted" status
        fdb.collection("participants").whereEqualTo("event", eventId).whereIn("status", Arrays.asList( "selected", "waitlisted")).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String documentId = document.getId();

                    // Update the status to "cancelled"
                    fdb.collection("participants").document(documentId).update("status", "cancelled");

                    //add participant's entrant id to cancelledList for notification
                    cancelledList.add((String) document.get("entrant"));
                }
            }

            //create cancelled notification (no longer invited to attend event)
            String notifCancelledID = fdb.collection("notifications").document().getId();
            String label = event.getEventName()+": Sorry! You aren't attending!";
            String content = "Unfortunately, you will not be attending our event. Thank you for signing up. " +
                    "We encourage you to sign up for our future events!";
            String[] notifCancelled = cancelledList.toArray(new String[0]);
            db.addNotification(notifCancelledID, eventId, content, label, "cancelled", notifCancelled);

            //add cancelled notification to cancelled entrants' notificationsList
            HashMap<String, Object> entrantUpdate = new HashMap<>();
            entrantUpdate.put("notificationsList", FieldValue.arrayUnion(notifCancelledID));
            for(String cancelledParticipant : cancelledList){
                db.updateDocument("entrants", cancelledParticipant, entrantUpdate, v -> {});
            }
        });
    }

    /**
     * Initializes event data from the Intent.
     *
     * <p>This method retrieves the event object from the Intent's extras, and sets the event
     * name, description, max capacity, poster, and QR Code in the corresponding Views.</p>
     *
     * @param intent The Intent containing the event data passed from the previous activity.
     */
    private void initializeData(Intent intent){
        Bundle bundle = getIntent().getExtras();
        event = (Event) bundle.get("EVENT");
        eventNameDisplay.setText(event.getEventName());
        eventDescriptionDisplay.setText(event.getEventDescription());
        maxCapacityDisplay.setText(Long.toString(event.getEventCapacity()));
        poster.setImageBitmap(imageUtils.base64ToBitmap(event.getPoster()));
        qrID = event.getQrID();
        qr_generator qr = new qr_generator();
        eventId = event.getEventID();
        Bitmap code = qr.generateQRCode(qrID);

        // https://stackoverflow.com/questions/30027242/set-bitmap-to-imageview
        qrCode.setImageBitmap(code);
    }

    private void askForImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a poster"), 200);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                Uri imageUri = data.getData();
                if (null != imageUri) {

                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);


                        String imagePath = imageUtils.getRealPathFromURI(this, imageUri);

                        // Fix orientation using `handleImageRotation`
                        Bitmap rotatedBitmap = imageUtils.handleImageRotation(imagePath, imageBitmap);
                        if (imageUtils.isImageTooLarge(rotatedBitmap)){
                            Toast.makeText(requireContext(), "Image is too large", Toast.LENGTH_LONG).show();

                        } else {
                            imageBitmap = rotatedBitmap;
                        }
                        String imageBase64 = imageUtils.bitmapToBase64(imageBitmap);

                        event.setPoster(imageBase64);

                        //Update database
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        database.collection("events").document(event.getEventID()).update("poster", imageBase64);
                        poster.setImageBitmap(imageUtils.base64ToBitmap(event.getPoster()));



                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private Context requireContext() {
        return getBaseContext();
    }

}


