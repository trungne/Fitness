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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.main.fitness.R;
import com.main.fitness.data.Model.AppUser;
import com.main.fitness.data.ViewModel.UserViewModel;
import com.main.fitness.ui.activities.EditInformationActivity;
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

    private Button signOutButton;
    private UserViewModel userViewModel;

    private TextView  userEmail;
    private TextView userDisplayName,userPhone;
    private MaterialToolbar toolbar;

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_user, container, false);

        //Button wire
        this.signOutButton = this.rootView.findViewById(R.id.UserFragmentSignOutButton);
        this.signOutButton.setOnClickListener(this::signOut);

        this.userViewModel = new ViewModelProvider(this)
                .get(UserViewModel .class);


        this.userDisplayName = this.rootView.findViewById(R.id.fragmentUserDisplayNameValue);
        this.userEmail = this.rootView.findViewById(R.id.fragmentUserEmailValue);
        this.userPhone = this.rootView.findViewById(R.id.fragmentUserPhoneValue);


        toolbar = this.rootView.findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.userFragmentEditInfo) {
                startActivity(new Intent(requireActivity(), EditInformationActivity.class));
            }
            return false;
        });
        getUserData();
        return this.rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserData();
    }

    public void getUserData(){
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
    }


    //Sign Out of the current account
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