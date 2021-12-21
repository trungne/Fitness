package com.main.fitness.ui.fragments;

import android.os.Bundle;

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
    private EditText fragmentRunningDistance;
    private Button fragmentRunningRun;
    private LinearLayout running_linear_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_running, container, false);
        fragmentRunningDistance = view.findViewById(R.id.fragmentRunningDistance);
        fragmentRunningRun = view.findViewById(R.id.fragmentRunningRun);
        running_linear_layout = view.findViewById(R.id.running_linear_layout);

        fragmentRunningRun.setOnClickListener(this::run);

        return view;
    }

    private void run(View view) {
        //User enter or not?
        if (TextUtils.isEmpty(fragmentRunningDistance.getText().toString())) {
            Toast.makeText(requireActivity(), "Please enter the distance you want to run first!", Toast.LENGTH_SHORT).show();
        } else if (Double.parseDouble(fragmentRunningDistance.getText().toString()) <= 0) {
            Toast.makeText(requireActivity(), "The distance must be bigger than 0", Toast.LENGTH_SHORT).show();
        } else {
            //direct to map
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.MainActivityFragmentContainer, new GoogleMapFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}