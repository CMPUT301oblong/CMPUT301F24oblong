package com.example.oblong.organizer;

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
import com.example.oblong.entrant.EntrantEventAcceptDescriptionActivity;
import com.example.oblong.entrant.EntrantEventDescriptionActivity;
import com.example.oblong.imageUtils;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying a list of events in the organizer's "My Events" section.
 *
 * <p>This adapter binds event data to a custom list item layout and provides click functionality
 * for viewing event details and accepting events. It uses the {@link Event} class for event data.</p>
 *
 * <p>Each list item displays the event poster, name, and draw date, along with buttons for viewing
 * or interacting with the event.</p>
 */
public class OrganizerMyEventsArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    /**
     * Constructor for the custom adapter.
     *
     * @param context The context in which the adapter is being used.
     * @param events  The list of {@link Event} objects to display in the adapter.
     */
    public OrganizerMyEventsArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }

    /**
     * Provides a view for an adapter's item at a specific position.
     *
     * <p>This method inflates a custom layout for each event item in the list and sets the event data,
     * such as the poster, name, and draw date. It also defines click listeners for buttons to view
     * and interact with the event.</p>
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A {@link View} corresponding to the data at the specified position.
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
        ImageView eventPoster = view.findViewById(R.id.entrant_event_list_item_poster);
        TextView eventName = view.findViewById(R.id.entrant_event_list_item_event_name);
        TextView drawDate = view.findViewById(R.id.entrant_event_list_item_draw_date);
        Button viewButton = view.findViewById(R.id.entrant_event_list_item_view_button);
        Button acceptButton = view.findViewById(R.id.entrant_event_list_item_accept_invite_button);

        viewButton.setOnClickListener(new View.OnClickListener() {
            // Handle the button click event
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, organizer_view_event_screen.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("EVENT", event);
                intent.putExtras(bundle);

                context.startActivity(intent);

                Log.d("button", String.format("%s button clicked", event.getEventName()));
            }
        });



        acceptButton.setOnClickListener(new View.OnClickListener() {
            // Handle the button click event
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, organizer_view_event_screen.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("EVENT", event);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });



        if(!(event.getPoster() == null)) {
            eventPoster.setImageBitmap(imageUtils.base64ToBitmap(event.getPoster()));
        }
        drawDate.setText("Due: " + event.getEventCloseDate());
        eventName.setText(event.getEventName());


        return view;
    }

}
