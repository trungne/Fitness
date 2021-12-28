package com.main.fitness.ui.activities.gym;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.main.fitness.R;
import com.main.fitness.data.Model.Program;
import com.main.fitness.data.Model.UserLevel;
import com.main.fitness.data.ViewModel.ProgramViewModel;
import com.main.fitness.data.ViewModel.UserViewModel;

import java.util.List;

public class ProgramDetailActivity extends AppCompatActivity {
    public static final String PROGRAM_ID_KEY = "com.main.fitness.ui.activities.gym.ProgramDetailActivity.programID";
    private ProgramViewModel programViewModel;
    private UserViewModel userViewModel;

    private TextView programName, programGoal,
            programDuration, programDaysPerWeek,
            programLevels, programOverview;

    private Button button;

    private String programId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_detail);
        Intent intent = getIntent();
        if (intent == null || TextUtils.isEmpty(intent.getStringExtra(PROGRAM_ID_KEY))){
            finish();
            Toast.makeText(this, "Cannot load program!", Toast.LENGTH_SHORT).show();
            return;
        }

        this.programName = findViewById(R.id.programDetailName);
        this.programGoal = findViewById(R.id.programDetailGoal);
        this.programDuration = findViewById(R.id.programDetailDuration);
        this.programDaysPerWeek = findViewById(R.id.programDetailDaysPerWeek);
        this.programLevels = findViewById(R.id.programDetailLevels);
        this.programOverview = findViewById(R.id.programDetailOverview);
        this.button = findViewById(R.id.programDetailTrainButton);

        this.programViewModel = new ViewModelProvider(this).get(ProgramViewModel.class);
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        if (this.userViewModel.getFirebaseUser() != null){
            this.button.setVisibility(View.VISIBLE);
        }

        this.programId = intent.getStringExtra(PROGRAM_ID_KEY);
        this.programViewModel.getProgram(this.programId).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()){
                finish();
                Toast.makeText(this, "Cannot load program!", Toast.LENGTH_SHORT).show();
                return;
            }

            Program program = task.getResult();
            setUpTextViews(program);
        });

    }

    private void setUpButton(boolean userHasRegistered){
        if (this.userViewModel.getFirebaseUser() == null){
            return;
        }

        String userId = this.userViewModel.getFirebaseUser().getUid();
        if (userHasRegistered){
            this.button.setText("Unregister");
            this.button.setOnClickListener(v -> {
                this.programViewModel.unregisterProgram(userId, this.programId);
            });
        }
        else{
            this.button.setText("Train");
            this.button.setOnClickListener(v -> {
                this.programViewModel.registerProgram(userId, this.programId);
            });
        }
    }

    private void setUpTextViews(Program program){
        this.programName.setText(program.getName());
        this.programGoal.setText(program.getGoal());

        Integer duration = program.getDuration();
        String durationText = duration == 0 ? "Indefinite" : duration + " weeks";
        this.programDuration.setText(durationText);

        Integer daysPerWeek = program.getDaysPerWeek();
        String daysPerWeekStr = daysPerWeek == 1 ? " day per week" : " days per week";
        String daysPerWeekText = daysPerWeek + daysPerWeekStr;
        this.programDaysPerWeek.setText(daysPerWeekText);

        StringBuilder levelsStringBuilder = new StringBuilder();
        List<UserLevel> userLevelList = program.getLevels();
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
        this.programOverview.setText(program.getOverview());

        this.button.setOnClickListener(v -> {
            if (this.userViewModel.getFirebaseUser() == null){
                Toast.makeText(this, "Please sign in!", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = this.userViewModel.getFirebaseUser().getUid();
            this.programViewModel.registerProgram(userId, program.getId());
        });
    }


}