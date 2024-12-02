package com.example.oblong.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.oblong.R;
import com.example.oblong.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Fragment for browsing user profiles in the admin panel.
 */
public class AdminProfileBrowserActivity extends Fragment {

    private ListView userList;
    private ArrayList<Object> userDataList;
    // Will eventually user AdminUserEventArrayAdapter \/
    private AdminUserEventArrayAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference userRef;

    /**
     * Inflates the layout for the profile browser fragment.
     *
     * @param inflater LayoutInflater to inflate the view.
     * @param container Parent view that this fragment's UI will be attached to.
     * @param savedInstanceState Saved state bundle for restoring the fragment.
     * @return Inflated view for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_event_browser, container, false);
    }

    /**
     * Called immediately after the view is created. Initializes UI elements and data fetching.
     *
     * @param view The fragment's root view.
     * @param savedInstanceState Saved state bundle for restoring the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users");

        userList = view.findViewById(R.id.admin_event_browser_list); // Corrected line
        TextView titleText = view.findViewById(R.id.admin_event_browser_title);
        titleText.setText("Profile Browser");
        userDataList = new ArrayList<>();
        // set an item click listener

//        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, userDataList);
        adapter = new AdminUserEventArrayAdapter(getContext(), userDataList);
        userList.setAdapter(adapter);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Handle item click event
                Log.d("AdminProfileBrowserActivity", "Item clicked: " + ((User) userDataList.get(i)).getId());
                User user = (User) userDataList.get(i);
                // Open the user's profile
                Intent intent = new Intent(getActivity(), AdminUserProfileView.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });


        // Fetch Items from Firebase
        fetchUsers();
    }
    /**
     * Fetches user profiles from Firestore and updates the ListView.
     */
    private void fetchUsers() {
        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    userDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        User user = new User(doc.getId(),
                                doc.getString("name"),
                                doc.getString("phone"),
                                doc.getString("email"),
                                doc.getString("profilePicture"),
                                doc.getString("type"));
                        userDataList.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}