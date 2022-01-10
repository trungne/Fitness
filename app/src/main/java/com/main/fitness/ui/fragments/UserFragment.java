package com.main.fitness.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.main.fitness.R;
import com.main.fitness.data.Model.AppUser;
import com.main.fitness.data.ViewModel.UserViewModel;
import com.main.fitness.ui.activities.MainActivity;
import com.main.fitness.ui.activities.MyRunRecordsActivity;

import java.util.HashMap;


public class UserFragment extends Fragment {
    private static final String TAG = "UserFragment";
    public UserFragment() {
        // Required empty public constructor
    }


    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    private Button signOutButton, updateProfileButton, runRecordsButton;
    private UserViewModel userViewModel;

    private TextView  userEmail;
    private EditText userDisplayName,userPhone;

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_user, container, false);

        //Button wire
        this.signOutButton = this.rootView.findViewById(R.id.UserFragmentSignoutButton);
        this.signOutButton.setOnClickListener(this::signOut);
        this.updateProfileButton = this.rootView.findViewById(R.id.UserFragmentUpdateButton);
        this.updateProfileButton.setOnClickListener(this::updateUserProfile);
        this.runRecordsButton = this.rootView.findViewById(R.id.UserFragmentRunRecordListButton);
        this.runRecordsButton.setOnClickListener((this::viewRunRecordList));

        this.userViewModel = new ViewModelProvider(this)
                .get(UserViewModel .class);


        this.userDisplayName = this.rootView.findViewById(R.id.fragmentUserDisplayNameValue);
        this.userEmail = this.rootView.findViewById(R.id.fragmentUserEmailValue);
        this.userPhone = this.rootView.findViewById(R.id.fragmentUserPhoneValue);




        //Get All information of the current user
        String uid = this.userViewModel.getFirebaseUser().getUid();
        this.userViewModel.getUser(uid)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (!task.isSuccessful() || task.getResult() == null){
                        //handle
                        return;
                    }

                    AppUser appUser = task.getResult();
                    this.userDisplayName.setText(appUser.getDisplayName());
                    this.userEmail.setText(appUser.getEmail());
                    this.userPhone.setText(appUser.getPhoneNumber());

                    // set other attributes
                });

        return this.rootView;
    }
    //View Run record list
    private void viewRunRecordList(View view) {
        Intent intent = new Intent(requireActivity(), MyRunRecordsActivity.class);
        startActivity(intent);
    }

    // Update User Profile
    private void updateUserProfile(View view) {
        if(userDisplayName.getText().length() < 1){
            Toast.makeText(requireActivity(), "Please insert a name.", Toast.LENGTH_SHORT).show();
        } else if( userPhone.getText().length() != 10){
            Toast.makeText(requireActivity(), "Please insert 10-digit phone number.", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> newUser = new HashMap<>();
            newUser.put( UserViewModel.DISPLAY_NAME_FIELD , userDisplayName.getText().toString());
            newUser.put( UserViewModel.PHONE_NUMBER_FIELD , userPhone.getText().toString());
            String uid = this.userViewModel.getFirebaseUser().getUid();
            //Update User information
            this.userViewModel.updateAppUser(uid, newUser).addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        // handle
                        return;
                    }
                    Toast.makeText(requireActivity(), "Update User's profile successful!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    //Sign Out of the current accout
    public void signOut(View v){
        AuthUI.getInstance()
                .signOut(requireActivity())
                .addOnCompleteListener(requireActivity(), task -> {
                    if (!task.isSuccessful()){
                        // handle
                        return;
                    }

                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.MainActivityFragmentContainer,
                                    RequireSignInFragment.newInstance())
                            .commit();
                    Toast.makeText(requireActivity(), "User signed out successfully!", Toast.LENGTH_SHORT).show();
                });
    }


}