package com.example.oblong;

import android.graphics.Bitmap;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class QRcodeGenerationTest {
    private qr_generator qrGenerator;
    private FirebaseStorage firebaseStorage;

    @Before
    public void setUp() {
        qrGenerator = new qr_generator();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    @Test
    public void testGenerateAndUploadQRCode() throws InterruptedException {
        // Generate QR Code with test data
        Bitmap qrCodeBitmap = qrGenerator.generateQRCode("Test Event", "This is a test event description",
                "2024-12-31", "http://example.com/test_image.jpg");

        assertNotNull("QR code generation failed", qrCodeBitmap);


        String uniqueID = qrGenerator.getUniqueID();
        assertNotNull("Unique ID generation failed", uniqueID);


        CountDownLatch latch = new CountDownLatch(1);


        qrGenerator.uploadQRCodeToFirebase(qrCodeBitmap, uniqueID);

        // Check that the file has been uploaded to Firebase
        StorageReference storageRef = firebaseStorage.getReference().child("qr_codes/" + uniqueID + ".jpg");
        storageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    assertNotNull("Download URL is null, upload failed", uri);
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    throw new AssertionError("Failed to upload QR code to Firebase", e);
                });

        // Wait for the upload to complete
        assertTrue("Upload did not complete in time", latch.await(10, TimeUnit.SECONDS));
    }

    @After
    public void tearDown() {
        // Clean up by deleting the uploaded QR code from Firebase after the test
        String uniqueID = qrGenerator.getUniqueID();
        if (uniqueID != null) {
            firebaseStorage.getReference().child("qr_codes/" + uniqueID + ".jpg").delete();
        }
    }
}