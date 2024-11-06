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
    private String uniqueID;

    public qr_generator() {

    }

    public String getUniqueID() {
        return uniqueID;
    }

    /**
     * Generates a QR code containing only the unique ID.
     *
     * @return QR code bitmap with unique ID embedded.
     */
    public Bitmap generateQRCode(String event) {
        int size = 512;

        try {
            QRCodeWriter writer = new QRCodeWriter();
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(event, BarcodeFormat.QR_CODE, size, size);

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
