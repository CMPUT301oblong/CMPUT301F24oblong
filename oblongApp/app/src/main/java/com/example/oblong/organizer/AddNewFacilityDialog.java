package com.example.oblong.organizer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.oblong.R;
import com.example.oblong.entrant.EntrantProfileScreenFragment;
import com.example.oblong.inputValidator;

/**
 * Dialog fragment for adding a facility to a user profile.
 * <p>
 * This fragment allows the user to add their facility information, including
 * name, email, and phone number.
 * <p>
 * Facility data is saved to the database when the user inputs valid data and adds the facility.
 */
public class AddNewFacilityDialog extends DialogFragment {
    /**
     * The {@code setListener} sets the AddFacilityDialog listener on the EntrantProfileScreenFragment
     * @param entrantProfileScreenFragment
     */
    public void setListener(EntrantProfileScreenFragment entrantProfileScreenFragment) {
        this.listener = entrantProfileScreenFragment;
    }

    public interface AddFacilityDialogListener {
        void addFacility(String name, String email, String phone);
    }

    private AddFacilityDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * {@code onCreateDialog} is called to have the fragment instantiate its user AddFacilityDialog fragment.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return dialog to be hosted in the fragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_new_user, null);
        TextView title = view.findViewById(R.id.dialogTitle);
        EditText name = view.findViewById(R.id.editTextText);
        EditText email = view.findViewById(R.id.editTextTextEmailAddress);
        EditText phone = view.findViewById(R.id.editTextPhone);

        title.setText("Create A New Facility:");
        name.setHint("Facility Name");
        email.setHint("billybob@gmail.com");
        phone.setHint("(OPTIONAL) 780-xxx-xxxx");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", null);  // Set null to override the default closing behavior


        AlertDialog dialog = builder.create();

        // Override onClick for Positive Button to handle validation
        dialog.setOnShowListener(dialogInterface ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    // Perform validation before adding the facility
                    inputValidator validator = new inputValidator(getContext());
                    if (validator.validateFacilityProfile(name.getText().toString(), email.getText().toString(), phone.getText().toString())) {
                        listener.addFacility(name.getText().toString(), email.getText().toString(), phone.getText().toString());
                        dialog.dismiss();  // Close the dialog only if validation succeeds
                    }
                })
        );

        return dialog;
    }


}
