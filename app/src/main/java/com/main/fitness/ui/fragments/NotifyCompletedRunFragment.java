package com.main.fitness.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.main.fitness.R;

import java.text.DecimalFormat;

public class NotifyCompletedRunFragment extends Fragment {

    //XML components
    private TextView notifyFragmentTravelledDistance;
    private TextView notifyFragmentTotalDistance;
    private TextView notifyFragmentInitialTime;
    private TextView notifyFragmentFinishedTime;
    private TextView notifyFragmentSteps;
    private TextView notifyFragmentTrackCompleted;
    private TextView notifyFragmentDuration;
    private Button notifyFragmentFinishButton;
    private BottomNavigationView navBar;



    public NotifyCompletedRunFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NotifyCompletedRunFragment newInstance() {
        NotifyCompletedRunFragment fragment = new NotifyCompletedRunFragment();
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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_notify_completed_run, container, false);

        Bundle bundle = this.getArguments();

        //Load xml components
        notifyFragmentTotalDistance = v.findViewById(R.id.notify_fragment_total_distance);
        notifyFragmentTravelledDistance = v.findViewById(R.id.notify_fragment_travelled_distance);
        notifyFragmentInitialTime = v.findViewById(R.id.notify_fragment_initial_time);
        notifyFragmentFinishedTime = v.findViewById(R.id.notify_fragment_finished_time);
        notifyFragmentSteps = v.findViewById(R.id.notify_fragment_total_steps);
        notifyFragmentTrackCompleted = v.findViewById(R.id.notify_fragment_track_completed);
        notifyFragmentDuration = v.findViewById(R.id.notify_fragment_duration);
        notifyFragmentFinishButton = v.findViewById(R.id.notify_fragment_button_finish);

        notifyFragmentFinishButton.setOnClickListener(this::FinishButton);

        navBar = requireActivity().findViewById(R.id.MainActivityBottomNavigationView);
        navBar.setVisibility(View.GONE);





        //Set data and set layout for linear layout
        try{
            //Data for textview
            assert bundle != null;
            DecimalFormat df = new DecimalFormat("#.00");
            double totalDistance = Double.parseDouble(bundle.getString("userTotalDistance"));
            double travelledDistance = Double.parseDouble(bundle.getString("userTravelledDistance"));
            notifyFragmentTotalDistance.setText("" + df.format(totalDistance));
            notifyFragmentTravelledDistance.setText("" + df.format(travelledDistance));
            notifyFragmentInitialTime.setText(bundle.getString("userInitialTime"));
            notifyFragmentFinishedTime.setText(bundle.getString("userFinishedTime"));
            notifyFragmentSteps.setText(bundle.getString("userSteps"));
            notifyFragmentTrackCompleted.setText(bundle.getString("userTrackCompletedStatus"));
            notifyFragmentDuration.setText(bundle.getString("userDuration"));

        }
        catch(NullPointerException e){
            Toast.makeText(requireActivity(), "NullPointerException occurred !", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private void FinishButton(View v){
        navBar = requireActivity().findViewById(R.id.MainActivityBottomNavigationView);
        navBar.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //Remove this fragment
        fragmentManager.popBackStack();
        fragmentManager.popBackStack();
        fragmentManager.popBackStack();
        fragmentTransaction.commit();
    }



}