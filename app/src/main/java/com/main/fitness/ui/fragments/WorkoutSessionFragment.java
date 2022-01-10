package com.main.fitness.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.main.fitness.R;
import com.main.fitness.data.FileUtils;
import com.main.fitness.data.Model.WorkoutExercise;
import com.main.fitness.data.Model.WorkoutSchedule;
import com.main.fitness.data.Model.WorkoutSession;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.data.ViewModel.WorkoutScheduleViewModel;
import com.main.fitness.data.ViewModel.WorkoutScheduleViewModelFactory;
import com.main.fitness.ui.activities.ExerciseDetailActivity;


public class WorkoutSessionFragment extends Fragment {
    private static final String TAG = "WorkoutSessionFragment";
    private static final String WORK_OUT_PROGRAM_PATH_PARAM = "WORK_OUT_PROGRAM_PATH_PARAM";
    private static final String WORK_OUT_PROGRAM_NAME_PARAM = "WORK_OUT_PROGRAM_NAME_PARAM";
    private static final String IS_CURRENT_SESSION_PARAM = "IS_CURRENT_SESSION_PARAM";
    private static final String DAY_PARAM = "DAY_PARAM";

    private String workoutProgramPath;
    private String workoutProgramName;
    private boolean isCurrentSession;
    private int day;

    public WorkoutSessionFragment() {
        // Required empty public constructor
    }

    public static WorkoutSessionFragment newInstance(String workoutProgramName, String workoutProgramPath, int day, boolean isCurrentSession) {
        WorkoutSessionFragment fragment = new WorkoutSessionFragment();
        Log.e(TAG, workoutProgramPath);
        Bundle args = new Bundle();

        args.putString(WORK_OUT_PROGRAM_PATH_PARAM, workoutProgramPath);
        args.putBoolean(IS_CURRENT_SESSION_PARAM, isCurrentSession);
        args.putInt(DAY_PARAM, day);
        args.putString(WORK_OUT_PROGRAM_NAME_PARAM, workoutProgramName);
        fragment.setArguments(args);
        return fragment;
    }

    private AssetsViewModel assetsViewModel;
    private WorkoutScheduleViewModel workOutScheduleViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            workoutProgramPath = getArguments().getString(WORK_OUT_PROGRAM_PATH_PARAM);
            workoutProgramName = getArguments().getString(WORK_OUT_PROGRAM_NAME_PARAM);
            isCurrentSession = getArguments().getBoolean(IS_CURRENT_SESSION_PARAM);
            day = getArguments().getInt(DAY_PARAM);
        }
        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
    }

    private TextView exerciseName, targetMuscles, exerciseOrder;
    private ImageView exerciseIllustration;
    private FloatingActionButton prevButton, nextButton;
    private LinearLayout repsLayout, weightsLayout;
    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout_session, container, false);
        this.exerciseName = view.findViewById(R.id.WorkoutSessionExerciseName);
        this.targetMuscles = view.findViewById(R.id.WorkoutSessionTargetMuscleValues);
        this.exerciseOrder = view.findViewById(R.id.WorkoutSessionCurrentExerciseOrder);
        this.exerciseIllustration = view.findViewById(R.id.WorkoutSessionExerciseIllustration);

        this.repsLayout = view.findViewById(R.id.WorkoutSessionRepsLinearLayout);
        this.weightsLayout = view.findViewById(R.id.WorkoutSessionWeightsLinearLayout);

        this.prevButton = view.findViewById(R.id.WorkoutSessionPreviousExercise);
        this.nextButton = view.findViewById(R.id.WorkoutSessionNextExercise);
        this.button = view.findViewById(R.id.WorkoutSessionFinish);

        showSession();

        return view;
    }

    private void showSession(){
        this.assetsViewModel.getWorkoutSchedule(workoutProgramPath, day).addOnCompleteListener(requireActivity(), task -> {
            if (!task.isSuccessful()){
                Toast.makeText(requireActivity(), "Cannot load session!", Toast.LENGTH_SHORT).show();
                return;
            }
            WorkoutSchedule schedule = task.getResult();
            if (getActivity() == null) return; // DO NOT REMOVE THIS LINE, for some reason it crashes the tab layout if we don't null check here
            WorkoutScheduleViewModelFactory factory = new WorkoutScheduleViewModelFactory(requireActivity().getApplication(), schedule);
            this.workOutScheduleViewModel = new ViewModelProvider(this, factory).get(WorkoutScheduleViewModel.class);

            WorkoutSession workoutSession = task.getResult().getCurrentSession();
            String[] targetMuscles = workoutSession.getTargetMuscles();
            StringBuilder stringBuilder = new StringBuilder();

            for(int i = 0; i < targetMuscles.length; i++){
                stringBuilder.append(targetMuscles[i]);
                // if not last item
                if (i < targetMuscles.length - 1){
                    stringBuilder.append(" - ");
                }
            }

            this.targetMuscles.setText(stringBuilder.toString());

            this.workOutScheduleViewModel.getExerciseOrderLiveData().observe(requireActivity(), integer ->
                    showExerciseOrder(integer, this.workOutScheduleViewModel.getExerciseSize()));

            this.workOutScheduleViewModel.getWorkoutSetMutableLiveData().observe(requireActivity(), workoutSet -> {
                this.weightsLayout.removeAllViews();
                this.repsLayout.removeAllViews();

                String name = workoutSet.getExercise();
                showExercise(name);


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);

                int[] reps = workoutSet.getReps();
                for (int rep : reps) {
                    TextView textView = new TextView(requireActivity());
                    textView.setTextSize(24);
                    textView.setText(String.valueOf(rep));
                    textView.setLayoutParams(layoutParams);
                    textView.setGravity(Gravity.CENTER);
                    this.repsLayout.addView(textView);
                }
                this.repsLayout.requestLayout();

                int[] weights = workoutSet.weight;
                for (int weight : weights) {
                    TextView textView = new TextView(requireActivity());
                    textView.setTextSize(24);
                    String text = weight + "%";
                    textView.setText(text);
                    textView.setGravity(Gravity.CENTER);
                    textView.setLayoutParams(layoutParams);
                    this.weightsLayout.addView(textView);
                }
                this.weightsLayout.requestLayout();


            });

            this.prevButton.setOnClickListener(v -> this.workOutScheduleViewModel.previousExercise());

            this.nextButton.setOnClickListener(v -> this.workOutScheduleViewModel.nextExercise());


            if (isCurrentSession){
                this.button.setText("Finish Session");
                this.button.setBackgroundResource(R.drawable.button_4);
            }
            else{
                this.button.setText("Move To This Session");
            }


        });
    }

    private void showExerciseOrder(int current, int total){
        String text = (current + 1) + "/" + total;
        this.exerciseOrder.setText(text);
    }



    private void showExercise(String exerciseName){
        WorkoutExercise e = this.assetsViewModel.getExerciseByName(exerciseName);

        if (e == null){
            Toast.makeText(requireActivity(), "Cannot load exercise info!", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = FileUtils.toTitleCase(e.getName());
        this.exerciseName.setText(name);
        this.exerciseIllustration.setImageDrawable(e.getIllustration());
        this.exerciseIllustration.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ExerciseDetailActivity.class);
            intent.putExtra(ExerciseDetailActivity.PATH_KEY, e.getFolderPath());
            startActivity(intent);
        });
    }
}