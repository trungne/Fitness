package com.main.fitness.ui.activities.gym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.main.fitness.R;

public class ExerciseDetailActivity extends AppCompatActivity {
    public static final String PATH_KEY = "com.main.fitness.ui.activities.gym.ExerciseDetailActivity.path";

    private String pathToExercise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);
        Intent intent = getIntent();
        if (intent == null || TextUtils.isEmpty(intent.getStringExtra(PATH_KEY))){
            finish();
            return;
        }

        this.pathToExercise = intent.getStringExtra(PATH_KEY);
    }
}