package com.main.fitness.ui.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutSchedule;
import com.main.fitness.data.ViewModel.AssetsViewModel;

public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";
    public static final String WORKOUT_PROGRAM_FOLDER_PATH_KEY = "ScheduleActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY";
    public static final String CURRENT_DAY_KEY = "ScheduleActivity.CURRENT_DAY_KEY";
    public static final String CURRENT_WEEK_KEY = "ScheduleActivity.CURRENT_WEEK_KEY";

    private static final LinearLayout.LayoutParams LAYOUT_PARAMS =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f);


    private AssetsViewModel assetsViewModel;

    private LinearLayout weekLinearLayout;
    private Button goToCurrentSessionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        this.weekLinearLayout = findViewById(R.id.ScheduleActivityLinearLayout);
        this.goToCurrentSessionButton = findViewById(R.id.ScheduleActivityGoToCurrentButton);


        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        String workoutProgramPath = intent.getStringExtra(WORKOUT_PROGRAM_FOLDER_PATH_KEY);
        int currentDay = intent.getIntExtra(CURRENT_DAY_KEY, -1);
        int currentWeek = intent.getIntExtra(CURRENT_WEEK_KEY, -1);

        if (TextUtils.isEmpty(workoutProgramPath)) {
            finish();
            return;
        }

        // no day and week data, cannot go to current session
        if (currentDay == -1 || currentWeek == -1){
            this.goToCurrentSessionButton.setVisibility(View.GONE);
        }
        else{
            this.goToCurrentSessionButton.setOnClickListener(v -> {
                startWorkoutSession(workoutProgramPath, currentWeek, currentWeek, currentDay);
            });
        }

        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.assetsViewModel.getWorkoutSchedule(workoutProgramPath,
                currentWeek == -1 ? 0 : currentWeek,
                currentDay == -1 ? 0 : currentDay)
                .addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()){
                Toast.makeText(this, "Cannot get workout schedule", Toast.LENGTH_LONG).show();
                return;
            }

            WorkoutSchedule schedule = task.getResult();
            int weekNum = schedule.getSchedule().length;

            if (weekNum == 1){
                startWorkoutSession(workoutProgramPath, 0,0,  currentDay == -1 ? 0 : currentDay);
                finish();
                return;
            }


            for (int week = 0; week < weekNum; week++){
                Button weekButton = new Button(this);
                String text = "Week " + (week + 1);
                weekButton.setText(text);
                weekButton.setTextColor(getResources().getColor(R.color.white, getTheme()));
                weekButton.setLayoutParams(LAYOUT_PARAMS);
                weekButton.setTextSize(40);
                weekButton.setBackgroundResource(R.drawable.button_5);

                weekButton.setGravity(Gravity.CENTER);

                int sessionWeek = week;

                weekButton.setOnClickListener(v -> {
                    Log.e(TAG + "sessionWeek ", String.valueOf(sessionWeek));
                    startWorkoutSession(workoutProgramPath, sessionWeek, currentWeek == -1 ? 0 : currentWeek, currentDay);
                });

                if (week == currentWeek){
                    weekButton.setBackgroundResource(R.drawable.button_6);
                }

                this.weekLinearLayout.addView(weekButton);
            }
            this.weekLinearLayout.requestLayout();
        });
    }

    private void startWorkoutSession(String workoutProgramPath, int sessionWeek, int currentWeek, int day){
        Intent workoutSessionIntent = new Intent(this, WorkoutSessionActivity.class);
        workoutSessionIntent.putExtra(WorkoutSessionActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY, workoutProgramPath);
        workoutSessionIntent.putExtra(WorkoutSessionActivity.CURRENT_DAY_KEY, day);
        workoutSessionIntent.putExtra(WorkoutSessionActivity.SESSION_WEEK, sessionWeek);
        workoutSessionIntent.putExtra(WorkoutSessionActivity.CURRENT_WEEK_KEY, currentWeek);
        WorkoutSessionActivityResultLauncher.launch(workoutSessionIntent);
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    private ActivityResultLauncher<Intent> WorkoutSessionActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultIntent = result.getData();
                        if (resultIntent == null){
                            return;
                        }
                        Intent originalIntent = getIntent();
                        originalIntent.putExtra(CURRENT_DAY_KEY, resultIntent.getIntExtra(CURRENT_DAY_KEY, -1));
                        originalIntent.putExtra(CURRENT_WEEK_KEY, resultIntent.getIntExtra(CURRENT_WEEK_KEY, -1));
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(originalIntent);
                        overridePendingTransition(0, 0);
                        // There are no request codes
                    }
                }
            });
}