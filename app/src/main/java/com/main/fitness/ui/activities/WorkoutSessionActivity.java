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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.main.fitness.R;
import com.main.fitness.data.Model.WeekAndDay;
import com.main.fitness.data.Model.WorkoutSchedule;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.data.ViewModel.WorkoutRegistrationViewModel;
import com.main.fitness.ui.adapters.ViewPagerAdapterForFragments;
import com.main.fitness.ui.fragments.WorkoutSessionFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkoutSessionActivity extends AppCompatActivity {
    private static final String TAG = "WorkoutSessionActivity";
    public static final String WORKOUT_PROGRAM_FOLDER_PATH_KEY = "com.main.fitness.ui.activities.WorkoutSessionActivity.WORKOUT_PROGRAM_FOLDER_PATH_KEY";
    public static final String SESSION_WEEK = "com.main.fitness.ui.activities.WorkoutSessionActivity.SESSION_WEEK"; // week that the session belongs to

    private AssetsViewModel assetsViewModel;
    private WorkoutRegistrationViewModel workoutRegistrationViewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    private void reload(){
        Intent intent = getIntent();
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
        int sessionWeek = intent.getIntExtra(SESSION_WEEK, -1);

        if (TextUtils.isEmpty(workoutProgramPath) || sessionWeek == -1) {
            finish();
            return;
        }

        this.workoutRegistrationViewModel = new ViewModelProvider(this).get(WorkoutRegistrationViewModel.class);
        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);


        String uid = FirebaseAuth.getInstance().getUid();
        AtomicInteger day = new AtomicInteger();
        AtomicInteger week = new AtomicInteger();

        day.set(-1);
        week.set(-1);

        this.workoutRegistrationViewModel.getCurrentWeekAndDayOfWorkoutProgram(uid)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()){
                        Toast.makeText(this, "Cannot get your progress", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        day.set(task.getResult().getDay());
                        week.set(task.getResult().getWeek());
                        Log.e(TAG, "Week: " + week.get());
                    }


                    return this.assetsViewModel.getWorkoutSchedule(workoutProgramPath);
                })

                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()){
                        finish();
                        return;
                    }

                    WorkoutSchedule schedule = task.getResult();
                    String workoutProgramName = schedule.getWorkoutProgramName();
                    List<WorkoutSessionFragment> fragments = new ArrayList<>();
                    for(int i = 0; i < schedule.getSchedule().length; i++){
                        System.out.println("week: " + (i + 1));
                        for(int j = 0; j < schedule.getSchedule()[i].length; j++){
                            System.out.println("Day " + (j+1) + ": " + Arrays.toString(schedule.getSchedule()[i][j].getTargetMuscles()));
                        }
                    }
                    for (int _day = 0; _day < schedule.getSchedule()[sessionWeek].length; _day++){
                        Log.e(TAG, "Create fragments number: " + _day);
                        boolean isCurrentSession = _day == day.get() &&  sessionWeek == week.get();

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

                            workoutRegistrationViewModel.updateCurrentSession(newWeek, newDay).addOnCompleteListener(this, updateTask -> {
                                if (!updateTask.isSuccessful()){
                                    Toast.makeText(this, "Cannot record data!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                new MaterialAlertDialogBuilder(this)
                                        .setTitle("Session Finished")
                                        .setMessage("Well Done! You have finished today's session!")
                                        .setCancelable(false)
                                        .setPositiveButton("Back To Home Screen", (dialog, which) -> {
                                            setResult(Activity.RESULT_OK);
                                            dialog.dismiss();
                                            finish();
                                        })
                                        .setNeutralButton("Cancel", (dialog, which) -> {
                                            dialog.dismiss();
                                            reload();
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
                                reload();
                            });

                        });

                        fragments.add(fragment);
                        Log.e(TAG, "========");
                    }

                    ViewPagerAdapterForFragments<WorkoutSessionFragment> viewPagerAdapterForFragments = new ViewPagerAdapterForFragments<>(this, fragments);
                    this.viewPager2.setAdapter(viewPagerAdapterForFragments);
                    TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(this.tabLayout, this.viewPager2, (tab, position) -> {
                        if (position == day.get() && sessionWeek == week.get()){
                            tab.view.setBackgroundColor(getResources().getColor(R.color.green_main, getTheme()));
                        }
                        tab.setText("Day " + (position + 1));

                    });
                    tabLayoutMediator.attach();

                    if (day.get() != -1){
                        tabLayout.selectTab(tabLayout.getTabAt(day.get()));
                    }
                    else{
                        Log.e(TAG, "Cannot scroll!");
                    }

                });
    }
}