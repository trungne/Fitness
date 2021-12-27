package com.main.fitness.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.main.fitness.R;
import com.main.fitness.data.Model.AppUser;
import com.main.fitness.data.ViewModel.UserViewModel;
import com.main.fitness.ui.activities.gym.CardioTrainingActivity;
import com.main.fitness.ui.activities.gym.ExerciseBankActivity;
import com.main.fitness.ui.activities.gym.FAQAndTerminologyActivity;
import com.main.fitness.ui.activities.gym.ProgramListActivity;
import com.main.fitness.ui.activities.gym.StrengthTrainingActivity;
import com.main.fitness.ui.dialogs.ChooseUserLevelDialog;

import java.util.HashMap;

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
    private String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        if (!this.userViewModel.isLoggedIn()){
            getParentFragmentManager().beginTransaction().replace(R.id.MainActivityFragmentContainer, RequireSignInFragment.newInstance()).commit();
            return;
        }

        this.userViewModel.getUser(this.userViewModel.getFirebaseUser().getUid()).addOnCompleteListener(requireActivity(), task -> {
            if (!task.isSuccessful() || task.getResult() == null){
                Toast.makeText(requireActivity(), "Cannot load user's information", Toast.LENGTH_SHORT).show();
                return;
            }
            AppUser appUser = task.getResult();
            this.uid = appUser.getUid();
            if (appUser.getUserLevel() == null){
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MainActivityFragmentContainer, RequireUserLevelFragment.newInstance())
                        .commit();
            }
        });
    }

    CardView strengthButton, cardioButton, faqAndTermButton, exerciseBankButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gym, container, false);
        this.strengthButton = rootView.findViewById(R.id.gymStrength);
        this.cardioButton = rootView.findViewById(R.id.gymCardio);
        this.faqAndTermButton = rootView.findViewById(R.id.gymFAQAndTerminology);
        this.exerciseBankButton = rootView.findViewById(R.id.gymExerciseBank);

        this.strengthButton.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), ProgramListActivity.class));
        });

        this.cardioButton.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), CardioTrainingActivity.class));
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