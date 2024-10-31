package com.example.oblong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

public class EventArrayAdapter extends ArrayAdapter<Event> {

    public EventArrayAdapter(@NonNull Context context, ArrayList<Event> events){
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_list_entry, parent, false);
        } else {
            view = convertView;
        }
        Event event = getItem(position);
        TextView eventNameTextView = view.findViewById(R.id.event_entry_event_name);
        TextView eventCloseDateTextView = view.findViewById(R.id.event_entry_event_close_date);
        ImageView eventImageView = view.findViewById(R.id.event_entry_event_image);
        eventNameTextView.setText(event.getEventName());
        eventCloseDateTextView.setText(event.getEventCloseDate());
        eventImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.baseline_person_24));
        return view;

    }
}
