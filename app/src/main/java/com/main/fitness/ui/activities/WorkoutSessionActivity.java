package com.main.fitness.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutSchedule;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.ui.adapters.ViewPagerAdapterForFragments;
import com.main.fitness.ui.fragments.WorkoutSessionFragment;

import java.util.ArrayList;
import java.util.List;

public class WorkoutSessionActivity extends AppCompatActivity {
    private static final String TAG = "WorkoutSessionActivity";
    public static final String WORKOUT_PROGRAM_FOLDER_PATH_KEY = "com.main.fitness.ui.activities.WorkoutSessionActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY";

    private AssetsViewModel assetsViewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session);

        this.tabLayout = findViewById(R.id.WorkoutSessionTabLayout);
        this.viewPager2 = findViewById(R.id.WorkoutSessionViewPager);


        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        String workoutProgramPath = intent.getStringExtra(WORKOUT_PROGRAM_FOLDER_PATH_KEY);

        if (TextUtils.isEmpty(workoutProgramPath)) {
            finish();
            return;
        }

        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);

        // TODO: get current day in workout program, create SQL lite to cache user workout info
        // int day = get....
        int day = 0;



        this.assetsViewModel.getWorkoutSchedule(workoutProgramPath, day).addOnCompleteListener(this, task -> {
           if (!task.isSuccessful()){
               Toast.makeText(this, "Cannot get workout schedule", Toast.LENGTH_SHORT).show();
               return;
           }

           WorkoutSchedule schedule = task.getResult();

           String workoutProgramName = schedule.getWorkoutProgramName();
            List<WorkoutSessionFragment> fragments = new ArrayList<>();
            Log.e(TAG, "Length" + schedule.getSchedule().length);

            for (int i = 0; i < schedule.getSchedule().length; i++){
                WorkoutSessionFragment fragment = WorkoutSessionFragment.newInstance(workoutProgramName, workoutProgramPath, i);
                fragments.add(fragment);
            }

            ViewPagerAdapterForFragments<WorkoutSessionFragment> viewPagerAdapterForFragments = new ViewPagerAdapterForFragments<>(this, fragments);
            this.viewPager2.setAdapter(viewPagerAdapterForFragments);
            TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(this.tabLayout, this.viewPager2, (tab, position) -> {
                if (position == day){
                    tab.view.setBackgroundColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                }
                tab.setText("Day " + (position + 1));

            });
            tabLayoutMediator.detach();
            tabLayoutMediator.attach();

        });



    }
}