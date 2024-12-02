package com.example.oblong.organizer;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.oblong.Database;
import com.example.oblong.R;
import com.example.oblong.imageUtils;
import com.example.oblong.qr_generator;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class organizer_create_event_fragment extends Fragment {

    private EditText eventNameInput;
    private EditText eventDescriptionInput;
    private Button uploadImageButton;
    private EditText maxCapacityInput;
    private EditText maxWaitlistCapacityInput;
    private Button createEventButton;
    private Button cancelButton;
    private Switch locationSwitch;
    private String qrID;
    private Bitmap imageBitmap = null;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_organizer_create_event_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: Set up any views or logic here

        eventNameInput = view.findViewById(R.id.event_name_entry);
        eventDescriptionInput = view.findViewById(R.id.event_description_entry);
        uploadImageButton = view.findViewById(R.id.upload_button);
        maxCapacityInput = view.findViewById(R.id.capacity_dropdown);
        createEventButton = view.findViewById(R.id.create_event_button);
        cancelButton = view.findViewById(R.id.event_creation_cancel_button);
        maxWaitlistCapacityInput = view.findViewById(R.id.waitlist_capacity_dropdown);
        locationSwitch = view.findViewById(R.id.loaction_required);

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get all info user entered:
                String eventName = eventNameInput.getText().toString();
                if(eventName.isEmpty()){
                    Toast.makeText(getContext(), "Cannot have an empty event name", Toast.LENGTH_SHORT).show();
                    return;
                }
                String eventDescription = eventDescriptionInput.getText().toString();
                Long maxCapacity;
                Long waitlistMaxCapacity;
                if(maxCapacityInput.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Cannot have an empty draw capacity", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    maxCapacity = Long.parseLong(maxCapacityInput.getText().toString());
                    if(maxCapacity%1 != 0){
                        Toast.makeText(getContext(), "Cannot have a decimal capacity", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(maxWaitlistCapacityInput.getText().toString().isEmpty()){
                    waitlistMaxCapacity = -1L;
                }else{
                    waitlistMaxCapacity = Long.parseLong(maxWaitlistCapacityInput.getText().toString());
                    if(waitlistMaxCapacity%1 != 0){
                        Toast.makeText(getContext(), "Cannot have a decimal waitlist capacity", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Generate random QR ID
                qrID = UUID.randomUUID().toString();





                //When we click this we want to take all this info and put it into database
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> event = new HashMap<>();

                qr_generator qr_gen = new qr_generator();

                event.put("capacity", maxCapacity);
                if(waitlistMaxCapacity != -1) {
                    event.put("waitlistCapacity", waitlistMaxCapacity);
                }
                event.put("dateAndTime", FieldValue.serverTimestamp());
                event.put("description", eventDescription);
                event.put("drawDate", FieldValue.serverTimestamp());
                event.put("location", new GeoPoint(0,0));
                event.put("name", eventName);
                event.put("qrID", qrID);

                if (locationSwitch.isChecked()){
                    event.put("locationRequired", "1");
                }else{
                    event.put("locationRequired", "0");
                }

                if (imageBitmap != null) {
                    String imageBase64 = imageUtils.bitmapToBase64(imageBitmap);
                    event.put("poster", imageBase64);
                }else{
                    event.put("poster","iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAKaSURBVHgB7ZlBbuIwFIYfobACiSULhNIbzA2GOcnkBtM5AekJZuYGmZOUnqDcIClCYtkukRDQ95AtGcc4tkkcV+WTLDm2E/1/nOc4LwA3bnxtOpc6RshgMPjV6XRmVKAdllj+rlar/5cGKA2Mx+O43+8/YTWGMCh2u91ss9m8yh2RanRg4om41+st6KmQO0oGptPpTwhLPCemR1puvFMMfBAPjsdj1u12fxdF8Q4eiZH9fj/H+Et4G4vFR3FcKQZwBo7iMYqP8zx/hRZAD6PD4fAmtmFAn2mOqi7SlnjCZNYrDYTOHTgymUy+4zOZYvmGhyOMlQXWM92a3QROM4BxMo+iaMGC6rS0sXqGxubgEWsDbJlNL14wilKaHfCEtQFxWdOMScET1gbwWZ+Jx7TM4l2/F9tYXHjBJQbOlrbtdnsqEiPwhMsjtBSPcY9S4N4pF9toRaq6DsUSljcsOYsrJ1wMpAZjMl0/E0xjaKZiqruasDaAb8dn0KxC+OpPde8CQbyMkwmn9wAKpA1Vgnd6wZreqY7B/GO9Xj9eOk8jnmNtonIzJ2+eXDEQL5LwWazS42UvpBGfsCJjPBPOeyFTdOKFuwyKMRlr19LoDJiIJ1g9UYxTnXtGYwZMxXM0JrQ0YsBWPMfFRO0GXMVzbE3UauBa8RwbE7UZqEs8x9RELQbqFs8xOfdqA02JN+UqA22LJ5wNhCCecM1KBCGecM1KZIou7+IJKwOhiScqDfCcfBvi6UdL1ZjSdpq+rMTUyXA4/IN5+dOvHsX5jYmnG4cJAznLt5THqb7I6KQUwqR0w0oGWE7+BcL7S1Og+Hu5sRQDlJOnj3OqQjgUmAGcqTq0H+wUuBgTDxgT3lKFIiweKdvxz/cvrhs3PgsfKfsY4D61rB4AAAAASUVORK5CYII=");
                }
                event.put("QR", "");

                db.collection("events").add(event).addOnSuccessListener(documentReference -> {
                    String eventID = documentReference.getId();
                    Bitmap qrBitmap = qr_gen.generateQRCode(qrID);
                    String qrBase64 = imageUtils.bitmapToBase64(qrBitmap);

                    db.collection("events").document(eventID).update("QR",qrBase64);

                //Now we want to put the organizer in a created relation with this event
                    Database.getCurrentUser(new Database.OnDataReceivedListener<String>() {
                        @Override
                        public void onDataReceived(String data) {
                            db.collection("organizers").whereEqualTo("user",data).get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                       String organizerFacility = queryDocumentSnapshots.getDocuments().get(0).getString("facility");

                                       Map<String, Object> eventCreation = new HashMap<>();
                                       eventCreation.put("event", eventID);
                                       eventCreation.put("facility", organizerFacility);

                                       db.collection("created").add(eventCreation);
                                    });
                        }
                    });
                });

                // Navigate to My Event Frame
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new organizer_my_events_fragment())
                        .commit();

            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForImage();
            }
        });

    }

    private void askForImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a poster"), 200);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                Uri imageUri = data.getData();
                if (null != imageUri) {

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                        // Get the real path to the image
                        String imagePath = imageUtils.getRealPathFromURI(requireContext(), imageUri);

                        // Fix orientation using `handleImageRotation`
                        Bitmap rotatedBitmap = imageUtils.handleImageRotation(imagePath, bitmap);
                        if (imageUtils.isImageTooLarge(rotatedBitmap)){
                            Toast.makeText(requireContext(), "Image is too large", Toast.LENGTH_LONG).show();
                            imageBitmap = null;;
                        } else {
                            imageBitmap = rotatedBitmap;
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}