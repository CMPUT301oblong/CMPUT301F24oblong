package com.example.oblong;

import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

public class qr_scanner extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        db.collection("users").document(userId).collection("associatedEvents")
                .document(uniqueId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Event is already associated with the user; navigate to description
                            Intent intent = new Intent(this, EntrantEventDescriptionActivity.class);
                            intent.putExtra("eventName", document.getString("eventName"));
                            intent.putExtra("eventDescription", document.getString("eventDescription"));
                            intent.putExtra("drawDate", document.getString("drawDate"));
                            intent.putExtra("uniqueId", uniqueId);
                            startActivity(intent);
                        } else {
                            // Event not associated; navigate to join event activity
                            Intent intent = new Intent(this, EntrantJoinEventActivity.class);
                            intent.putExtra("uniqueId", uniqueId);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(this, "Error checking event association", Toast.LENGTH_SHORT).show();
                    }
                });
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == PERMISSION_REQUEST_CAMERA) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                initQRCodeScanner();
//            } else {
//                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
//                finish();
//            }
//        }
//    }

}
