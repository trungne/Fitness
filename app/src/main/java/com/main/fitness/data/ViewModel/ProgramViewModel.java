package com.main.fitness.data.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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

    MutableLiveData<WorkoutProgram> currentWorkoutProgram;

    public ProgramViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.db = FirebaseFirestore.getInstance();
        this.currentWorkoutProgram = new MutableLiveData<>();
    }

    public void setCurrentWorkoutProgram(WorkoutProgram program){
        this.currentWorkoutProgram.setValue(program);
    }

    public MutableLiveData<WorkoutProgram> getCurrentWorkoutProgram(){
        return this.currentWorkoutProgram;
    }

    public Task<WorkoutProgram> getProgram(@NonNull String id){
        return this.db.collection(PROGRAMS_COLLECTION).document(id).get().continueWith(Executors.newSingleThreadExecutor(), task -> {
            if (!task.isSuccessful() || task.getResult() == null){
                throw new Exception("Cannot find program!");
            }

            WorkoutProgram workoutProgram = task.getResult().toObject(WorkoutProgram.class);

            if (workoutProgram == null){
                throw new Exception("Cannot find program!");
            }

            return workoutProgram;
        });
    }



    public Task<Void> unregisterProgram(String userId, String programId){
        return this.db.collection(USER_PROGRAM_COLLECTION).document(userId).delete();
    }

    public Task<Void> registerProgram(String userId, String programId){
        HashMap<String, Object> data = new HashMap<>();
        data.put("programName", programId);

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
}
