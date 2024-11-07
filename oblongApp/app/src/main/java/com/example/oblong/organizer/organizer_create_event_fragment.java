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

public class organizer_create_event_fragment extends Fragment {

    private EditText eventNameInput;
    private EditText eventDescriptionInput;
    private Button uploadImageButton;
    private EditText maxCapacityInput;
    private Button createEventButton;
    private Button cancelButton;

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

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get all info user entered:
                String eventName = eventNameInput.getText().toString();
                String eventDescription = eventDescriptionInput.getText().toString();
                Long maxCapacity;
                if(maxCapacityInput.getText().toString() == ""){
                    maxCapacity = -1L;
                }else{
                    maxCapacity = Long.parseLong(maxCapacityInput.getText().toString());
                }



                //When we click this we want to take all this info and put it into database
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> event = new HashMap<>();

                qr_generator qr_gen = new qr_generator();

                event.put("capacity", maxCapacity);
                event.put("dateAndTime", FieldValue.serverTimestamp());
                event.put("description", eventDescription);
                event.put("drawDate", FieldValue.serverTimestamp());
                event.put("location", new GeoPoint(0,0));
                event.put("name", eventName);

                if (imageBitmap != null) {
                    String imageBase64 = imageUtils.bitmapToBase64(imageBitmap);
                    event.put("poster", imageBase64);
                }else{
                    event.put("poster", "image");
                }
                event.put("QR", "");

                db.collection("events").add(event).addOnSuccessListener(documentReference -> {
                    String eventID = documentReference.getId();
                    Bitmap qrBitmap = qr_gen.generateQRCode(eventID);
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
                        imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                }
            }
        }
    }
}