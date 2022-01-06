package com.main.fitness.ui.activities.gym;

import androidx.annotation.NonNull;
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
import com.main.fitness.data.Model.Exercise;
import com.main.fitness.data.ViewModel.AssetsViewModel;

public class ExerciseDetailActivity extends AppCompatActivity {
    // TODO: add action bar
    public static final String PATH_KEY = "com.main.fitness.ui.activities.gym.ExerciseDetailActivity.path";

    private AssetsViewModel assetsViewModel;
    private Exercise exercise;
    private String path = "";

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
        this.path = intent.getStringExtra(PATH_KEY);
//
//        if (intent != null){
//            this.path = intent.getStringExtra(PATH_KEY);
//        }
//        else{
//            this.path = savedInstanceState.getString(PATH_KEY);
//        }

        if (TextUtils.isEmpty(path)){
            finish();
            return;
        }

        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.exercise = this.assetsViewModel.getExercise(this.path);
        this.backButton = findViewById(R.id.activityProgramListBackButton);
        //Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (this.exercise == null){
            finish();
            return;
        }

        // init views
        this.name = findViewById(R.id.ExerciseDetailName);
        this.description = findViewById(R.id.ExerciseDetailDescription);
        this.illustration = findViewById(R.id.ExerciseDetailIllustration);

        this.name.setText(this.exercise.getName());
        this.description.setText(this.exercise.getDescription());
        this.illustration.setImageDrawable(this.exercise.getIllustration());

        this.description.setMovementMethod(new ScrollingMovementMethod());
    }
//
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (!TextUtils.isEmpty(this.path)){
//            outState.putString(PATH_KEY, this.path);
//        }
//    }
}