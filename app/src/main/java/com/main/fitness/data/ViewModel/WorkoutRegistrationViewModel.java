package com.main.fitness.data.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.main.fitness.data.Model.WeekAndDay;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;

public class WorkoutRegistrationViewModel extends AndroidViewModel {
    private static final String PROGRAM_REGISTRATION_COLLECTION = "programRegistration";
    private static final String PROGRAM_NAME_FIELD = "programName";
    private static final String PROGRAM_CURRENT_WEEK = "currentWeek";
    private static final String PROGRAM_CURRENT_DAY = "currentDay";


    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final Application mApplication;
    public WorkoutRegistrationViewModel(@NonNull Application application) {
        super(application);
        this.mApplication = application;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public Task<Void> updateCurrentSession(int week, int day){
        String uid = this.mAuth.getUid();
        HashMap<String, Object> data = new HashMap<>();
        data.put(PROGRAM_CURRENT_WEEK, week);
        data.put(PROGRAM_CURRENT_DAY, day);
        return this.db.collection(PROGRAM_REGISTRATION_COLLECTION).document(Objects.requireNonNull(uid)).set(data, SetOptions.merge());
    }

    public Task<Void> registerProgram(String uid, String programName, int week, int day){
        HashMap<String, Object> registrationData = new HashMap<>();
        registrationData.put(PROGRAM_NAME_FIELD, programName);
        registrationData.put(PROGRAM_CURRENT_WEEK, week);
        registrationData.put(PROGRAM_CURRENT_DAY, day);
        return this.db.collection(PROGRAM_REGISTRATION_COLLECTION).document(uid).set(registrationData, SetOptions.merge());
    }

    public Task<WeekAndDay> getCurrentWeekAndDayOfWorkoutProgram(String uid){
        return this.db.collection(PROGRAM_REGISTRATION_COLLECTION).document(uid).get().continueWith(Executors.newSingleThreadExecutor(), task -> {
            if (!task.isSuccessful()){
                throw new Exception("Cannot get current session day!");
            }

            Integer day = task.getResult().get(PROGRAM_CURRENT_DAY, Integer.class);
            Integer week = task.getResult().get(PROGRAM_CURRENT_WEEK, Integer.class);
            if (day == null){
                throw new Exception("Cannot get current session day!");
            }
            else {
                return new WeekAndDay(week, day);
            }
        });
    }

    public Task<Void> unregisterCurrentProgram(String uid){
        return this.db.collection(PROGRAM_REGISTRATION_COLLECTION).document(uid).delete();
    }

    public Task<String> getCurrentProgramName(String uid){
        return this.db.collection(PROGRAM_REGISTRATION_COLLECTION).document(uid).get().continueWith(Executors.newSingleThreadExecutor(), task -> {
            if (!task.isSuccessful()){
                throw new Exception("Cannot get current program name!");
            }
            String name = task.getResult().get(PROGRAM_NAME_FIELD, String.class);
            if (name == null){
                throw new Exception("Cannot get current program name!");
            }
            else{
                return name;
            }
        });
    }
}
