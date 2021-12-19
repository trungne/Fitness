package com.main.fitness.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.main.fitness.R;


public class UserFragment extends Fragment {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        this.signOutButton = view.findViewById(R.id.UserFragmentSignoutButton);
        this.signOutButton.setOnClickListener(this::signOut);
        return view;
    }

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