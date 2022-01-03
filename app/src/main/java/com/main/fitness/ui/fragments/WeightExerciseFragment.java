package com.main.fitness.ui.fragments;

import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.main.fitness.R;

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

    AssetManager mAssetManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        this.mAssetManager = requireContext().getAssets();
    }

    AutoCompleteTextView autoCompleteTextView;

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
        try {





            String[] options = this.mAssetManager.list("exercise_bank");
            Arrays.sort(options);
            for (String str: options){
                Log.i("WeightExerciseFragment", str);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.option_exercise_body_part, options);
            this.autoCompleteTextView.setText(adapter.getItem(0).toString(), false);
            this.autoCompleteTextView.setAdapter(adapter);
            this.autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("WeightExerciseFragment", (String) parent.getItemAtPosition(position));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}