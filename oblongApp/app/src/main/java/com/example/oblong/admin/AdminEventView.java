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
 * Activity class for managing events as an Administrator.
 * Allows admins to view, delete and update event information
 */
public class AdminEventView extends AppCompatActivity {
    private Event current_event;

    /**
     * Initializes the activity, setting up the UI components and event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
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
            // sync up current event ID with event ID from created to match the facility that created it
            db.collection("created").whereEqualTo("event", current_event.getEventID()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // fetch facility ID
                        String facilityID = document.getString("facility");
                        if (facilityID != null) {
                            // delete facility ID in facilities that matches the current event ID in created
                            db.collection("facilities").document(facilityID).delete();
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


    /**
     * Creates a confirmation dialog for deleting the event.
     *
     * @return The created AlertDialog instance.
     */
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
    /**
     * Creates a confirmation dialog for deleting the QR code.
     *
     * @return The created AlertDialog instance.
     */
    private AlertDialog createDialogQRCode() {
        Log.d("AdminEventView", "createDialog called");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to deactivate this QR code?\n" +
                "This will make it so that scanning the QR code will no longer take the user to the event page\n" +
        "However, a new unique qr Code will be generated for this event in place of the old one");
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
