package com.main.fitness.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.main.fitness.R;
import com.main.fitness.data.FileUtils;
import com.main.fitness.data.Model.WorkoutExercise;
import com.main.fitness.data.Model.WorkoutSession;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.data.ViewModel.WorkOutScheduleViewModel;
import com.main.fitness.data.ViewModel.WorkoutScheduleViewModelFactory;

public class WorkoutSessionActivity extends AppCompatActivity {
    private static final String TAG = "WorkoutSessionActivity";
    public static final String WORKOUT_PROGRAM_FOLDER_PATH_KEY = "com.main.fitness.ui.activities.WorkoutSessionActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY";

    private TextView exerciseName, targetMuscles, exerciseOrder;
    private ImageView exerciseIllustration;
    private FloatingActionButton prevButton, nextButton;
    private LinearLayout repsLayout, weightsLayout;
    private Button finishButton;

    private AssetsViewModel assetsViewModel;
    private WorkOutScheduleViewModel workOutScheduleViewModel;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session);
        this.exerciseName = findViewById(R.id.WorkoutSessionExerciseName);
        this.targetMuscles = findViewById(R.id.WorkoutSessionTargetMuscleValues);
        this.exerciseOrder = findViewById(R.id.WorkoutSessionCurrentExerciseOrder);
        this.exerciseIllustration = findViewById(R.id.WorkoutSessionExerciseIllustration);
        this.finishButton = findViewById(R.id.WorkoutSessionFinish);

        this.repsLayout = findViewById(R.id.WorkoutSessionRepsLinearLayout);
        this.weightsLayout = findViewById(R.id.WorkoutSessionWeightsLinearLayout);


        this.prevButton = findViewById(R.id.WorkoutSessionPreviousExercise);
        this.nextButton = findViewById(R.id.WorkoutSessionNextExercise);

        Intent intent = getIntent();
        if (intent == null){
            finish();
            return;
        }

        String workoutProgramPath = intent.getStringExtra(WORKOUT_PROGRAM_FOLDER_PATH_KEY);
        if (TextUtils.isEmpty(workoutProgramPath)){
            Log.i(TAG, "cannot start workout session activity");
            finish();
            return;
        }

        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);

        // TODO: get current day in workout program, create SQL lite to cache user workout info
        // int day = get....

        this.assetsViewModel.getWorkoutSchedule(workoutProgramPath, 0).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()){
                Toast.makeText(this, "Cannot load session!", Toast.LENGTH_SHORT).show();
                return;
            }

            WorkoutScheduleViewModelFactory factory = new WorkoutScheduleViewModelFactory(getApplication(), task.getResult());
            this.workOutScheduleViewModel = new ViewModelProvider(this, factory).get(WorkOutScheduleViewModel.class);

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

            this.workOutScheduleViewModel.getExerciseOrderLiveData().observe(this, integer -> {
                showExerciseOrder(integer, this.workOutScheduleViewModel.getExerciseSize());
            });

            this.workOutScheduleViewModel.getWorkoutSetMutableLiveData().observe(this, workoutSet -> {
                this.weightsLayout.removeAllViews();
                this.repsLayout.removeAllViews();

                String name = workoutSet.getExercise();
                showExercise(name);


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);

                int[] reps = workoutSet.getReps();
                for (int rep : reps) {
                    TextView textView = new TextView(this);
                    textView.setTextSize(24);
                    textView.setText(String.valueOf(rep));
                    textView.setLayoutParams(layoutParams);
                    textView.setGravity(Gravity.CENTER);
                    this.repsLayout.addView(textView);
                }
                this.repsLayout.requestLayout();

                int[] weights = workoutSet.weight;
                for (int weight : weights) {
                    TextView textView = new TextView(this);
                    textView.setTextSize(24);
                    String text = weight + "%";
                    textView.setText(text);
                    textView.setGravity(Gravity.CENTER);
                    textView.setLayoutParams(layoutParams);
                    this.weightsLayout.addView(textView);
                }
                this.weightsLayout.requestLayout();


            });

            this.prevButton.setOnClickListener(v -> {
                this.workOutScheduleViewModel.previousExercise();
            });

            this.nextButton.setOnClickListener(v -> {
                this.workOutScheduleViewModel.nextExercise();
            });
        });
    }

    private void showExerciseOrder(int current, int total){
        String text = (current + 1) + "/" + total;
        this.exerciseOrder.setText(text);
    }



    private void showExercise(String exerciseName){
        WorkoutExercise e = this.assetsViewModel.getExerciseByName(exerciseName);


        if (e == null){
            Toast.makeText(this, "Cannot load exercise info!", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = FileUtils.toTitleCase(e.getName());
        this.exerciseName.setText(name);
        this.exerciseIllustration.setImageDrawable(e.getIllustration());
    }
}