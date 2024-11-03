package com.example.oblong;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class qr_generator {
    private FirebaseFirestore firestore;
    private String uniqueID;

    public qr_generator() {
        // Initialize Firebase Firestore instance
        firestore = FirebaseFirestore.getInstance();
    }

    public String getUniqueID() {
        return uniqueID;
    }

    /**
     * Generates a QR code containing only the unique ID.
     *
     * @return QR code bitmap with unique ID embedded.
     */
    public Bitmap generateQRCode() {
        uniqueID = UUID.randomUUID().toString();  // Generate a unique ID
        int size = 512;

        try {
            QRCodeWriter writer = new QRCodeWriter();
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(uniqueID, BarcodeFormat.QR_CODE, size, size);

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

    /**
     * Saves event details to Firebase under the unique ID.
     *
     * @param eventName        The name of the event.
     * @param eventDescription A description of the event.
     * @param drawDate         The date of the event's draw.
     */
    public void saveEventDetailsToFirebase(String eventName, String eventDescription, String drawDate) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", eventName);
        eventData.put("eventDescription", eventDescription);
        eventData.put("drawDate", drawDate);
        eventData.put("uniqueID", uniqueID);  // Save unique ID for reference

        firestore.collection("events").document(uniqueID).set(eventData)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Event details successfully uploaded"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to upload event details", e));
    }
}
