package com.main.fitness.data.ViewModel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.main.fitness.data.Model.RunningRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class WorkoutRecordViewModel extends AndroidViewModel {
    private static final String REP_MAX_COLLECTION = "repMax";

    private static final String REP_MAX_FIELD = "max";

    private static final String PROGRAM_REGISTRATION_COLLECTION = "programRegistration";
    private static final String PROGRAM_NAME_FIELD = "programName";
    private static final String PROGRAM_DAYS_PER_WEEK_FIELD = "daysPerWeek";
    private static final String USER_ID_FIELD = "uid";

    private final Application application;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAth;

    public WorkoutRecordViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.db = FirebaseFirestore.getInstance();
        this.mAth = FirebaseAuth.getInstance();
    }

    public Task<Void> updateRunningRecord(RunningRecord runningRecord){
        return null;
    }


    public Task<Void> unregisterProgram(String userId, String programId){
        return this.db.collection(PROGRAM_REGISTRATION_COLLECTION).document(userId).delete();
    }

//    public Task<Void> finishWorkout(String workoutProgramName){
//
//    }

    public Task<String> getProgramRegistrationDocumentId(String programName){
        String uid = Objects.requireNonNull(this.mAth.getUid());
        return this.db.collection(PROGRAM_REGISTRATION_COLLECTION)
                .whereEqualTo(USER_ID_FIELD, uid)
                .whereEqualTo(PROGRAM_NAME_FIELD, programName)
                .limit(1)
                .get().continueWith(Executors.newSingleThreadExecutor(), task -> {
            if (task.isSuccessful()){
                QuerySnapshot snapshot = task.getResult();
                List<DocumentSnapshot> documents = snapshot.getDocuments();
                if (!documents.isEmpty()){
                    return documents.get(0).getId();
                }
                return "";
            }
            else{
                throw new Exception();
            }
        });
    }

    public Task<Void> registerProgram(String programName, int programDaysPerWeek){
        return getProgramRegistrationDocumentId(programName).continueWith(Executors.newSingleThreadExecutor(), task -> {
           if (!task.isSuccessful()){
               throw new Exception();
           }
           // if user hasn't registered for the program
           if (TextUtils.isEmpty(task.getResult())){
               String uid = Objects.requireNonNull(this.mAth.getUid());
               HashMap<String, Object> data = new HashMap<>();
               data.put(PROGRAM_NAME_FIELD, programName);
               data.put(USER_ID_FIELD, uid);
               data.put(PROGRAM_DAYS_PER_WEEK_FIELD, programDaysPerWeek);
               this.db.collection(PROGRAM_REGISTRATION_COLLECTION).document().set(data, SetOptions.merge());
           }
           return null;
        });
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
}
