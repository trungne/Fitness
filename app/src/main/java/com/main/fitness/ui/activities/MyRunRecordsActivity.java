package com.main.fitness.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.main.fitness.R;
import com.main.fitness.data.Model.RunningRecord;
import com.main.fitness.ui.adapters.RecordListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyRunRecordsActivity extends AppCompatActivity {


    ImageButton backButton;
    List<RunningRecord> runningRecords = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_run_records);

        backButton = findViewById(R.id.activityMyRunRecordsBackButton);
        listView= findViewById(R.id.activityMyRunRecordsList);
        backButton.setOnClickListener((this::returnToMain));

        //ADD data to runningRecords list function
        //TODO: ADD THE FUNCTION

        //Adapter Setup
        RecordListAdapter adapter = new RecordListAdapter(runningRecords, MyRunRecordsActivity.this);
        listView.setAdapter(adapter);

    }
    //return Back to Home
    private void returnToMain(View view) {
        finish();
    }
}