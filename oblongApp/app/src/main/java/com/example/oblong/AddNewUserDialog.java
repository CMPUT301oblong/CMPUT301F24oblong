package com.example.oblong;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class AddNewUserDialog extends DialogFragment {
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
                    inputValidator validator = new inputValidator(getContext());
                    if (validator.validateInput(name.getText().toString(), email.getText().toString(), phone.getText().toString())) {
                        listener.addUser(name.getText().toString(), email.getText().toString(), phone.getText().toString());
                        dialog.dismiss();  // Close the dialog only if validation succeeds
                    }
                })
        );

        return dialog;
    }


}
