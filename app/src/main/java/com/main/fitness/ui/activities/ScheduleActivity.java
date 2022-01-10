package com.main.fitness.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
        int day = intent.getIntExtra(CURRENT_DAY_KEY, -1);
        int week = intent.getIntExtra(CURRENT_WEEK_KEY, -1);

        if (TextUtils.isEmpty(workoutProgramPath)) {
            finish();
            return;
        }

        // no day and week data, cannot go to current session
        if (day == -1 || week == -1){
            this.goToCurrentSessionButton.setVisibility(View.GONE);
        }
        else{
            this.goToCurrentSessionButton.setOnClickListener(v -> {
                startWorkoutSession(workoutProgramPath, week, day);
            });
        }

        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.assetsViewModel.getWorkoutSchedule(workoutProgramPath,
                week == -1 ? 0 : week,
                day == -1 ? 0 : day)
                .addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()){
                Toast.makeText(this, "Cannot get workout schedule", Toast.LENGTH_LONG).show();
                return;
            }

            WorkoutSchedule schedule = task.getResult();
            int weekNum = schedule.getSchedule().length;

            if (weekNum == 1){
                startWorkoutSession(workoutProgramPath, 0,  day == -1 ? 0 : day);
                finish();
                return;
            }


            for (int i = 0; i < weekNum; i++){
                Button weekButton = new Button(this);
                String text = "Week " + (i + 1);
                weekButton.setText(text);
                weekButton.setLayoutParams(LAYOUT_PARAMS);
                weekButton.setGravity(Gravity.CENTER);

                int _day = i;
                weekButton.setOnClickListener(v -> {
                    startWorkoutSession(workoutProgramPath, week == -1 ? 0 : week, _day);
                });

                if (i == week){
                    weekButton.setBackgroundColor(getResources().getColor(R.color.green_main, getTheme()));
                }

                this.weekLinearLayout.addView(weekButton);
            }
            this.weekLinearLayout.requestLayout();
        });
    }

    private void startWorkoutSession(String workoutProgramPath, int week, int day){
        Intent workoutSessionIntent = new Intent(this, WorkoutSessionActivity.class);
        workoutSessionIntent.putExtra(WorkoutSessionActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY, workoutProgramPath);
        workoutSessionIntent.putExtra(WorkoutSessionActivity.CURRENT_DAY_KEY, day);
        workoutSessionIntent.putExtra(WorkoutSessionActivity.CURRENT_WEEK_KEY, week);
        startActivity(workoutSessionIntent);
    }
}