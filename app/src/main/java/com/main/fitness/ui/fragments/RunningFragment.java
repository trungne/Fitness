package com.main.fitness.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.main.fitness.R;
import com.main.fitness.ui.activities.RunningMapsActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunningFragment extends Fragment {
    public RunningFragment() {
        // Required empty public constructor
    }

    public static RunningFragment newInstance() {
        RunningFragment fragment = new RunningFragment();
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

    private View view;
    private Button fragmentRunningRun;
    private LinearLayout running_linear_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Request permission from the user
        requestPermission();

        //Set up the view
        View view = inflater.inflate(R.layout.fragment_running, container, false);
        fragmentRunningRun = view.findViewById(R.id.fragmentRunningRun);
        running_linear_layout = view.findViewById(R.id.running_linear_layout);

        fragmentRunningRun.setOnClickListener(this::run);

        return view;
    }

    private void run(View view) {
        if (getActivity() != null){
            startActivity(new Intent(requireActivity(), RunningMapsActivity.class));
        }
    }

    //Request phone permission for location
    public void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, 1);
    }
}