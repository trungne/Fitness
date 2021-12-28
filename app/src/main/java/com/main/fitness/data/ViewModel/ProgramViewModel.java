package com.main.fitness.data.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.main.fitness.data.Model.Program;

public class ProgramViewModel extends AndroidViewModel {
    private static final String STRENGTH_PROGRAMS_COLLECTION = "strengthPrograms";
    private static final String CARDIO_PROGRAMS_COLLECTION = "cardioPrograms";

    private final Application application;
    private final FirebaseFirestore db;

    public ProgramViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.db = FirebaseFirestore.getInstance();
    }


    public Query getBaseQueryForStrengthPrograms(){
        return this.db.collection(STRENGTH_PROGRAMS_COLLECTION).orderBy("name");
    }

    public Query getBaseQueryForCardioPrograms() {
        return this.db.collection(CARDIO_PROGRAMS_COLLECTION).orderBy("name");
    }
}
