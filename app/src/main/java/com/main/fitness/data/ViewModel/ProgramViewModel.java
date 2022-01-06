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
import com.main.fitness.data.Model.WorkoutProgram;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class ProgramViewModel extends AndroidViewModel {
    private static final String PROGRAMS_COLLECTION = "programs";
    private static final String REP_MAX_COLLECTION = "repMax";
    private static final String USER_PROGRAM_COLLECTION = "usersAndPrograms";
    private static final String REP_MAX_FIELD = "max";

    private final Application application;
    private final FirebaseFirestore db;

    public ProgramViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.db = FirebaseFirestore.getInstance();
    }

    public Task<WorkoutProgram> getProgram(@NonNull String id){
        return this.db.collection(PROGRAMS_COLLECTION).document(id).get().continueWith(Executors.newSingleThreadExecutor(), task -> {
            if (!task.isSuccessful() || task.getResult() == null){
                throw new Exception("Cannot find program!");
            }

            WorkoutProgram program = task.getResult().toObject(WorkoutProgram.class);

            if (program == null){
                throw new Exception("Cannot find program!");
            }

            return program;
        });
    }



    public Task<Void> unregisterProgram(String userId, String programId){
        return this.db.collection(USER_PROGRAM_COLLECTION).document(userId).delete();
    }

    public Task<Void> registerProgram(String userId, String programId){
        HashMap<String, Object> data = new HashMap<>();
        data.put("programId", programId);

        return this.db.collection(USER_PROGRAM_COLLECTION).document(userId).set(data, SetOptions.merge());
    }

    public Task<Void> setRepMax(@NonNull String userId, int repMax){
        HashMap<String, Object> data = new HashMap<>();
        data.put(REP_MAX_FIELD, repMax);
        return this.db.collection(REP_MAX_COLLECTION).document(userId).set(data, SetOptions.merge());
    }

    public Task<Integer> getRepMax(@NonNull String userId){
        return this.db.collection(REP_MAX_COLLECTION).document(userId).get().continueWith(Executors.newSingleThreadExecutor(), task -> {
            if (!task.isSuccessful() || !task.getResult().exists() || task.getResult() == null){
                throw new Exception("Cannot get rep max of the user");
            }
            Integer max = task.getResult().get(REP_MAX_FIELD, Integer.class);
            if (max == null){
                throw new Exception("Cannot get rep max of the user");
            }

            return max;
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
                WorkoutProgram program = doc.toObject(WorkoutProgram.class);
                if (program == null){
                    continue;
                }
                HashMap<String, Object> programData = new HashMap<>();
                //programData.put("id", program.getId());
                programData.put("daysPerWeek", program.getDaysPerWeek());
                programData.put("goal", program.getGoal());
                //programData.put("imagePath", program.getImagePath());
                programData.put("levels", program.getLevels());
                programData.put("name", program.getName());
                programData.put("overview", program.getOverview());
                //programData.put("type", program.getType());
                programData.put("duration", program.getDuration());

                //this.db.collection(newCollection).document(program.getId()).set(programData, SetOptions.merge());
            }
            return null;
        });
    }
}
