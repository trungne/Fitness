package com.main.fitness.data.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.main.fitness.data.Model.RunningRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class WorkoutRecordViewModel extends AndroidViewModel {
    private static final String REP_MAX_COLLECTION = "repMax";
    private static final String REP_MAX_FIELD = "max";

    private static final String RUNNING_RECORD_COLLECTION = "runningRecord";
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

    public Task<Void> updateRunningRecord(RunningRecord record){
        return this.db.collection(RUNNING_RECORD_COLLECTION).document().set(record, SetOptions.merge());
    }

    public Task<List<RunningRecord>> getRunningRecords(){
        String uid = Objects.requireNonNull(this.mAth.getUid());
        return this.db.collection(RUNNING_RECORD_COLLECTION)
                .whereEqualTo(USER_ID_FIELD, uid)
                .orderBy("timestamp")
                .get()
                .continueWith(Executors.newSingleThreadExecutor(), task -> {
                    if (!task.isSuccessful() || task.getResult() == null){
                        throw new Exception();
                    }

                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    List<RunningRecord> records = new ArrayList<>();
                    for (DocumentSnapshot doc: documents){
                        RunningRecord r = doc.toObject(RunningRecord.class);
                        records.add(r);
                    }

                    return records;
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
