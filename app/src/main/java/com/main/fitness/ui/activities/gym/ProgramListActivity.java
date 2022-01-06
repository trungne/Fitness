package com.main.fitness.ui.activities.gym;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutProgram;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.ui.adapters.WorkoutProgramAdapter;

import java.util.List;

public class ProgramListActivity extends AppCompatActivity {
    private static final String TAG = "ProgramListActivity";
    // this key is used to indicate whether user has selected to view strength or cardio programs
    public static final String PROGRAMS_KEY = "com.main.fitness.ui.activities.gym.ProgramListActivity.programKey";

    public static final String STRENGTH_PROGRAMS = "STRENGTH_PROGRAMS";
    public static final String CARDIO_PROGRAMS = "CARDIO_PROGRAMS";

    private RecyclerView programListRecycleView;
    private AssetsViewModel assetsViewModel;
    private ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_list);
        this.programListRecycleView = findViewById(R.id.ProgramListRecycleView);
        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.backButton = findViewById(R.id.activityProgramListBackButton);
        //Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent == null || TextUtils.isEmpty(intent.getStringExtra(PROGRAMS_KEY))){
            finish();
            return;
        }
        String type;

        if (intent.getStringExtra(PROGRAMS_KEY).equals(CARDIO_PROGRAMS)){
            type = "cardio";
        }
        else if (intent.getStringExtra(PROGRAMS_KEY).equals(STRENGTH_PROGRAMS)){
            type = "strength";
        }
        else{
            finish();
            return;
        }

        this.assetsViewModel.getWorkoutPrograms(type).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()){
                Toast.makeText(this, "Cannot load programs", Toast.LENGTH_SHORT).show();
                return;
            }

            List<WorkoutProgram> workoutProgramList = task.getResult();
            this.programListRecycleView = findViewById(R.id.ProgramListRecycleView);
            this.programListRecycleView.setLayoutManager(new LinearLayoutManager(this));
            WorkoutProgramAdapter adapter = new WorkoutProgramAdapter(workoutProgramList);
            this.programListRecycleView.setAdapter(adapter);
            adapter.setOnViewClickListener(folderPath -> {
                this.assetsViewModel.getWorkoutProgram(folderPath).addOnCompleteListener(this, task1 -> {
                    if (!task1.isSuccessful()){
                        return;
                    }

                    Intent i = new Intent(this, ProgramDetailActivity.class);
                    i.putExtra(ProgramDetailActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY, task1.getResult().getFolderPath());
                    startActivity(i);
                });
            });
        });
    }





}