package com.example.oblong;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.oblong.entrant.EntrantProfileScreenFragment;
import com.example.oblong.organizer.organizer_profile_edit;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
public class OrganizerFacilityTest {

    @Test
    public void testAddFacility() {
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();

        EntrantProfileScreenFragment fragment = new EntrantProfileScreenFragment();
        fragment.addFacility("very unique facility name", "james.a.garfield@examplepetstore.com", "1234567890");

        fdb.collection("facilities").whereEqualTo("name", "very unique facility name").get().addOnSuccessListener(
                queryDocumentSnapshots -> {
                    assertTrue(!queryDocumentSnapshots.isEmpty());
                }
        );
    }

    // FIXME: test not working
    @Test
    public void testEditFacility() {
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        ActivityScenario<organizer_profile_edit> scenario = ActivityScenario.launch(organizer_profile_edit.class);
        scenario.onActivity(activity -> {
            // Perform test actions here
            EditText facilityNameInput = activity.findViewById(R.id.facility_name_editText);
            facilityNameInput.setText("brand new unique facility name");
            EditText facilityEmailInput = activity.findViewById(R.id.facility_email_editText);
            facilityEmailInput.setText("james.wilson@example-pet-store.com");
            EditText facilityPhoneInput = activity.findViewById(R.id.facility_phone_editText);
            facilityPhoneInput.setText("7801111111");

            Button saveChangesButton = activity.findViewById(R.id.save_button);
            saveChangesButton.performClick();

            // Add assertions here
            // For example, check if the facility was updated in the database
            fdb.collection("facilities").whereEqualTo("name", "brand new unique facility name").get().addOnSuccessListener(
                    queryDocumentSnapshots -> {
                        assertTrue(queryDocumentSnapshots.size() > 0);
                    }
            );
        });
    }

}
