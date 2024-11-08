package com.example.oblong.entrant;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oblong.Event;
import com.example.oblong.R;

import java.util.ArrayList;
/**
 * Custom ArrayAdapter for displaying a list of events in the "All Events" section of the entrant's interface.
 *
 * <p>This adapter is responsible for inflating the list item view for each event and setting its
 * corresponding data such as event name and draw date. It also provides an onClickListener for the
 * "View" button to handle user interactions.</p>
 */
public class EntrantAllEventsArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    /**
     * Constructs a new EntrantAllEventsArrayAdapter.
     *
     * @param context The context of the current state of the application.
     * @param events  The list of events to be displayed in the ListView.
     */
    public EntrantAllEventsArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }

    /**
     * Provides a view for an adapter view (ListView) for each event item in the list.
     *
     * <p>This method inflates the layout for each event list item and sets the event data
     * such as the event name and draw date. It also sets a click listener on the "View"
     * button for each event.</p>
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent view that this view will eventually be attached to.
     * @return The constructed view for the specified position in the list.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.entrant_event_list_item, parent,false);
        }

        Event event = events.get(position);

        //TODO: Get the event images

        // Set the event name
        TextView eventName = view.findViewById(R.id.entrant_event_list_item_event_name);
        TextView drawDate = view.findViewById(R.id.entrant_event_list_item_draw_date);
        Button viewButton = view.findViewById(R.id.entrant_event_list_item_view_button);

        viewButton.setOnClickListener(new View.OnClickListener() {
            // Handle the button click event
            @Override
            public void onClick(View v) {
                // TODO: open the event page
                // Create an Intent to start the new activity
                // Intent intent = new Intent(getContext(), NewActivity.class);
                // Start the new activity
                // context.startActivity(intent);

                Log.d("button", String.format("%s button clicked", event.getEventName()));
            }
        });


        //TODO: implement images
        drawDate.setText("Due: " + event.getEventCloseDate());
        eventName.setText(event.getEventName());


        return view;
    }
}
