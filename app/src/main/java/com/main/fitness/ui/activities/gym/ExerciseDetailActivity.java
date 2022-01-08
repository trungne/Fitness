package com.main.fitness.ui.activities.gym;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutExercise;
import com.main.fitness.data.ViewModel.AssetsViewModel;

public class ExerciseDetailActivity extends AppCompatActivity {
    // TODO: add action bar
    public static final String PATH_KEY = "com.main.fitness.ui.activities.gym.ExerciseDetailActivity.path";

    private AssetsViewModel assetsViewModel;
    private WorkoutExercise workoutExercise;

    private TextView name, description;
    private ImageView illustration;
    private ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);
        Intent intent = getIntent();

        if (intent == null){
            finish();
            return;
        }

        String path = intent.getStringExtra(PATH_KEY);

        if (TextUtils.isEmpty(path)){
            finish();
            return;
        }

        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.workoutExercise = this.assetsViewModel.getExercise(path);
        this.backButton = findViewById(R.id.activityProgramListBackButton);
        this.backButton.setOnClickListener(v -> finish());

        if (this.workoutExercise == null){
            finish();
            return;
        }

        // init views
        this.name = findViewById(R.id.ExerciseDetailName);
        this.description = findViewById(R.id.ExerciseDetailDescription);
        this.illustration = findViewById(R.id.ExerciseDetailIllustration);

        this.name.setText(this.workoutExercise.getName());
        this.description.setText(this.workoutExercise.getDescription());
        this.illustration.setImageDrawable(this.workoutExercise.getIllustration());

        this.description.setMovementMethod(new ScrollingMovementMethod());
    }
}