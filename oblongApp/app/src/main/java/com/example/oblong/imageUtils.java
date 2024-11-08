package com.example.oblong;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
/**
 * Utility class for encoding Bitmaps to Base64 strings and decoding Base64 strings back to Bitmaps.
 * for storage in Firebase since Base64 gets stored as a string
 */
public class imageUtils {

    /**
     * Converts a Bitmap image to a Base64-encoded string.
     *
     * @param bitmap The Bitmap image to encode.
     * @return A Base64-encoded string representing the image.
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        int quality;
        quality = 25;   // Change quality to change size of the resulting file

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    /**
     * Converts a Base64-encoded string back to a Bitmap image.
     *
     * @param base64String The Base64-encoded string to decode.
     * @return A Bitmap image decoded from the Base64 string.
     */
    public static Bitmap base64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}

