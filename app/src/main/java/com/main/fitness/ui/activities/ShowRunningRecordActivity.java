package com.main.fitness.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.main.fitness.R;
import com.main.fitness.data.Model.RunningRecord;
import com.main.fitness.data.ViewModel.WorkoutRecordViewModel;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ShowRunningRecordActivity extends AppCompatActivity {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String TAG = "ShowRunningRecordActivity";
    public static final String START_TIME_KEY = "ShowRunningRecord.START_TIME_KEY";
    public static final String FINISH_TIME_KEY = "ShowRunningRecord.FINISH_TIME_KEY";
    public static final String DISTANCE_KEY = "ShowRunningRecord.DISTANCE_KEY";
    public static final String STEPS_KEY = "ShowRunningRecord.STEPS_KEY";

    private TextView startTimeView, finishTimeView, durationView, distanceView, stepsView;
    private Button cancelButton, saveButton;

    private WorkoutRecordViewModel workoutRecordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_running_record);

        this.workoutRecordViewModel = new ViewModelProvider(this).get(WorkoutRecordViewModel.class);

        // init views
        this.startTimeView = findViewById(R.id.ShowRunningRecordStartTimeValue);
        this.finishTimeView = findViewById(R.id.ShowRunningRecordFinishTimeValue);
        this.durationView = findViewById(R.id.ShowRunningRecordDurationValue);
        this.distanceView = findViewById(R.id.ShowRunningRecordDistanceValue);
        this.stepsView = findViewById(R.id.ShowRunningRecordStepsValue);

        this.cancelButton = findViewById(R.id.ShowRunningRecordCancelButton);
        this.saveButton = findViewById(R.id.ShowRunningRecordSaveButton);


        Intent intent = getIntent();

        // get data from intent
        String startTime = intent.getStringExtra(START_TIME_KEY);
        String finishTime = intent.getStringExtra(FINISH_TIME_KEY);
        float distance = intent.getFloatExtra(DISTANCE_KEY, -1);
        int steps = intent.getIntExtra(STEPS_KEY, 0);

        String uid = FirebaseAuth.getInstance().getUid();

        // check if the intent contains enough data, steps can be 0 or -1 due to some phones don't have sensors
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(finishTime) || distance == -1){
            finish();
            return;
        }

        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime finish = LocalDateTime.parse(finishTime);

        long seconds = ChronoUnit.SECONDS.between(start, finish) % 60;

        // only get the minutes
        long minutes = ChronoUnit.MINUTES.between(start, finish) % 60;
        // no need for modular % 24, I don't think anyone is gonna use this app for more than 24 hours
        // if they did, let them crash the app, they deserve it
        long hours = ChronoUnit.HOURS.between(start, finish);


        String secondsText = seconds + (seconds == 1 ? " Second" : " Seconds");

        String hoursText = "";
        if (hours >= 1){
            hoursText = hours + (hours == 1 ? " Hour " : " Hours ");
        }

        String minutesText = "";
        if (minutes >= 1){
            minutesText = minutes + (minutes == 1 ? " Minute " : " Minutes ");
        }

        String durationText = hoursText + minutesText + secondsText;

        String startTimeText = start.format(DATE_TIME_FORMATTER);
        this.startTimeView.setText(startTimeText);

        String finishTimeText = finish.format(DATE_TIME_FORMATTER);
        this.finishTimeView.setText(finishTimeText);

        String distanceText = DECIMAL_FORMAT.format(distance);
        this.distanceView.setText(distanceText+ " m");

        if (steps > 0){
            this.stepsView.setText(String.valueOf(steps));
        }

        this.durationView.setText(durationText);


        RunningRecord r = new RunningRecord(uid, startTime, finishTime, distance, steps);
        this.saveButton.setOnClickListener(v -> {
            saveRunningRecord(r);
        });

        this.cancelButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Cancel the session")
                    .setMessage("Your data will be lost. Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    })
                    .setNeutralButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }


    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cancel the session")
                .setMessage("You haven't saved your data. Do you want to go back?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed();
                    dialog.dismiss();
                    finish();

                })
                .setNeutralButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();


    }

    private void saveRunningRecord(RunningRecord r){
        this.workoutRecordViewModel.updateRunningRecord(r).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                new MaterialAlertDialogBuilder(this)
                        .setMessage("Failed to save the session! Check your internet connection.")
                        .setPositiveButton("Try again", (dialog, which) -> {
                            saveRunningRecord(r);
                            dialog.dismiss();
                        })
                        .setNeutralButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
                Toast.makeText(this, "Failed to save the session!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}