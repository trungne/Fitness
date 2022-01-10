package com.main.fitness.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewTreeObserver;
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
    public static final String CURRENT_SESSION_KEY = "com.main.fitness.ui.activities.WorkoutSessionActivity.CURRENT_SESSION_KEY";

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
        int day = intent.getIntExtra(CURRENT_SESSION_KEY, -1);

        if (TextUtils.isEmpty(workoutProgramPath) || day == -1) {
            finish();
            return;
        }

        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
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
                // TODO: add isCurrentSession boolean when create a fragment
                boolean isCurrentSession  = i == day;
                WorkoutSessionFragment fragment = WorkoutSessionFragment.newInstance(workoutProgramName, workoutProgramPath, i, isCurrentSession);
                fragments.add(fragment);
            }
            // TODO: if time allows, use view instead of fragment to reduce load on main thread
            ViewPagerAdapterForFragments<WorkoutSessionFragment> viewPagerAdapterForFragments = new ViewPagerAdapterForFragments<>(this, fragments);
            this.viewPager2.setAdapter(viewPagerAdapterForFragments);
            TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(this.tabLayout, this.viewPager2, (tab, position) -> {
                boolean select= false;

                if (position == day){

                    tab.view.setBackgroundColor(getResources().getColor(R.color.green_main, getTheme()));

                }
                tab.setText("Day " + (position + 1));

            });
            tabLayoutMediator.attach();
            scrollToTabAfterLayout(day);
        });
    }
    // http://developer.android.com/reference/android/view/ViewTreeObserver.html
    private void scrollToTabAfterLayout(final int tabIndex) {

            final ViewTreeObserver observer = tabLayout.getViewTreeObserver();

            if (observer.isAlive()) {
                observer.dispatchOnGlobalLayout(); // In case a previous call is waiting when this call is made
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        observer.removeOnGlobalLayoutListener(this);
                        TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
                        if (tab != null){
                            tab.select();
                        }
                    }
                });
            }
    }
}