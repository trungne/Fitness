package com.main.fitness.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutProgram;
import com.main.fitness.data.Model.WorkoutProgramLevel;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.data.ViewModel.WorkoutRecordViewModel;
import com.main.fitness.data.ViewModel.UserViewModel;
import com.main.fitness.ui.activities.WorkoutSessionActivity;

import java.util.List;

public class ProgramDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProgramDetailActivity";
    public static final String WORKOUT_PROGRAM_FOLDER_PATH_KEY = "workout_program_folder_path_key";
    private WorkoutRecordViewModel workoutRecordViewModel;
    private AssetsViewModel assetsViewModel;
    private UserViewModel userViewModel;

    private TextView programName, programGoal,
            programDuration, programDaysPerWeek,
            programLevels, programOverview;

    private Button trainButton;
    private ImageButton backButton;

    private String path;

    private WorkoutProgram w;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_detail);

        this.programName = findViewById(R.id.programDetailName);
        this.programGoal = findViewById(R.id.programDetailGoal);
        this.programDuration = findViewById(R.id.programDetailDuration);
        this.programDaysPerWeek = findViewById(R.id.programDetailDaysPerWeek);
        this.programLevels = findViewById(R.id.programDetailLevels);
        this.programOverview = findViewById(R.id.programDetailOverview);
        this.trainButton = findViewById(R.id.programDetailTrainButton);
        this.backButton = findViewById(R.id.activityProgramDetailBackButton);

        this.workoutRecordViewModel = new ViewModelProvider(this).get(WorkoutRecordViewModel.class);
        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        backButton.setOnClickListener(v -> finish());



        if (getIntent() != null){
            this.path = getIntent().getStringExtra(WORKOUT_PROGRAM_FOLDER_PATH_KEY);
            this.assetsViewModel.getWorkoutProgram(path).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful()){
                    Toast.makeText(this, "Cannot get program!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                w = task.getResult();
                setUpTextViews(w);
                setUpButton(w);
            });
        }
        if (this.userViewModel.getFirebaseUser() != null){
            this.trainButton.setVisibility(View.VISIBLE);
        }
    }

    private void startWorkoutSessionActivity(int currentSession){
        Intent intent = new Intent(this, WorkoutSessionActivity.class);
        intent.putExtra(WorkoutSessionActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY, w.getFolderPath());
        intent.putExtra(WorkoutSessionActivity.CURRENT_SESSION_KEY, currentSession);
        startActivity(intent);
    }

    private void setUpButton(WorkoutProgram workoutProgram){
        if (this.userViewModel.getFirebaseUser() == null){
            return;
        }

        this.workoutRecordViewModel.getCurrentSessionIndexOfWorkoutProgram(workoutProgram.getName()).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()){
                this.trainButton.setText("Start Training");
                this.trainButton.setOnClickListener(v -> {
                    this.workoutRecordViewModel.registerProgram(workoutProgram.getName(), workoutProgram.getDaysPerWeek());
                    startWorkoutSessionActivity(0);
                });
                return;
            }
            this.trainButton.setText("Resume Training");
            this.trainButton.setOnClickListener(v -> {
                startWorkoutSessionActivity(task.getResult());
            });
        });
    }

    private void setUpTextViews(WorkoutProgram workoutProgram){
        this.programName.setText(workoutProgram.getName());
        this.programGoal.setText(workoutProgram.getGoal());

        Integer duration = workoutProgram.getDuration();
        String durationText = duration == 0 ? "Indefinite" : duration + " weeks";
        this.programDuration.setText(durationText);

        Integer daysPerWeek = workoutProgram.getDaysPerWeek();
        String daysPerWeekStr = daysPerWeek == 1 ? " day per week" : " days per week";
        String daysPerWeekText = daysPerWeek + daysPerWeekStr;
        this.programDaysPerWeek.setText(daysPerWeekText);

        StringBuilder levelsStringBuilder = new StringBuilder();
        List<WorkoutProgramLevel> workoutProgramLevelList = workoutProgram.getLevels();
        for (int i = 0; i < workoutProgramLevelList.size() ; i++){
            // this is just to capitalize the first letter of user level
            // so instead of "BEGINNER", we have "Beginner"
            String level = workoutProgramLevelList.get(i).getLevel().substring(0, 1).toUpperCase()
                    + workoutProgramLevelList.get(i).getLevel().substring(1).toLowerCase();
            levelsStringBuilder.append(level);

            // don't add the " - " at the end
            if (i != workoutProgramLevelList.size() - 1){
                levelsStringBuilder.append(" - ");
            }
        }
        this.programLevels.setText(levelsStringBuilder.toString());
        this.programOverview.setText(workoutProgram.getOverview());
    }


}