package com.example.oblong.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.imageUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Activity class for displaying a list of events.
 */
public class AdminEventView extends AppCompatActivity {
    private Event current_event;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_admin_page_description);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView eventName = findViewById(R.id.new_event_textView);
        EditText eventDescriptionEntry = findViewById(R.id.event_description_entry);


        Button deleteButton = findViewById(R.id.create_event_button3);
        Button deleteQRCodeButton = findViewById(R.id.create_event_button2);
        Button deleteEventFacilityButton = findViewById(R.id.event_creation_cancel_button);


        // TODO: set up Event Facility Deletion

        Event event = (Event) getIntent().getSerializableExtra("event");
        current_event = event;

        eventName.setText(event.getEventName());
        eventDescriptionEntry.setText(event.getEventDescription());


        deleteButton.setOnClickListener(v -> {
            AlertDialog dialog = createDialog();
            dialog.show();
        });

        deleteQRCodeButton.setOnClickListener(v -> {
            AlertDialog dialog = createDialogQRCode();
            dialog.show();
        });

        deleteEventFacilityButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Query created collection where the event is linked to the current event
            db.collection("created").whereEqualTo("event", current_event.getEventID()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Fetch facility ID
                        String facilityID = document.getString("facility");

                        if (facilityID != null) {
                            // Query all events linked to this facility in the "created" collection
                            db.collection("created").whereEqualTo("facility", facilityID).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    for (QueryDocumentSnapshot eventDoc : task1.getResult()) {
                                        String eventID = eventDoc.getString("event");

                                        if (eventID != null) {
                                            // Delete the event in the events collection
                                            db.collection("events").document(eventID).delete();
                                        }
                                    }

                                    // delete the facility
                                    db.collection("facilities").document(facilityID).delete().addOnSuccessListener(aVoid -> {
                                        Log.d("AdminEventView", "Facility deleted: " + facilityID);

                                        // query and delete from organizers collection
                                        db.collection("organizers").whereEqualTo("facility", facilityID).get().addOnCompleteListener(task2 -> {

                                            if (task2.isSuccessful()) {
                                                for (QueryDocumentSnapshot organizerDoc : task2.getResult()) {
                                                    // Delete the document in the organizers collection
                                                    String userID = organizerDoc.getString("user");
                                                    db.collection("organizers").document(organizerDoc.getId()).delete();

                                                    if (userID != null) {
                                                        db.collection("users").document(userID).update("type", "entrant");
                                                    } else {
                                                        Log.e("AdminEventView", "User ID is null");
                                                    }

                                                }
                                            } else {
                                                Log.e("AdminEventView", "Error querying organizers collection");
                                            }
                                        });
                                    });

                                } else {
                                    Log.e("AdminEventView", "Error querying events for facility");
                                }
                            });
                        } else {
                            Log.d("AdminEventView", "Facility is null");
                        }
                    }
                } else {
                    Log.d("AdminEventView", "Couldn't fetch facility");
                }
            });
        });
    }

    // Delete Event Dialog
    private AlertDialog createDialog() {
        Log.d("AdminEventView", "createDialog called");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this event?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Database db = new Database();
                db.deleteEvent(AdminEventView.this, current_event);
                finish();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("AdminEventView", "Event not deleted");
                Toast.makeText(AdminEventView.this, "Deletion cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
    private AlertDialog createDialogQRCode() {
        Log.d("AdminEventView", "createDialog called");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to deactivate this QR code?\n" +
                "This will make it so that scanning the QR code will no longer take the user to the event page");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Database db = new Database();
                db.deleteQR(AdminEventView.this, current_event);
                finish();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("AdminEventView", "Event not deleted");
                Toast.makeText(AdminEventView.this, "Deletion cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}
