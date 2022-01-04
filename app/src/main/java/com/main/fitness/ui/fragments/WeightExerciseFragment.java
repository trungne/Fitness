package com.main.fitness.ui.fragments;

import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.main.fitness.R;
import com.main.fitness.data.ViewModel.ExerciseViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.Arrays;

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

    private ExerciseViewModel exerciseViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        this.exerciseViewModel = new ViewModelProvider(requireActivity()).get(ExerciseViewModel.class);
    }

    private AutoCompleteTextView autoCompleteTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weight_exercise, container, false);
        this.autoCompleteTextView = view.findViewById(R.id.WeightExerciseAutoCompleteTextView);
        this.autoCompleteTextView.setInputType(InputType.TYPE_NULL);
        loadOptionsForBodyParts();

        return view;
    }

    private void loadOptionsForBodyParts(){
        String[] folders = this.exerciseViewModel.getExerciseTypes();
        if (folders == null){
            // handle cases when no folders are found
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.option_exercise_body_part, folders);
        this.autoCompleteTextView.setAdapter(adapter);
        this.autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("WeightExerciseFragment", (String) parent.getItemAtPosition(position));
            }
        });

        this.autoCompleteTextView.setText(adapter.getItem(0).toString(), false);

    }
}