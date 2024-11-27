package com.example.oblong.organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.oblong.R;

import java.util.List;
import java.util.Map;

public class AttendeesArrayAdapter extends BaseAdapter{
    private Context context;
    private List<Map<String, String>> attendees;



    public AttendeesArrayAdapter(Context context, List<Map<String, String>> attendees) {
        this.context = context;
        this.attendees = attendees;
    }

    @Override
    public int getCount() {
        return attendees.size();
    }

    @Override
    public Object getItem(int position) {
        return attendees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_organizer_list_item_attendee, parent, false);
        }

        Map<String, String> participant = attendees.get(position);

        TextView nameTextView = convertView.findViewById(R.id.organizer_attendee_list_attendee_name);
        TextView statusTextView = convertView.findViewById(R.id.organizer_attendee_list_attendee_status);

        nameTextView.setText(participant.get("name"));
        statusTextView.setText(participant.get("status"));

        return convertView;

    }

}
