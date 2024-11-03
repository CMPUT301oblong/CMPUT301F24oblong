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

public class qr_scanner extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Check camera permissions and start scanner
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            initQRCodeScanner();
        }
    }

    private void initQRCodeScanner() {
        // Initialize QR code scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                handleScannedData(result.getContents());  // Only passing uniqueId
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleScannedData(String uniqueId) {
        String userId = auth.getCurrentUser().getUid();

        // Check if event is associated with the user
        db.collection("users").document(userId).collection("associatedEvents")
                .document(uniqueId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Event is already associated with the user; retrieve event details
                            retrieveEventDetails(uniqueId, true);
                        } else {
                            // Event not associated; proceed to join event
                            retrieveEventDetails(uniqueId, false);
                        }
                    } else {
                        Toast.makeText(this, "Error checking event association", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void retrieveEventDetails(String uniqueId, boolean isAssociated) {
        // Fetch event details from Firestore using uniqueId
        db.collection("events").document(uniqueId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extract event details
                        String eventName = documentSnapshot.getString("eventName");
                        String eventDescription = documentSnapshot.getString("eventDescription");
                        String drawDate = documentSnapshot.getString("drawDate");

                        // Navigate based on association status
                        if (isAssociated) {
                            // Navigate to EntrantEventDescriptionActivity with event details
                            Intent intent = new Intent(this, EntrantEventDescriptionActivity.class);
                            intent.putExtra("eventName", eventName);
                            intent.putExtra("eventDescription", eventDescription);
                            intent.putExtra("drawDate", drawDate);
                            intent.putExtra("uniqueId", uniqueId);
                            startActivity(intent);
                        } else {
                            // Navigate to EntrantJoinEventActivity with uniqueId only
                            Intent intent = new Intent(this, EntrantJoinEventActivity.class);
                            intent.putExtra("uniqueId", uniqueId);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching event details", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

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
