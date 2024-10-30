package com.example.oblong;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONObject;

public class qr_generator {
    public Bitmap generateQRCode(String eventName, String eventDescription, String drawDate, String uniqueID, String imageUrl, int size) {
        try {
            // Create JSON object with event data
            JSONObject eventData = new JSONObject();
            eventData.put("eventName", eventName);
            eventData.put("eventDescription", eventDescription);
            eventData.put("drawDate", drawDate);
            eventData.put("uniqueID", uniqueID);
            eventData.put("imageUrl", imageUrl);  // Optional image URL

            // Convert JSON to string and generate QR code
            String data = eventData.toString();
            QRCodeWriter writer = new QRCodeWriter();
            Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
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

