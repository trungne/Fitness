package com.main.fitness.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.main.fitness.R;

public class TravelledStatusFragement extends Fragment {

    private TextView statusFragmentTravelledDistance;
    private TextView statusFragmentTotalDistance;
    private TextView statusFragmentInitialTime;
    private TextView statusFragmentFinishedTime;
    private TextView statusFragmentSteps;
    private TextView statusFragmentTrackCompleted;
    private TextView statusFragmentDuration;



    public TravelledStatusFragement() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TravelledStatusFragement newInstance() {
        TravelledStatusFragement fragment = new TravelledStatusFragement();
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
        View v =  inflater.inflate(R.layout.fragment_travelled_status_fragement, container, false);

        Bundle bundle = this.getArguments();

        //Load xml components
        statusFragmentTotalDistance = v.findViewById(R.id.status_fragment_total_distance);
        statusFragmentTravelledDistance = v.findViewById(R.id.status_fragment_travelled_distance);
        statusFragmentInitialTime = v.findViewById(R.id.status_fragment_initial_time);
        statusFragmentFinishedTime = v.findViewById(R.id.status_fragment_finished_time);
        statusFragmentSteps = v.findViewById(R.id.status_fragment_steps);
        statusFragmentTrackCompleted = v.findViewById(R.id.status_fragment_complete_status);
        statusFragmentDuration = v.findViewById(R.id.status_fragment_duration);



        //Set data and set layout for linear layout
        try{
            //Data for textview
            assert bundle != null;
            statusFragmentTotalDistance.setText("Total distance: " + bundle.getString("userTotalDistance"));
            statusFragmentTravelledDistance.setText("Travelled distance: " + bundle.getString("userTravelledDistance"));
            statusFragmentInitialTime.setText(bundle.getString("userInitialTime"));
            statusFragmentFinishedTime.setText(bundle.getString("userFinishedTime"));
            statusFragmentSteps.setText(bundle.getString("userSteps"));
            statusFragmentTrackCompleted.setText(bundle.getString("userTrackCompletedStatus"));
            statusFragmentDuration.setText(bundle.getString("userDuration"));






        }
        catch(NullPointerException e){
            Toast.makeText(requireActivity(), "NullPointerException occurred !", Toast.LENGTH_SHORT).show();
        }

        return v;
    }



}