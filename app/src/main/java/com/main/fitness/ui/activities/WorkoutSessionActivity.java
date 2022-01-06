package com.main.fitness.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.main.fitness.R;
import com.main.fitness.data.Model.TrainingSession;
import com.main.fitness.data.Model.WorkoutProgram;
import com.main.fitness.data.ViewModel.AssetsViewModel;

public class WorkoutSessionActivity extends AppCompatActivity {
    private static final String TAG = "WorkoutSessionActivity";
    public static final String WORKOUT_PROGRAM_FOLDER_PATH_KEY = "com.main.fitness.ui.activities.WorkoutSessionActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY";

    private TextView timer;
    private TextView exerciseName;

    private AssetsViewModel assetsViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session);
        this.timer = findViewById(R.id.WorkoutSessionTimer);
        this.exerciseName = findViewById(R.id.WorkoutSessionExerciseName);

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

        this.assetsViewModel.getWorkoutProgram(workoutProgramPath).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()){
                Toast.makeText(this, "Cannot load session!", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.i(TAG, "can get workout program");

        });


        this.assetsViewModel.getTrainingSession(workoutProgramPath, 0).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()){
                Toast.makeText(this, "Cannot load session!", Toast.LENGTH_SHORT).show();
                return;
            }

            TrainingSession trainingSession = task.getResult();
            this.exerciseName.setText(trainingSession.getCurrentExercise().getExercise());
        });

        CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timer.setText("done!");
            }
        }.start();

        countDownTimer.cancel();

    }
}