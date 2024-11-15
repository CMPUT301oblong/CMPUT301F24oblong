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

        // TODO: set up Qr Code Deletion
        // TODO: set up Event Facility Deletion

        Event event = (Event) getIntent().getSerializableExtra("event");
        current_event = event;

        eventName.setText(event.getEventName());
        eventDescriptionEntry.setText(event.getEventDescription());


        deleteButton.setOnClickListener(v -> {
            AlertDialog dialog = createDialog();
            dialog.show();
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
}
