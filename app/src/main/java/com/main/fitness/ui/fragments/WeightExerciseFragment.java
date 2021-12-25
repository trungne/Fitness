package com.main.fitness.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.main.fitness.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeightExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeightExerciseFragment extends Fragment {
    public WeightExerciseFragment() {
        // Required empty public constructor
    }

    public static WeightExerciseFragment newInstance() {
        WeightExerciseFragment fragment = new WeightExerciseFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weight_exercise, container, false);
    }
}