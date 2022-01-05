package com.main.fitness.ui.activities.gym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.main.fitness.R;
import com.main.fitness.data.GlideApp;
import com.main.fitness.data.Model.WorkoutProgram;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.data.ViewModel.ProgramViewModel;
import com.main.fitness.ui.adapters.WorkoutProgramAdapter;

import java.util.List;

public class ProgramListActivity extends AppCompatActivity {
    // this key is used to indicate whether user has selected to view strength or cardio programs
    public static final String PROGRAMS_KEY = "com.main.fitness.ui.activities.gym.ProgramListActivity.programKey";

    public static final String STRENGTH_PROGRAMS = "STRENGTH_PROGRAMS";
    public static final String CARDIO_PROGRAMS = "CARDIO_PROGRAMS";

    private RecyclerView programListRecycleView;
    private ProgramViewModel programViewModel;
    private AssetsViewModel assetsViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_list);
        this.programListRecycleView = findViewById(R.id.ProgramListRecycleView);
        this.assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        this.programViewModel = new ViewModelProvider(this).get(ProgramViewModel.class);

        Intent intent = getIntent();
        if (intent == null || TextUtils.isEmpty(intent.getStringExtra(PROGRAMS_KEY))){
            finish();
            return;
        }
        String type;

        if (intent.getStringExtra(PROGRAMS_KEY).equals(CARDIO_PROGRAMS)){
            type = "cardio";
        }
        else if (intent.getStringExtra(PROGRAMS_KEY).equals(STRENGTH_PROGRAMS)){
            type = "strength";
        }
        else{
            finish();
            return;
        }

        this.assetsViewModel.getPrograms(type).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()){
                Toast.makeText(this, "Cannot load programs", Toast.LENGTH_SHORT).show();
                return;
            }

            List<WorkoutProgram> workoutProgramList = task.getResult();
            this.programListRecycleView = findViewById(R.id.ProgramListRecycleView);
            this.programListRecycleView.setLayoutManager(new LinearLayoutManager(this));
            WorkoutProgramAdapter adapter = new WorkoutProgramAdapter(workoutProgramList);
            this.programListRecycleView.setAdapter(adapter);
        });
    }





}