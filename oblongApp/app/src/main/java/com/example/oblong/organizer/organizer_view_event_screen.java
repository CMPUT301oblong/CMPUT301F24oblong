package com.example.oblong.organizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.R;
import com.example.oblong.qr_generator;

public class organizer_view_event_screen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_view_event_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        qr_generator qr = new qr_generator();
        Intent intent = getIntent();
        Bitmap code = qr.generateQRCode(intent.getStringExtra("event"));
        ImageView qrCode = findViewById(R.id.activity_organizer_view_event_event_description_qr_code_display);

        // https://stackoverflow.com/questions/30027242/set-bitmap-to-imageview
        qrCode.setImageBitmap(code);
    }
}


