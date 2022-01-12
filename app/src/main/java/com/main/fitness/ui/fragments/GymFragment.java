package com.main.fitness.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.main.fitness.R;
import com.main.fitness.data.ViewModel.UserViewModel;
import com.main.fitness.ui.activities.ExerciseBankActivity;
import com.main.fitness.ui.activities.FAQAndTerminologyActivity;
import com.main.fitness.ui.activities.ProgramListActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GymFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GymFragment extends Fragment {
    private static final String TAG = "GymFragment";
    public GymFragment() {
        // Required empty public constructor
    }

    public static GymFragment newInstance() {
        GymFragment fragment = new GymFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    private UserViewModel userViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        if (!this.userViewModel.isLoggedIn()){
            getParentFragmentManager().beginTransaction().replace(R.id.MainActivityFragmentContainer, RequireSignInFragment.newInstance()).commit();
        }
    }

    Button workoutProgramButton, faqAndTermButton, exerciseBankButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gym, container, false);
        this.workoutProgramButton = rootView.findViewById(R.id.gymStrength);
        this.faqAndTermButton = rootView.findViewById(R.id.gymFAQAndTerminology);
        this.exerciseBankButton = rootView.findViewById(R.id.gymExerciseBank);
        Intent programListActivityIntent = new Intent(requireActivity(), ProgramListActivity.class);

        this.workoutProgramButton.setOnClickListener(v -> {
            programListActivityIntent.putExtra(ProgramListActivity.PROGRAMS_KEY, ProgramListActivity.STRENGTH_PROGRAMS);
            startActivity(programListActivityIntent);
        });

        this.faqAndTermButton.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), FAQAndTerminologyActivity.class));
        });

        this.exerciseBankButton.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), ExerciseBankActivity.class));
        });

        return rootView;
    }
}