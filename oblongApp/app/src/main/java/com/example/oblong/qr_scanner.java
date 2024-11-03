package com.example.oblong;

import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * The {@code qr_scanner} class handles QR code scanning functionality, retrieves event details
 * from Firebase Firestore, and navigates the user to the appropriate activity based on the
 * association of the scanned event.
 *
 * <p>This class is used to scan QR codes and extract event information from Firestore using the
 * unique ID encoded in the QR code. If the event is already associated with the user, they are
 * navigated to the event description page; otherwise, they are directed to the event joining page.
 */
public class qr_scanner extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    /**
     * Initializes the Firebase instances and checks for camera permissions.
     * If permission is granted, initializes the QR code scanner.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            initQRCodeScanner();
        }
    }

    /**
     * Initializes the QR code scanner using {@code IntentIntegrator}.
     * Configures the scanner to scan QR codes with specific orientations and prompts.
     */
    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    /**
     * Handles the result of the QR code scan. If content is found, processes the scanned data.
     *
     * @param requestCode The request code originally supplied to {@link #startActivityForResult}.
     * @param resultCode  The result code returned by the child activity through its {@code setResult()}.
     * @param data        An Intent, which can return result data to the caller.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                handleScannedData(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Checks the association of the scanned event with the user in Firestore.
     * Based on the association, directs to the appropriate activity.
     *
     * @param uniqueId The unique ID of the event extracted from the QR code.
     */
    private void handleScannedData(String uniqueId) {
        String userId = auth.getCurrentUser().getUid();

        db.collection("entrants").document(userId).collection("associatedEvents")
                .document(uniqueId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            retrieveEventDetails(uniqueId, true);
                        } else {
                            retrieveEventDetails(uniqueId, false);
                        }
                    } else {
                        Toast.makeText(this, "Error checking event association", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Retrieves event details from Firestore based on the event's unique ID.
     * Directs the user to the event description or joining page based on association status.
     *
     * @param uniqueId      The unique ID of the event in Firestore.
     * @param isAssociated  {@code true} if the event is associated with the user; otherwise {@code false}.
     */
    private void retrieveEventDetails(String uniqueId, boolean isAssociated) {
        db.collection("events").document(uniqueId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventName = documentSnapshot.getString("eventName");
                        String eventDescription = documentSnapshot.getString("eventDescription");
                        String drawDate = documentSnapshot.getString("drawDate");

                        Intent intent;
                        if (isAssociated) {
                            intent = new Intent(this, EntrantEventDescriptionActivity.class);
                            intent.putExtra("eventName", eventName);
                            intent.putExtra("eventDescription", eventDescription);
                            intent.putExtra("drawDate", drawDate);
                            intent.putExtra("uniqueId", uniqueId);
                        } else {
                            intent = new Intent(this, EntrantJoinEventActivity.class);
                            intent.putExtra("uniqueId", uniqueId);
                        }
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching event details", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    /**
     * Called when the user responds to the permission request for camera access.
     * If permission is granted, initializes the QR code scanner.
     *
     * @param requestCode  The request code passed in {@link #requestPermissions}.
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the requested permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}

