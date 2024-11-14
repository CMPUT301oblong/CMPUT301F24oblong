package com.example.oblong.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oblong.Database;
import com.example.oblong.R;
import com.example.oblong.User;
import com.example.oblong.imageUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Activity class for displaying a list of events.
 */
public class AdminUserProfileView extends AppCompatActivity {
    private User viewed_user;
    private String facility_id;
    private String toast_message;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_entrant_profile_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView userImage = findViewById(R.id.imageView);
        TextView userName = findViewById(R.id.profile_name);
        TextView userEmail = findViewById(R.id.profile_email);
        TextView userPhone = findViewById(R.id.profile_phoneno);
        Button deleteButton = findViewById(R.id.delete_user_button);


        // Set user image, name, email, and phone number
        User user = (User) getIntent().getSerializableExtra("user");
        viewed_user = user;
        // TODO: set up user image
        if(user.getProfilePicture() == null){
            userImage.setImageResource(R.drawable.image_placeholder);
        }else{
            userImage.setImageBitmap(imageUtils.base64ToBitmap(user.getProfilePicture()));
        }
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        userPhone.setText(user.getPhone());
        deleteButton.setOnClickListener(v -> {
            AlertDialog dialog = createDialog();
            dialog.show();
        });

    }

    private AlertDialog createDialog() {
        Log.d("AdminUserProfileView", "createDialog called");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this user?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Delete user from database
                Database db = new Database();
                Database.getCurrentUser(user_id -> {
                    if(!viewed_user.getId().equals(user_id)) {
                        db.deleteUser(AdminUserProfileView.this, viewed_user);
                    } else {
                        Toast.makeText(AdminUserProfileView.this, "You cannot delete yourself", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();

            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("AdminUserProfileView", "User not deleted");
                Toast.makeText(AdminUserProfileView.this, "Deletion cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}
