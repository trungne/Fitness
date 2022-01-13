package com.main.fitness.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.main.fitness.R;
import com.main.fitness.data.Model.RunningRecord;
import com.main.fitness.data.ViewModel.WorkoutRecordViewModel;
import com.main.fitness.ui.adapters.RecordListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyRunRecordsActivity extends AppCompatActivity {


    private ImageButton backButton;
    private List<RunningRecord> runningRecords;
    private ListView listView;
    private FirebaseFirestore firebaseFirestore;
    private WorkoutRecordViewModel workoutRecordViewModel = new WorkoutRecordViewModel(getApplication());
    private RecordListAdapter recordListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_run_records);

        backButton = findViewById(R.id.activityMyRunRecordsBackButton);
        listView= findViewById(R.id.activityMyRunRecordsList);
        backButton.setOnClickListener((this::returnToMain));

        //ADD data to runningRecords list function
        //TODO: ADD THE FUNCTION
        fillDataToRunningRecordsList();
        //Adapter Setup
        recordListAdapter = new RecordListAdapter(runningRecords, MyRunRecordsActivity.this);
        listView.setAdapter(recordListAdapter);

    }
    //return Back to Home
    private void returnToMain(View view) {
        finish();
    }

    private void fillDataToRunningRecordsList(){

        runningRecords = new ArrayList<>();
        Task<List<RunningRecord>> taskRetrieveData = workoutRecordViewModel.getRunningRecords().addOnCompleteListener(new OnCompleteListener<List<RunningRecord>>() {
            @Override
            public void onComplete(@NonNull Task<List<RunningRecord>> task) {
                runningRecords = task.getResult();
                Log.d("NOTICE MEE !!!!!", "filling array list" + "Size is at: " +  runningRecords.size());
                if(runningRecords.size() >= 1){
                    Log.i("Data: " , runningRecords.get(0).getStartTime() + " and " + runningRecords.get(0).getFinishTime());
                }
                recordListAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("FAILURE","failure detected on retrieving data !");
            }
        });
    }
}