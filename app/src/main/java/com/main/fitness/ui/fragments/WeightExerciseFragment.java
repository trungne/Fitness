package com.main.fitness.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.main.fitness.R;
import com.main.fitness.data.Model.Exercise;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.ui.activities.gym.ExerciseDetailActivity;
import com.main.fitness.ui.adapters.ExerciseAdapter;
import com.main.fitness.ui.menu.CustomDropdownMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeightExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeightExerciseFragment extends Fragment {
    private static final String TAG = "WeightExerciseFragment";
    public WeightExerciseFragment() {
        // Required empty public constructor
    }

    public static WeightExerciseFragment newInstance() {
        WeightExerciseFragment fragment = new WeightExerciseFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    private AssetsViewModel assetsViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        this.assetsViewModel = new ViewModelProvider(requireActivity()).get(AssetsViewModel.class);
    }

    private CustomDropdownMenu autoCompleteTextView;
    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weight_exercise, container, false);
        this.autoCompleteTextView = view.findViewById(R.id.WeightExerciseAutoCompleteTextView);
        this.autoCompleteTextView.setInputType(InputType.TYPE_NULL);

        this.exerciseAdapter = new ExerciseAdapter(new ArrayList<>());
        this.exerciseAdapter.setOnExerciseClickListener(folderPath -> {
            Intent intent = new Intent(requireActivity(), ExerciseDetailActivity.class);
            intent.putExtra(ExerciseDetailActivity.PATH_KEY, folderPath);
            startActivity(intent);
        });

        this.recyclerView = view.findViewById(R.id.WeightExerciseRecycleView);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        this.recyclerView.setAdapter(this.exerciseAdapter);

        loadOptionsForBodyParts();
        return view;
    }

    private void loadOptionsForBodyParts(){
        String[] folders = this.assetsViewModel.getExerciseTypes();
        Log.i(TAG, Arrays.toString(folders));
        if (folders == null){
            // handle cases when no folders are found
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.option_exercise_body_part, Arrays.asList(folders));
        this.autoCompleteTextView.setAdapter(adapter);
        this.autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String exerciseType = (String) parent.getItemAtPosition(position);
            Log.i(TAG, "exerciseType: " + exerciseType);
            List<Exercise> exerciseList = this.assetsViewModel.getExercises(exerciseType);
            this.exerciseAdapter.updateExerciseList(exerciseList);
//            this.recyclerView.setAdapter(this.exerciseAdapter);
            this.exerciseAdapter.notifyDataSetChanged();
        });

        this.autoCompleteTextView.setSelection(0);

    }
}