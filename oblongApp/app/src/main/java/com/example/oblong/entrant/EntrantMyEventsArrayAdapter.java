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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oblong.Event;
import com.example.oblong.R;

import java.util.ArrayList;

public class EntrantMyEventsArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    public EntrantMyEventsArrayAdapter(Context context, ArrayList<Event> events){
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
        TextView eventName = view.findViewById(R.id.entrant_event_list_item_event_name);
        TextView drawDate = view.findViewById(R.id.entrant_event_list_item_draw_date);
        Button viewButton = view.findViewById(R.id.entrant_event_list_item_view_button);
        Button acceptButton = view.findViewById(R.id.entrant_event_list_item_accept_invite_button);

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
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EntrantEventAcceptDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("EVENT", event);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });


        //TODO: implement images
        drawDate.setText("Due: " + event.getEventCloseDate());
        eventName.setText(event.getEventName());


        return view;
    }
}
