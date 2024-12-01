package com.example.oblong.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oblong.Database;
import com.example.oblong.Event;
import com.example.oblong.R;
import com.example.oblong.User;
import com.example.oblong.imageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * Custom ArrayAdapter for displaying a list of events or users in the admin "Profile Browser" or "Event Browser" section.
 *
 * <p>This adapter is used to populate a ListView with events and users.
 * It handles the click actions for deletions </p>
 */
public class AdminUserImageArrayAdapter extends ArrayAdapter<AdminImageBrowserObject> {

    private String defaultImage = "iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAKaSURBVHgB7ZlBbuIwFIYfobACiSULhNIbzA2GOcnkBtM5AekJZuYGmZOUnqDcIClCYtkukRDQ95AtGcc4tkkcV+WTLDm2E/1/nOc4LwA3bnxtOpc6RshgMPjV6XRmVKAdllj+rlar/5cGKA2Mx+O43+8/YTWGMCh2u91ss9m8yh2RanRg4om41+st6KmQO0oGptPpTwhLPCemR1puvFMMfBAPjsdj1u12fxdF8Q4eiZH9fj/H+Et4G4vFR3FcKQZwBo7iMYqP8zx/hRZAD6PD4fAmtmFAn2mOqi7SlnjCZNYrDYTOHTgymUy+4zOZYvmGhyOMlQXWM92a3QROM4BxMo+iaMGC6rS0sXqGxubgEWsDbJlNL14wilKaHfCEtQFxWdOMScET1gbwWZ+Jx7TM4l2/F9tYXHjBJQbOlrbtdnsqEiPwhMsjtBSPcY9S4N4pF9toRaq6DsUSljcsOYsrJ1wMpAZjMl0/E0xjaKZiqruasDaAb8dn0KxC+OpPde8CQbyMkwmn9wAKpA1Vgnd6wZreqY7B/GO9Xj9eOk8jnmNtonIzJ2+eXDEQL5LwWazS42UvpBGfsCJjPBPOeyFTdOKFuwyKMRlr19LoDJiIJ1g9UYxTnXtGYwZMxXM0JrQ0YsBWPMfFRO0GXMVzbE3UauBa8RwbE7UZqEs8x9RELQbqFs8xOfdqA02JN+UqA22LJ5wNhCCecM1KBCGecM1KZIou7+IJKwOhiScqDfCcfBvi6UdL1ZjSdpq+rMTUyXA4/IN5+dOvHsX5jYmnG4cJAznLt5THqb7I6KQUwqR0w0oGWE7+BcL7S1Og+Hu5sRQDlJOnj3OqQjgUmAGcqTq0H+wUuBgTDxgT3lKFIiweKdvxz/cvrhs3PgsfKfsY4D61rB4AAAAASUVORK5CYII=";

    // invoke the suitable constructor of the ArrayAdapter class
    public AdminUserImageArrayAdapter(@NonNull Context context, ArrayList<AdminImageBrowserObject> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.image_list_content, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        AdminImageBrowserObject object = getItem(position);

        TextView imageInfo = currentItemView.findViewById(R.id.image_info_text);
        ImageView deleteButton = currentItemView.findViewById(R.id.delete_image);
        ImageView poster = currentItemView.findViewById(R.id.event_poster);

        Log.d("Entered Array Adapter", "We have entered adapter");

        assert object != null;
        if(Objects.equals(object.getType(), "event")){
            imageInfo.setText(String.format("From Event: %s", object.getName()));
        }else if(Objects.equals(object.getType(), "user")){
            imageInfo.setText(String.format("From User: %s", object.getName()));
        }else{
            imageInfo.setText(String.format("From Facility: %s", object.getName()));
        }

        try{
            poster.setImageBitmap(imageUtils.base64ToBitmap(object.getImage()));
        } catch (Exception e) {
            poster.setImageBitmap(imageUtils.base64ToBitmap(defaultImage));
        }

        /*deleteButton.setOnClickListener(new View.OnClickListener() {
            // Handle the button click event
            /**
             * Handles the click event for the "delete" button.
             *
             * <p>When the user clicks the "delete" button, it presents a dialog confirming the
             * deletion of the selected item. If the user confirms, it performs the deletion in the
             * database.</p>

            @Override
            public void onClick(View v) {
                AlertDialog dialog = createDialog(object);
                dialog.show();
            }
        });*/

        return currentItemView;
    }

    private AlertDialog createDialog(Object object) {
        Log.d("AdminUserProfileView", "createDialog called");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if(object instanceof Event){
            builder.setMessage("Are you sure you want to delete this event?");
        } else if(object instanceof User){
            builder.setMessage("Are you sure you want to delete this user?");
        }
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Database db = new Database();
                if(object instanceof Event){
                    Event event = (Event) object;

                    db.deleteEvent(getContext(), event);
                }
                if(object instanceof User){
                    User user = (User) object;

                    Database.getCurrentUser(user_id -> {
                        if(!user.getId().equals(user_id)) {
                            db.deleteUser(getContext(), user);
                        } else {
                            Toast.makeText(getContext(), "You cannot delete yourself", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(getContext().toString(), "Deletion cancelled");
                Toast.makeText(getContext(), "Deletion cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}
