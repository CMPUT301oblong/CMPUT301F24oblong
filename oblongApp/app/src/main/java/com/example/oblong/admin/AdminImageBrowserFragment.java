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

import com.example.oblong.Event;
import com.example.oblong.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminImageBrowserFragment extends Fragment {

    private String defaultImage = "iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAKaSURBVHgB7ZlBbuIwFIYfobACiSULhNIbzA2GOcnkBtM5AekJZuYGmZOUnqDcIClCYtkukRDQ95AtGcc4tkkcV+WTLDm2E/1/nOc4LwA3bnxtOpc6RshgMPjV6XRmVKAdllj+rlar/5cGKA2Mx+O43+8/YTWGMCh2u91ss9m8yh2RanRg4om41+st6KmQO0oGptPpTwhLPCemR1puvFMMfBAPjsdj1u12fxdF8Q4eiZH9fj/H+Et4G4vFR3FcKQZwBo7iMYqP8zx/hRZAD6PD4fAmtmFAn2mOqi7SlnjCZNYrDYTOHTgymUy+4zOZYvmGhyOMlQXWM92a3QROM4BxMo+iaMGC6rS0sXqGxubgEWsDbJlNL14wilKaHfCEtQFxWdOMScET1gbwWZ+Jx7TM4l2/F9tYXHjBJQbOlrbtdnsqEiPwhMsjtBSPcY9S4N4pF9toRaq6DsUSljcsOYsrJ1wMpAZjMl0/E0xjaKZiqruasDaAb8dn0KxC+OpPde8CQbyMkwmn9wAKpA1Vgnd6wZreqY7B/GO9Xj9eOk8jnmNtonIzJ2+eXDEQL5LwWazS42UvpBGfsCJjPBPOeyFTdOKFuwyKMRlr19LoDJiIJ1g9UYxTnXtGYwZMxXM0JrQ0YsBWPMfFRO0GXMVzbE3UauBa8RwbE7UZqEs8x9RELQbqFs8xOfdqA02JN+UqA22LJ5wNhCCecM1KBCGecM1KZIou7+IJKwOhiScqDfCcfBvi6UdL1ZjSdpq+rMTUyXA4/IN5+dOvHsX5jYmnG4cJAznLt5THqb7I6KQUwqR0w0oGWE7+BcL7S1Og+Hu5sRQDlJOnj3OqQjgUmAGcqTq0H+wUuBgTDxgT3lKFIiweKdvxz/cvrhs3PgsfKfsY4D61rB4AAAAASUVORK5CYII=";

    private ListView imageList;
    private ArrayList<AdminImageBrowserObject> imageDataList = new ArrayList<>();
    private AdminUserImageArrayAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseFirestore datab = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_image_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        imageList = view.findViewById(R.id.image_list); // Corrected line
        TextView titleText = view.findViewById(R.id.admin_image_browser_title);
        titleText.setText("Image Browser");



        imageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                                             {
                                                 /* Handle item click event
                                                 Log.d("AdminEventBrowserFragment", "Item clicked: " + ((Event) eventDataList.get(i)).getEventID());
                                                 Event event = (Event) eventDataList.get(i);
                                                 Intent intent = new Intent(getActivity(), AdminEventView.class);
                                                 Bundle bundle = new Bundle();
                                                 bundle.putSerializable("event", event);
                                                 intent.putExtras(bundle);

                                                 startActivity(intent); */
                                             }
                                         });


        // Fetch Items from Firebase
        fetchAllImages();
    }

    private void fetchAllImages() {

        //IMAGES FROM USERS, FACILITIES AND EVENTS therefore what we need is to cache all that data in seperate arrays

        datab.collection("events").get().addOnSuccessListener(task -> {

            List<DocumentSnapshot> dataToParse = task.getDocuments();
            for(int i = 0; i<dataToParse.size(); i++){
                imageDataList.add(
                        new AdminImageBrowserObject(
                                "event",
                                (String) dataToParse.get(i).get("poster"),
                                dataToParse.get(i).getId(),
                                (String) dataToParse.get(i).get("name")
                        )
                );
            }
            fetchUserProfilePictures();
        });
    }

    private void fetchUserProfilePictures(){
        List<String> searchConditions = Arrays.asList(defaultImage, null);
        datab.collection("users").whereNotEqualTo("photo", searchConditions).get().addOnSuccessListener(imageData -> {
            List<DocumentSnapshot> dataToParse = imageData.getDocuments();
            for(int i = 0; i<dataToParse.size(); i++){
                imageDataList.add(
                        new AdminImageBrowserObject(
                                "event",
                                (String) dataToParse.get(i).get("poster"),
                                dataToParse.get(i).getId(),
                                (String) dataToParse.get(i).get("name")
                        )
                );
            }
            Log.d("Datalist size", Integer.toString(imageDataList.size()));
            adapter = new AdminUserImageArrayAdapter(requireActivity(), imageDataList);
            imageList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });
    }

    private void setUpFragment(){
        return;
    }
}