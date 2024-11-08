package com.example.oblong.entrant;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.imageUtils;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying a list of events in the entrant's "My Events" section.
 *
 * <p>This adapter is used to populate a ListView with event details such as the event name,
 * draw date, and options to view or accept the event invite. It handles the click actions
 * for the buttons that navigate to detailed event description activities.</p>
 */
public class EntrantMyEventsArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    /**
     * Constructor for the EntrantMyEventsArrayAdapter.
     *
     * @param context The context in which the adapter is being used.
     * @param events  The list of Event objects to be displayed.
     */
    public EntrantMyEventsArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }

    /**
     * Provides a view for an AdapterView (ListView) using the data at the specified position.
     *
     * <p>This method inflates a custom layout for each item in the list and sets the event name,
     * draw date, and handles the click events for "View" and "Accept" buttons.</p>
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent ViewGroup that this view will eventually be attached to.
     * @return The View corresponding to the data at the specified position.
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


        // Set the event name
        TextView eventName = view.findViewById(R.id.entrant_event_list_item_event_name);
        TextView drawDate = view.findViewById(R.id.entrant_event_list_item_draw_date);
        Button viewButton = view.findViewById(R.id.entrant_event_list_item_view_button);
        Button acceptButton = view.findViewById(R.id.entrant_event_list_item_accept_invite_button);
        ImageView poster = view.findViewById(R.id.entrant_event_list_item_poster);

        viewButton.setOnClickListener(new View.OnClickListener() {
            // Handle the button click event
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EntrantEventDescriptionActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("EVENT", event);
                intent.putExtras(bundle);

                context.startActivity(intent);

                Log.d("button", String.format("%s button clicked", event.getEventName()));
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            // Handle the button click event
            /**
             * Handles the click event for the "View" button.
             *
             * <p>When the user clicks the "View" button, it starts the EntrantEventDescriptionActivity
             * and passes the current event data to it using a Bundle.</p>
             */
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EntrantEventAcceptDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("EVENT", event);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });


        drawDate.setText("Due: " + event.getEventCloseDate());
        eventName.setText(event.getEventName());
        if(event.getPoster() != null) {
            poster.setImageBitmap(imageUtils.base64ToBitmap(event.getPoster()));
        }

        return view;
    }
}
