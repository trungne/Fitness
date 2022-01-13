package com.main.fitness.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.main.fitness.R;
import com.main.fitness.data.ViewModel.UserViewModel;
import com.main.fitness.ui.fragments.UserFragment;

import java.util.HashMap;


public class EditInformationActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userDisplayName,userPhone;
    private UserViewModel userViewModel;
    private Button updateProfileButton, exitProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        this.userEmail = findViewById(R.id.EditInfoActivityNewUserEmailValue);
        this.userDisplayName = findViewById(R.id.EditInfoActivityNewUserDisplayNameValue);
        this.userPhone = findViewById(R.id.EditInfoActivityNewUserPhoneValue);
        this.updateProfileButton = findViewById(R.id.EditInfoActivityUpdateButton);
        this.exitProfileButton = findViewById(R.id.EditInfoActivityExitButton);

        this.userViewModel = new ViewModelProvider(this).get(UserViewModel .class);

        updateProfileButton.setOnClickListener(this::updateUserProfile);
        exitProfileButton.setOnClickListener(this::exitUserProfile);
    }

    private void updateUserProfile(View view) {
        if (userDisplayName.getText().length() == 0 && userPhone.getText().length() == 0
                && userEmail.getText().length() == 0) {
            Toast.makeText(this, "Please insert a name or a phone or an email.", Toast.LENGTH_SHORT).show();
        }
        if (userDisplayName.getText().length() != 0 || userPhone.getText().length() != 0) {
            if (userDisplayName.getText().length() != 0 && userDisplayName.getText().length() < 2) {
                Toast.makeText(this, "Please insert a name.", Toast.LENGTH_SHORT).show();
            } else if (userPhone.getText().length() > 1 && userPhone.getText().length() != 10) {
                Toast.makeText(this, "Please insert 10-digit phone number.", Toast.LENGTH_SHORT).show();
            } else if (userEmail.getText().length() != 0 && !userEmail.getText().toString().contains("@")) {
                Toast.makeText(this, "Please insert a valid email.", Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, Object> newUser = new HashMap<>();
                newUser.put( UserViewModel.DISPLAY_NAME_FIELD , userDisplayName.getText().toString());
                newUser.put( UserViewModel.PHONE_NUMBER_FIELD , userPhone.getText().toString());
                String uid = this.userViewModel.getFirebaseUser().getUid();
                //Update User information
                this.userViewModel.updateAppUser(uid, newUser).addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()){
                        return;
                    }
                    Toast.makeText(EditInformationActivity.this, "Update User's profile successful!", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void exitUserProfile(View view) {
        finish();
    }
}
