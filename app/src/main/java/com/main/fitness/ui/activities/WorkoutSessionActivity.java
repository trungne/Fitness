package com.main.fitness.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutSchedule;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.data.ViewModel.WorkoutRegistrationViewModel;
import com.main.fitness.ui.adapters.ViewPagerAdapterForFragments;
import com.main.fitness.ui.fragments.WorkoutSessionFragment;

import java.util.ArrayList;
import java.util.List;

public class WorkoutSessionActivity extends AppCompatActivity {
    private static final String TAG = "WorkoutSessionActivity";
    public static final String WORKOUT_PROGRAM_FOLDER_PATH_KEY = "com.main.fitness.ui.activities.WorkoutSessionActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY";
    public static final String CURRENT_WEEK_KEY = "com.main.fitness.ui.activities.WorkoutSessionActivity.CURRENT_WEEK_KEY";
    public static final String CURRENT_DAY_KEY = "com.main.fitness.ui.activities.WorkoutSessionActivity.CURRENT_DAY_KEY";

    private AssetsViewModel assetsViewModel;
    private WorkoutRegistrationViewModel workoutRegistrationViewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    private void reload(int newSessionNumber){
        Intent intent = getIntent();
        intent.putExtra(CURRENT_DAY_KEY, newSessionNumber);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
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
        int day = intent.getIntExtra(CURRENT_DAY_KEY, -1);
        int week = intent.getIntExtra(CURRENT_WEEK_KEY, -1);
        Log.e(TAG + " Day: ", String.valueOf(day));
        Log.e(TAG + " Week: ", String.valueOf(week));

        if (TextUtils.isEmpty(workoutProgramPath)) {
            finish();
            return;
        }

        this.workoutRegistrationViewModel = new ViewModelProvider(this).get(WorkoutRegistrationViewModel.class);
        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.assetsViewModel.getWorkoutSchedule(workoutProgramPath, week == -1 ? 0 : week, day == -1 ? 0 : day).addOnCompleteListener(this, task -> {
           if (!task.isSuccessful()){
               Toast.makeText(this, "Cannot get workout schedule", Toast.LENGTH_SHORT).show();
               return;
           }

           WorkoutSchedule schedule = task.getResult();

           String workoutProgramName = schedule.getWorkoutProgramName();
            List<WorkoutSessionFragment> fragments = new ArrayList<>();

            for (int i = 0; i < schedule.getSchedule()[week].length; i++){
                boolean isCurrentSession = i == day;
                WorkoutSessionFragment fragment = WorkoutSessionFragment.newInstance(workoutProgramName, workoutProgramPath, week, i, isCurrentSession);

                fragment.setOnFinishSessionListener(sessionNumber -> {
                    int newSessionNumber = sessionNumber == schedule.getSchedule().length - 1 ? 0 : sessionNumber + 1;
                    workoutRegistrationViewModel.updateCurrentSession(newSessionNumber);
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Session Finished")
                            .setMessage("Well Done! You have finished today's session!")
                            .setPositiveButton("Back To Home Screen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setResult(RESULT_OK);
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .setNegativeButton("Next Session", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    reload(newSessionNumber);
                                }
                            })
                            .show();

                });

                fragment.setOnSkipToSessionListener(sessionNumber -> {
                    workoutRegistrationViewModel.updateCurrentSession(sessionNumber);
                    reload(sessionNumber);
                });

                fragments.add(fragment);
            }
            ViewPagerAdapterForFragments<WorkoutSessionFragment> viewPagerAdapterForFragments = new ViewPagerAdapterForFragments<>(this, fragments);
            this.viewPager2.setAdapter(viewPagerAdapterForFragments);
            TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(this.tabLayout, this.viewPager2, (tab, position) -> {
                if (position == day){
                    tab.view.setBackgroundColor(getResources().getColor(R.color.green_main, getTheme()));
                }
                tab.setText("Day " + (position + 1));

            });
            tabLayoutMediator.attach();

            if (day != -1){
                scrollToTabAfterLayout(day);
            }
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
                        if (observer.isAlive()){
                            observer.removeOnGlobalLayoutListener(this);
                            TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
                            if (tab != null){
                                tab.select();
                            }
                        }
                    }
                });
            }
    }
}