package com.example.oblong;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import android.util.Patterns;


public class AddNewUserFragment extends DialogFragment {
    interface AddUserDialogListener {
        void addUser(String name, String email, String phone);
    }

    private AddUserDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddUserDialogListener) {
            listener = (AddUserDialogListener) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement AddUserDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_new_user, null);
        EditText name = view.findViewById(R.id.editTextText);
        EditText email = view.findViewById(R.id.editTextTextEmailAddress);
        EditText phone = view.findViewById(R.id.editTextPhone);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", null);  // Set null to override the default closing behavior


        AlertDialog dialog = builder.create();

        // Override onClick for Positive Button to handle validation
        dialog.setOnShowListener(dialogInterface ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    // Perform validation before adding the user
                    if (validateInput(name.getText().toString(), email.getText().toString(), phone.getText().toString())) {
                        listener.addUser(name.getText().toString(), email.getText().toString(), phone.getText().toString());
                        dialog.dismiss();  // Close the dialog only if validation succeeds
                    }
                })
        );

        return dialog;
    }


    private boolean validateInput(String name, String email, String phone) {
        if (name.isEmpty() || email.isEmpty()){
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate that two names are provided and they are valid
        String[] names = name.split(" ");
        if (names.length < 2 || !name.matches("[a-zA-Z ]+")) {
            Toast.makeText(getContext(), "Please enter a valid first and last name", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate optional phone format (example: checking for 10-digit numbers) or it is empty
        if (!phone.isEmpty() && !phone.matches("\\d{10}")) {
            Toast.makeText(getContext(), "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
