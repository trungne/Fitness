package com.main.fitness.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.main.fitness.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GymFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GymFragment extends Fragment {
    public GymFragment() {
        // Required empty public constructor
    }

    public static GymFragment newInstance() {
        GymFragment fragment = new GymFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gym, container, false);
    }
}