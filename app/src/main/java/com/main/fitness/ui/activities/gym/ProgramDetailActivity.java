package com.main.fitness.ui.activities.gym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutProgram;
import com.main.fitness.data.Model.UserLevel;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.data.ViewModel.ProgramViewModel;
import com.main.fitness.data.ViewModel.UserViewModel;

import java.util.List;

public class ProgramDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProgramDetailActivity";
    public static final String WORKOUT_PROGRAM_FOLDER_PATH_KEY = "workout_program_folder_path_key";
    private ProgramViewModel programViewModel;
    private AssetsViewModel assetsViewModel;
    private UserViewModel userViewModel;

    private TextView programName, programGoal,
            programDuration, programDaysPerWeek,
            programLevels, programOverview;

    private Button button;

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
        this.button = findViewById(R.id.programDetailTrainButton);

        this.programViewModel = new ViewModelProvider(this).get(ProgramViewModel.class);
        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        if (getIntent() != null){
            String path = getIntent().getStringExtra(WORKOUT_PROGRAM_FOLDER_PATH_KEY);
            Log.i(TAG, path);
            this.assetsViewModel.getWorkoutProgram(path).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful()){
                    Toast.makeText(this, "Cannot get program!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                WorkoutProgram w = task.getResult();
                setUpTextViews(w);
                setUpButton(false, w);
            });
        }
//        else {
//            WorkoutProgram workoutProgram = this.programViewModel.getCurrentWorkoutProgram();
//            if (workoutProgram != null){
//                setUpTextViews(workoutProgram);
//                setUpButton(false, workoutProgram);
//            }
//        }

        if (this.userViewModel.getFirebaseUser() != null){
            this.button.setVisibility(View.VISIBLE);
        }
    }

    private void setUpButton(boolean userHasRegistered, WorkoutProgram workoutProgram){
        if (this.userViewModel.getFirebaseUser() == null){
            return;
        }

        String userId = this.userViewModel.getFirebaseUser().getUid();
        if (userHasRegistered){
            this.button.setText("Unregister");
            this.button.setOnClickListener(v -> {
                this.programViewModel.unregisterProgram(userId, workoutProgram.getName());
            });
        }
        else{
            this.button.setText("Train");
            this.button.setOnClickListener(v -> {
                this.programViewModel.registerProgram(userId, workoutProgram.getName());
            });
        }
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
        List<UserLevel> userLevelList = workoutProgram.getLevels();
        for (int i = 0; i < userLevelList.size() ; i++){
            // this is just to capitalize the first letter of user level
            // so instead of "BEGINNER", we have "Beginner"
            String level = userLevelList.get(i).getLevel().substring(0, 1).toUpperCase()
                    + userLevelList.get(i).getLevel().substring(1).toLowerCase();
            levelsStringBuilder.append(level);

            // don't add the " - " at the end
            if (i != userLevelList.size() - 1){
                levelsStringBuilder.append(" - ");
            }
        }
        this.programLevels.setText(levelsStringBuilder.toString());
        this.programOverview.setText(workoutProgram.getOverview());

        this.button.setOnClickListener(v -> {
            if (this.userViewModel.getFirebaseUser() == null){
                Toast.makeText(this, "Please sign in!", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = this.userViewModel.getFirebaseUser().getUid();
            this.programViewModel.registerProgram(userId, workoutProgram.getName());
        });
    }


}