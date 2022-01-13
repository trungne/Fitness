package com.main.fitness.ui.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutSchedule;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.data.ViewModel.WorkoutRegistrationViewModel;

import java.util.concurrent.atomic.AtomicInteger;

public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";
    public static final String WORKOUT_PROGRAM_FOLDER_PATH_KEY = "ScheduleActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY";

    private static final LinearLayout.LayoutParams LAYOUT_PARAMS =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f);


    private AssetsViewModel assetsViewModel;
    private WorkoutRegistrationViewModel workoutRegistrationViewModel;

    private LinearLayout weekLinearLayout;
    private Button goToCurrentSessionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);


        Log.e(TAG, "onCreated called");
        this.weekLinearLayout = findViewById(R.id.ScheduleActivityLinearLayout);
        this.goToCurrentSessionButton = findViewById(R.id.ScheduleActivityGoToCurrentButton);

        Intent intent = getIntent();
        String workoutProgramPath = intent.getStringExtra(WORKOUT_PROGRAM_FOLDER_PATH_KEY);
        if (TextUtils.isEmpty(workoutProgramPath)) {
            finish();
            return;
        }

        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);

        this.workoutRegistrationViewModel = new ViewModelProvider(this).get(WorkoutRegistrationViewModel.class);
        String uid = FirebaseAuth.getInstance().getUid();
        AtomicInteger day = new AtomicInteger();
        AtomicInteger week = new AtomicInteger();

        day.set(-1);
        week.set(-1);

        this.workoutRegistrationViewModel.getCurrentWeekAndDayOfWorkoutProgram(uid).continueWithTask(task -> {

            if (!task.isSuccessful()){
                Toast.makeText(this, "Cannot load your progress", Toast.LENGTH_SHORT).show();
            }
            else{
                day.set(task.getResult().getDay());
                week.set(task.getResult().getWeek());

                this.goToCurrentSessionButton.setOnClickListener(v -> startWorkoutSession(workoutProgramPath, week.get()));
            }

            return this.assetsViewModel.getWorkoutSchedule(workoutProgramPath);
        }).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()){
                Toast.makeText(this, "Cannot get workout schedule", Toast.LENGTH_LONG).show();
                return;
            }

            WorkoutSchedule schedule = task.getResult();
            int totalNumberOfWeek = schedule.getSchedule().length;

            if (totalNumberOfWeek == 1){
                startWorkoutSession(workoutProgramPath, 0);
                finish();
                return;
            }

            for (int _week = 0; _week < totalNumberOfWeek; _week++){
                Button weekButton = new Button(this);
                String text = "Week " + (_week + 1);
                weekButton.setText(text);
                weekButton.setTextColor(getResources().getColor(R.color.white, getTheme()));
                weekButton.setLayoutParams(LAYOUT_PARAMS);
                weekButton.setTextSize(40);
                weekButton.setBackgroundResource(R.drawable.button_5);

                weekButton.setGravity(Gravity.CENTER);

                int sessionWeek = _week; // copy to final so java stops complaining
                weekButton.setOnClickListener(v -> startWorkoutSession(workoutProgramPath, sessionWeek));

                if (_week == week.get()){
                    weekButton.setBackgroundResource(R.drawable.button_6);
                }

                this.weekLinearLayout.addView(weekButton);
            }
            this.weekLinearLayout.requestLayout();
        });

    }

    private void startWorkoutSession(String workoutProgramPath, int sessionWeek){
        Intent workoutSessionIntent = new Intent(this, WorkoutSessionActivity.class);
        workoutSessionIntent.putExtra(WorkoutSessionActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY, workoutProgramPath);
        workoutSessionIntent.putExtra(WorkoutSessionActivity.SESSION_WEEK, sessionWeek);
        WorkoutSessionActivityResultLauncher.launch(workoutSessionIntent);
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    private ActivityResultLauncher<Intent> WorkoutSessionActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // for some reason when Activity.Result_OK is sent back
                // this listener doesn't catch it
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            });


}