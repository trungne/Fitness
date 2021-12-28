package com.main.fitness.ui.activities.gym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.main.fitness.R;
import com.main.fitness.data.Model.Program;
import com.main.fitness.data.ViewModel.ProgramViewModel;

public class ProgramDetailActivity extends AppCompatActivity {
    public static final String PROGRAM_ID_KEY = "com.main.fitness.ui.activities.gym.ProgramDetailActivity.programID";
    private ProgramViewModel programViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_detail);
        Intent intent = getIntent();
        if (intent == null || TextUtils.isEmpty(intent.getStringExtra(PROGRAM_ID_KEY))){
            finish();
            Toast.makeText(this, "Cannot load program!", Toast.LENGTH_SHORT).show();
            return;
        }

        this.programViewModel = new ViewModelProvider(this).get(ProgramViewModel.class);

        String programId = intent.getStringExtra(PROGRAM_ID_KEY);
        this.programViewModel.getProgram(programId).addOnCompleteListener(this, new OnCompleteListener<Program>() {
            @Override
            public void onComplete(@NonNull Task<Program> task) {
                if (!task.isSuccessful()){
                    return;
                }

                Program program = task.getResult();
                Log.i("ProgramDetailActivity", program.getName());
            }
        });

    }
}