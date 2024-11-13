package com.example.oblong.admin;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oblong.Event;
import com.example.oblong.R;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying a list of events or users in the admin "Profile Browser" or "Event Browser" section.
 *
 * <p>This adapter is used to populate a ListView with events and users.
 * It handles the click actions for deletions </p>
 */
public class AdminUserEventArrayAdapter extends ArrayAdapter<Object> {
    private ArrayList<Object> objects;
    private Context context;

    /**
     * Constructor for the AdminUserEventArrayAdapter.
     *
     * @param context The context in which the adapter is being used.
     * @param objects  The list of Event and User objects to be displayed.
     */
    public AdminUserEventArrayAdapter(Context context, ArrayList<Object> objects){
        super(context,0, objects);
        this.objects = objects;
        this.context = context;
    }

    /**
     * Provides a view for an AdapterView (ListView) using the data at the specified position.
     *
     * <p>This method inflates a custom layout for each item in the list and sets the list item name,
     * profile pictures, and deletion buttons accordingly.</p>
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
            view = LayoutInflater.from(context).inflate(R.layout.admin_user_event_list_item, parent,false);
        }

        Object object = objects.get(position);

        TextView itemName = view.findViewById(R.id.listItemTextView);
        ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        ImageView poster = view.findViewById(R.id.profilepicture);

        // Set the event name
        if (object instanceof Event) {
            Event event = (Event) object;
            itemName.setText(event.getEventName());
        } else if (object instanceof String) {
            // TODO: implement user as its own object to support profile pictures
            String user = (String) object;
            itemName.setText(user);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            // Handle the button click event
            /**
             * Handles the click event for the "delete" button.
             *
             * <p>When the user clicks the "delete" button, it presents a dialog confirming the
             * deletion of the selected item. If the user confirms, it performs the deletion in the
             * database.</p>
             */
            @Override
            public void onClick(View v) {

                // TODO: Setup Item Deletion
//                Intent intent = new Intent(context, EntrantEventAcceptDescriptionActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("EVENT", event);
//                intent.putExtras(bundle);

//                context.startActivity(intent);
            }
        });

        return view;
    }
}
