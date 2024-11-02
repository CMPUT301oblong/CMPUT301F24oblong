package com.example.oblong;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


// This class is intended to be called by the organizer_create_event_screen class to create the qr code
public class qr_generator {

    //    private FirebaseStorage storage;
//    private FirebaseFirestore firestore;
    private String uniqueID;

    public qr_generator() {
        // Initialize Firebase instances
//        storage = FirebaseStorage.getInstance();
//        firestore = FirebaseFirestore.getInstance();
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public Bitmap generateQRCode(String eventName, String eventDescription, String drawDate, String imageUrl) {
        String uniqueID = UUID.randomUUID().toString();
        int size = 512;

        try {
            // Create JSON object with event data
            JSONObject eventData = new JSONObject();
//            eventData.put("eventName", eventName);
//            eventData.put("eventDescription", eventDescription);
//            eventData.put("drawDate", drawDate);
            eventData.put("uniqueID", uniqueID);
//            eventData.put("imageUrl", imageUrl);

            // Convert JSON to string and generate QR code
            String data = eventData.toString();
            QRCodeWriter writer = new QRCodeWriter();
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
//    public void uploadQRCodeToFirebase(Bitmap qrCodeBitmap, String uniqueID) {
//        // Compress the Bitmap into a JPEG format for storage
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        byte[] qrCodeData = byteArrayOutputStream.toByteArray();
//
//        StorageReference storageRef = storage.getReference().child("qr_codes/" + uniqueID + ".jpg");
//
//        storageRef.putBytes(qrCodeData)
//                .addOnSuccessListener(taskSnapshot -> Log.d("Firebase", "QR code successfully uploaded"))
//                .addOnFailureListener(e -> Log.e("Firebase", "QR code upload failed", e));
//    }
//}
