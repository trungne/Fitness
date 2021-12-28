package com.main.fitness.data.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.main.fitness.data.Model.Program;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class ProgramViewModel extends AndroidViewModel {
    public static final String PROGRAMS_COLLECTION = "programs";

    private final Application application;
    private final FirebaseFirestore db;

    public ProgramViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.db = FirebaseFirestore.getInstance();
    }

    public Task<Program> getProgram(String id){
        return this.db.collection(PROGRAMS_COLLECTION).document(id).get().continueWith(Executors.newSingleThreadExecutor(), new Continuation<DocumentSnapshot, Program>() {
            @NonNull
            @Override
            public Program then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                if (!task.isSuccessful() || task.getResult() == null){
                    throw new Exception("Cannot find program!");
                }

                Program program = task.getResult().toObject(Program.class);

                if (program == null){
                    throw new Exception("Cannot find program!");
                }

                return program;
            }
        });
    }


    public Query getBaseQueryForStrengthPrograms(){
        return this.db.collection(PROGRAMS_COLLECTION).whereEqualTo("type", "strength");
    }

    public Query getBaseQueryForCardioPrograms() {
        return this.db.collection(PROGRAMS_COLLECTION).whereEqualTo("type", "cardio");
    }

    private void transferPrograms(String oldCollection, String newCollection){
        this.db.collection(oldCollection).get().continueWith(Executors.newSingleThreadExecutor(), task -> {
            List<DocumentSnapshot> docs = task.getResult().getDocuments();
            for (DocumentSnapshot doc: docs){
                Program program = doc.toObject(Program.class);
                if (program == null){
                    continue;
                }
                HashMap<String, Object> programData = new HashMap<>();
                programData.put("id", program.getId());
                programData.put("daysPerWeek", program.getDaysPerWeek());
                programData.put("goal", program.getGoal());
                programData.put("imagePath", program.getImagePath());
                programData.put("levels", program.getLevels());
                programData.put("name", program.getName());
                programData.put("overview", program.getOverview());
                programData.put("type", program.getType());
                programData.put("duration", program.getDuration());

                this.db.collection(newCollection).document(program.getId()).set(programData, SetOptions.merge());
            }
            return null;
        });
    }
}
