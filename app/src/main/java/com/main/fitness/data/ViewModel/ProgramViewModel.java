package com.main.fitness.data.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.fitness.data.Model.Program;

public class ProgramViewModel extends AndroidViewModel {
    private static final String PROGRAMS_COLLECTION = "programs";
    private final Application application;
    private final FirebaseFirestore db;

    public ProgramViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.db = FirebaseFirestore.getInstance();
    }

//    public Task<Program> getProgram(@NonNull String name){
//
//    }
}
