package com.main.fitness.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Tasks;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutProgram;
import com.main.fitness.data.Model.WorkoutProgramLevel;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.data.ViewModel.UserViewModel;
import com.main.fitness.data.ViewModel.WorkoutRegistrationViewModel;

import java.util.List;

public class ProgramDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProgramDetailActivity";
    public static final String WORKOUT_PROGRAM_FOLDER_PATH_KEY = "workout_program_folder_path_key";
    private WorkoutRegistrationViewModel workoutRegistrationViewModel;
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
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

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

        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        this.workoutRegistrationViewModel = new ViewModelProvider(this).get(WorkoutRegistrationViewModel.class);

        this.backButton.setOnClickListener(v -> finish());



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
                setUpButton();
            });
        }
        if (this.userViewModel.getFirebaseUser() != null){
            this.trainButton.setVisibility(View.VISIBLE);
        }
    }

    private void startWorkoutSessionActivity(){
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra(ScheduleActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY, w.getFolderPath());
        startActivity(intent);
    }

    private void showDialogProceedWithoutCurrentSessionInfo(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Network problem!")
                .setMessage("Still want to see the workout?")
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Yes", (dialog, which) -> {
                    startWorkoutSessionActivity();
                    dialog.dismiss();
                }).show();
    }
    private void registerAndStartWorkoutSession(){
        if (this.userViewModel.getFirebaseUser() == null){
            return;
        }
        String uid = this.userViewModel.getFirebaseUser().getUid();
        this.workoutRegistrationViewModel.registerProgram(uid, w.getName(), 0, 0).addOnCompleteListener(this, registerTask -> {
            if (registerTask.isSuccessful()){
                startWorkoutSessionActivity();
            }
            else{
                showDialogProceedWithoutCurrentSessionInfo();
            }
        });
    }

    private void setUpButton(){
        String uid;
        if (this.userViewModel.getFirebaseUser() == null){
            return;
        }
        uid = this.userViewModel.getFirebaseUser().getUid();

        this.workoutRegistrationViewModel.getCurrentProgramName(uid).addOnCompleteListener(this, task -> {
            // user hasn't registered for any programs
            if (!task.isSuccessful()){
               this.trainButton.setText("Start");
               this.trainButton.setOnClickListener(v -> {
                   registerAndStartWorkoutSession();
               });
               return;
           }

           String programName = task.getResult();
           if (programName.equals(w.getName())){
               this.trainButton.setText("Resume");
//               Drawable img = getResources().getDrawable(R.drawable.button_4, getTheme());
               Drawable img = ContextCompat.getDrawable(this, R.drawable.button_4);
               this.trainButton.setBackground(img);
               this.trainButton.setOnClickListener(v -> {
                   startWorkoutSessionActivity();
               });
           }
           else{
               this.trainButton.setText("Start");
               this.trainButton.setOnClickListener(v -> {
                   new MaterialAlertDialogBuilder(this)
                       .setTitle("Cancel current workout program?")
                       .setMessage("You are currently following \"" + programName + "\" program. " +
                               "Do you want to cancel it and start following \"" + w.getName() + "\" instead?")
                       .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                       .setPositiveButton("Yes", (dialog, which) -> {
                           dialog.dismiss();
                           registerAndStartWorkoutSession();
                       })
                       .show();
               });
           }
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