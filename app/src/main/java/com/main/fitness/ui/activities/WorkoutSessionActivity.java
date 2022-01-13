package com.main.fitness.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    public static final String SESSION_WEEK = "com.main.fitness.ui.activities.WorkoutSessionActivity.SESSION_WEEK"; // week that the session belongs to
    public static final String CURRENT_WEEK_KEY = "com.main.fitness.ui.activities.WorkoutSessionActivity.CURRENT_WEEK_KEY"; // week that the user is current on
    public static final String CURRENT_DAY_KEY = "com.main.fitness.ui.activities.WorkoutSessionActivity.CURRENT_DAY_KEY"; // day that the user is current on

    private AssetsViewModel assetsViewModel;
    private WorkoutRegistrationViewModel workoutRegistrationViewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private OnSessionFinishLister mListener;

    private void reload(int week, int day){
        Intent intent = getIntent();
        intent.putExtra(CURRENT_DAY_KEY, day);
        intent.putExtra(CURRENT_WEEK_KEY, week);
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
        int currentWeek = intent.getIntExtra(CURRENT_WEEK_KEY, -1);
        int sessionWeek = intent.getIntExtra(SESSION_WEEK, -1);

        if (TextUtils.isEmpty(workoutProgramPath) || sessionWeek == -1) {
            finish();
            return;
        }

        this.workoutRegistrationViewModel = new ViewModelProvider(this).get(WorkoutRegistrationViewModel.class);
        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.assetsViewModel.getWorkoutSchedule(workoutProgramPath, sessionWeek, day == -1 ? 0 : day).addOnCompleteListener(this, task -> {
           if (!task.isSuccessful()){
               Toast.makeText(this, "Cannot get workout schedule", Toast.LENGTH_SHORT).show();
               return;
           }

           WorkoutSchedule schedule = task.getResult();

           String workoutProgramName = schedule.getWorkoutProgramName();
            List<WorkoutSessionFragment> fragments = new ArrayList<>();

            for (int _day = 0; _day < schedule.getSchedule()[currentWeek].length; _day++){
                boolean isCurrentSession = _day == day &&  sessionWeek == currentWeek;

                WorkoutSessionFragment fragment = WorkoutSessionFragment.newInstance(workoutProgramName, workoutProgramPath, sessionWeek, _day, isCurrentSession);

                fragment.setOnFinishSessionListener((mWeek, mDay) -> {
                    int newWeek = mWeek;
                    int newDay = mDay;

                    // the last session of the workout program
                    if (mDay == schedule.getSchedule()[mWeek].length - 1
                            && mWeek == schedule.getSchedule().length - 1) {
                        newDay = 0;
                        newWeek = 0;
                    }

                    // the last session of a week
                    else if (mDay == schedule.getSchedule()[mWeek].length - 1 ){
                        newDay = 0;
                        newWeek++;
                    }
                    else{
                        newDay++;
                    }

                    int finalNewDay = newDay;
                    int finalNewWeek = newWeek;
                    workoutRegistrationViewModel.updateCurrentSession(finalNewWeek, finalNewDay).addOnCompleteListener(this, updateTask -> {
                        if (!updateTask.isSuccessful()){
                            Toast.makeText(this, "Cannot record data!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Session Finished")
                                .setMessage("Well Done! You have finished today's session!")
                                .setCancelable(false)
                                .setPositiveButton("Back To Home Screen", (dialog, which) -> {
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra(ScheduleActivity.CURRENT_DAY_KEY, finalNewDay);
                                    resultIntent.putExtra(ScheduleActivity.CURRENT_WEEK_KEY, finalNewWeek);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    dialog.dismiss();
                                    finish();
                                })
                                .setNeutralButton("Cancel", (dialog, which) -> {
                                    reload(finalNewWeek, finalNewDay);
                                    dialog.dismiss();
                                })
                                .show();
                    });


                });

                fragment.setOnSkipToSessionListener((mWeek, mDay) -> {
                    workoutRegistrationViewModel.updateCurrentSession(mWeek, mDay).addOnCompleteListener(this, updateTask -> {
                        if (!updateTask.isSuccessful()){
                            Toast.makeText(this, "Cannot skip!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        reload(mWeek, mDay);
                    });

                });

                fragments.add(fragment);
            }
            ViewPagerAdapterForFragments<WorkoutSessionFragment> viewPagerAdapterForFragments = new ViewPagerAdapterForFragments<>(this, fragments);
            this.viewPager2.setAdapter(viewPagerAdapterForFragments);
            TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(this.tabLayout, this.viewPager2, (tab, position) -> {
                if (position == day && sessionWeek == currentWeek){
                    tab.view.setBackgroundColor(getResources().getColor(R.color.green_main, getTheme()));
                }
                tab.setText("Day " + (position + 1));

            });
            tabLayoutMediator.attach();

            if (day != -1){
                tabLayout.selectTab(tabLayout.getTabAt(day));
            }
            else{
                Log.e(TAG, "Cannot scroll!");
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
    public void setOnSessionFinishListener(OnSessionFinishLister listener){
        this.mListener = listener;
    }

    public interface OnSessionFinishLister{
        public void onSessionFinish(int week, int day);
    }
}