package com.main.fitness.data.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.concurrent.Executors;

public class WorkoutRegistrationViewModel extends AndroidViewModel {
    private static final String PROGRAM_REGISTRATION_COLLECTION = "programRegistration";
    private static final String PROGRAM_NAME_FIELD = "programName";
    private static final String PROGRAM_DAYS_PER_WEEK_FIELD = "daysPerWeek";
    private static final String USER_ID_FIELD = "uid";
    private static final String CURRENT_SESSION_OF_PROGRAM = "currentSession";


    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final Application mApplication;
    public WorkoutRegistrationViewModel(@NonNull Application application) {
        super(application);
        this.mApplication = application;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public Task<Void> registerProgram(String uid, String programName, int programDaysPerWeek){
        HashMap<String, Object> registrationData = new HashMap<>();
        registrationData.put(PROGRAM_NAME_FIELD, programName);
        registrationData.put(PROGRAM_DAYS_PER_WEEK_FIELD, programDaysPerWeek);
        // when first registered, the current session is 0 by default
        registrationData.put(CURRENT_SESSION_OF_PROGRAM, 0);
        return this.db.collection(PROGRAM_REGISTRATION_COLLECTION).document(uid).set(registrationData, SetOptions.merge());
    }

    public Task<Integer> getCurrentSessionDay(String uid){
        return this.db.collection(PROGRAM_REGISTRATION_COLLECTION).document(uid).get().continueWith(Executors.newSingleThreadExecutor(), task -> {
            if (!task.isSuccessful()){
                throw new Exception("Cannot get current session day!");
            }

            Integer day = task.getResult().get(CURRENT_SESSION_OF_PROGRAM, Integer.class);
            if (day == null){
                throw new Exception("Cannot get current session day!");
            }
            else {
                return day;
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
