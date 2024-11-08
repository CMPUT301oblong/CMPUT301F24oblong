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

public class OrganizerMyEventsArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    public OrganizerMyEventsArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }


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


        //TODO: implement images
        if(!(event.getPoster() == null)) {
            eventPoster.setImageBitmap(imageUtils.base64ToBitmap(event.getPoster()));
        }
        drawDate.setText("Due: " + event.getEventCloseDate());
        eventName.setText(event.getEventName());


        return view;
    }

}
