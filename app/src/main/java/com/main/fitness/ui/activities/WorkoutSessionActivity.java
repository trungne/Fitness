package com.main.fitness.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.main.fitness.R;
import com.main.fitness.data.FileUtils;
import com.main.fitness.data.Model.Exercise;
import com.main.fitness.data.Model.TrainingSession;
import com.main.fitness.data.Model.WorkoutSet;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.data.ViewModel.TrainingSessionViewModel;

import java.io.File;

public class WorkoutSessionActivity extends AppCompatActivity {
    private static final String TAG = "WorkoutSessionActivity";
    public static final String WORKOUT_PROGRAM_FOLDER_PATH_KEY = "com.main.fitness.ui.activities.WorkoutSessionActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY";

    private TextView timer;
    private TextView exerciseName, targetMuscles, exerciseOrder;
    private ImageView exerciseIllustration;
    private FloatingActionButton prevButton, nextButton;

    private AssetsViewModel assetsViewModel;
    private TrainingSessionViewModel trainingSessionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session);
        this.timer = findViewById(R.id.WorkoutSessionTimer);
        this.exerciseName = findViewById(R.id.WorkoutSessionExerciseName);
        this.targetMuscles = findViewById(R.id.WorkoutSessionTargetMuscleValues);
        this.exerciseOrder = findViewById(R.id.WorkoutSessionCurrentExerciseOrder);
        this.exerciseIllustration = findViewById(R.id.WorkoutSessionExerciseIllustration);
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
        this.trainingSessionViewModel = new ViewModelProvider(this).get(TrainingSessionViewModel.class);

        // TODO: get current day in workout program, create SQL lite to cache user workout info
        // int day = get....

        this.assetsViewModel.getTrainingSession(workoutProgramPath, 0).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()){
                Toast.makeText(this, "Cannot load session!", Toast.LENGTH_SHORT).show();
                return;
            }

            TrainingSession trainingSession = task.getResult();
            String[] targetMuscles = trainingSession.getTargetMuscles();
            StringBuilder stringBuilder = new StringBuilder();

            for(int i = 0; i < targetMuscles.length; i++){
                stringBuilder.append(targetMuscles[i]);
                // if not last item
                if (i < targetMuscles.length - 1){
                    stringBuilder.append(" - ");
                }
            }

            this.targetMuscles.setText(stringBuilder.toString());

            this.trainingSessionViewModel.setTrainingSession(trainingSession);
            this.trainingSessionViewModel.getExerciseOrderLiveData().observe(this, integer -> {
                showExerciseOrder(integer, this.trainingSessionViewModel.getExerciseSize());
            });
            this.trainingSessionViewModel.getWorkoutSetMutableLiveData().observe(this, workoutSet -> {
                String name = workoutSet.getExercise();
                showExercise(workoutProgramPath, name);
            });

            this.prevButton.setOnClickListener(v -> {
                this.trainingSessionViewModel.previousExercise();

            });

            this.nextButton.setOnClickListener(v -> {
                this.trainingSessionViewModel.nextExercise();
            });
        });
    }

    private void showExerciseOrder(int current, int total){
        String text = (current + 1) + "/" + total;
        this.exerciseOrder.setText(text);
    }



    private void showExercise(String workoutFolderPath, String exerciseName){
        Exercise e = this.assetsViewModel.getExerciseByName(exerciseName);


        if (e == null){
            Toast.makeText(this, "Cannot load exercise info!", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = FileUtils.toTitleCase(e.getName());
        this.exerciseName.setText(name);

        Log.i(TAG, name);
        this.exerciseIllustration.setImageDrawable(e.getIllustration());
    }
}