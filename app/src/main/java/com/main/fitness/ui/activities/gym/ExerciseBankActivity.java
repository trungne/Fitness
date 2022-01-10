package com.main.fitness.ui.activities.gym;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutExercise;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.ui.adapters.ExerciseAdapter;
import com.main.fitness.ui.menu.CustomDropdownMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseBankActivity extends AppCompatActivity {
    private AssetsViewModel assetsViewModel;


    private CustomDropdownMenu autoCompleteTextView;
    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private ImageButton backButton;

    // TODO: add menu to have back button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_bank);
        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.autoCompleteTextView = findViewById(R.id.WeightExerciseAutoCompleteTextView);
        this.autoCompleteTextView.setInputType(InputType.TYPE_NULL);
        this.backButton = findViewById(R.id.fragmentWeightExerciseBackButton);

        this.exerciseAdapter = new ExerciseAdapter(new ArrayList<>());
        this.exerciseAdapter.setOnExerciseClickListener(folderPath -> {
            Intent intent = new Intent(this, ExerciseDetailActivity.class);
            intent.putExtra(ExerciseDetailActivity.PATH_KEY, folderPath);
            startActivity(intent);
        });

        this.recyclerView = findViewById(R.id.WeightExerciseRecycleView);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(this.exerciseAdapter);

        this.backButton.setOnClickListener(v -> {
            finish();
        });

        loadOptionsForBodyParts();

    }

    private void loadOptionsForBodyParts(){
        String[] folders = this.assetsViewModel.getExerciseTypes();
        if (folders == null){
            // handle cases when no folders are found
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.option_exercise_body_part, Arrays.asList(folders));
        this.autoCompleteTextView.setAdapter(adapter);
        this.autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String exerciseType = (String) parent.getItemAtPosition(position);
            List<WorkoutExercise> workoutExerciseList = this.assetsViewModel.getExercises(exerciseType);
            this.exerciseAdapter.updateExerciseList(workoutExerciseList);
            this.exerciseAdapter.notifyDataSetChanged();
        });

        this.autoCompleteTextView.setSelection(0);
    }



}