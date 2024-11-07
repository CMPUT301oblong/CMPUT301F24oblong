package com.example.oblong;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;

@RunWith(AndroidJUnit4.class)
public class imageUtilsTest {

    @Test
    public void testBitmapToBase64() {
        // Create a sample Bitmap
        Bitmap originalBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        originalBitmap.eraseColor(android.graphics.Color.RED); // Fill bitmap with red color for testing

        // Convert Bitmap to Base64 string
        String base64String = imageUtils.bitmapToBase64(originalBitmap);

        // Verify that the Base64 string is not null or empty
        assertNotNull("Base64 string should not be null", base64String);
        assertTrue("Base64 string should not be empty", base64String.length() > 0);
    }
    @Test
    public void testBase64ToBitmap() {
        // Create a sample Bitmap and convert it to a Base64 string
        Bitmap originalBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        originalBitmap.eraseColor(android.graphics.Color.RED); // Fill bitmap with red color for testing
        String base64String = imageUtils.bitmapToBase64(originalBitmap);

        // Convert Base64 string back to Bitmap
        Bitmap decodedBitmap = imageUtils.base64ToBitmap(base64String);

        // Verify that the decoded Bitmap is not null and has the same dimensions as the original
        assertNotNull("Decoded Bitmap should not be null", decodedBitmap);
        assertEquals("Width should match", originalBitmap.getWidth(), decodedBitmap.getWidth());
        assertEquals("Height should match", originalBitmap.getHeight(), decodedBitmap.getHeight());

        // Check if the decoded bitmap has the same content by comparing bytes
        ByteArrayOutputStream originalStream = new ByteArrayOutputStream();
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, originalStream);
        byte[] originalBytes = originalStream.toByteArray();

        ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
        decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, decodedStream);
        byte[] decodedBytes = decodedStream.toByteArray();

        assertEquals("Bitmaps should have the same content", Base64.encodeToString(originalBytes, Base64.DEFAULT),
                Base64.encodeToString(decodedBytes, Base64.DEFAULT));
    }
}

