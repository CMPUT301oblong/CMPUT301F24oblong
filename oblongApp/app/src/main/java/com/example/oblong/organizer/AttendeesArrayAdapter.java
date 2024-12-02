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

/**
 * {@code EntrantUpcomingEventsFragment} This class handles the list of attendees to an event
 * to be displayed for the Organizer
 */
public class AttendeesArrayAdapter extends BaseAdapter{
    private Context context;
    private List<Map<String, String>> attendees;


    /**
     * Constructor for AttendeesArrayAdapter
     *
     * @param context The context where the array adapter will be used
     * @param attendees The list of attendee Map objects to an event
     */
    public AttendeesArrayAdapter(Context context, List<Map<String, String>> attendees) {
        this.context = context;
        this.attendees = attendees;
    }

    /**
     * The {@code getCount} gets the size of the attendees list
     *
     * @return The size of attendees as an integer
     */
    @Override
    public int getCount() {
        return attendees.size();
    }

    /**
     * The {@code getItem} gets the attendee at a given index in the attendees list
     *
     * @param position The position of an item in the attendees list
     * @return Map object at the index position
     */
    @Override
    public Object getItem(int position) {
        return attendees.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }
    /**
     * Provides a view for a BaseAdapter (ListView) using the data at the specified position.
     *
     * <p>This method inflates a custom layout for each item in the list and sets the attendee name
     * and status.</p>
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent ViewGroup that this view will eventually be attached to.
     * @return The View corresponding to the data at the specified position.
     */
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
