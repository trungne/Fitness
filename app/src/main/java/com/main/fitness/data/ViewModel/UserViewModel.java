package com.main.fitness.data.ViewModel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.main.fitness.data.Model.AppUser;
import com.main.fitness.data.Model.UserLevel;

import java.util.HashMap;
import java.util.concurrent.Executors;

public class UserViewModel extends AndroidViewModel {
    // collection contains documents, which have user id as their unique id,
    private static final String USER_COLLECTION = "users";

    // fields in a document
    public static final String UID_FIELD = "uid";
    public static final String EMAIL_FIELD = "email";
    public static final String PHONE_NUMBER_FIELD = "phoneNumber";
    public static final String PHOTO_URL_FIELD = "photoUrl";
    public static final String DISPLAY_NAME_FIELD = "displayName";
    public static final String WORKOUT_SCORE_FIELD = "workoutScore";
    public static final String USER_LEVEL_FIELD = "userLevel";

    private final Application application;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }
//    /** @param doc the document snapshot to convert to AppUser object
//     * @return AppUser an AppUser, throw error if the document doesn't exist
//     * */
//    private static AppUser convertDocumentSnapshotToAppUser(@NonNull DocumentSnapshot doc) throws Exception {
//        String uid = doc.get(UID_FIELD, String.class);
//        String email = doc.get(EMAIL_FIELD, String.class);
//        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(email)) {
//            throw new Exception("Document doesn't exists");
//        }
//        String displayName = doc.get(DISPLAY_NAME_FIELD, String.class);
//        String photoUrl = doc.get(PHOTO_URL_FIELD, String.class);
//        String phoneNumber = doc.get(PHONE_NUMBER_FIELD, String.class);
//        Integer workoutScore = doc.get(WORKOUT_SCORE_FIELD, Integer.class);
//        if (workoutScore == null){
//            workoutScore = 0;
//        }
//        String userExperienceString = doc.get(USER_EXPERIENCE_FIELD, String.class);
//        UserLevel userLevel = UserLevel.fromString(userExperienceString);
//
//        return new AppUser(uid, displayName, email, photoUrl, phoneNumber, workoutScore, userLevel);
//    }

    public Task<AuthResult> signInWithEmailAndPassword(String email, String password){
        return this.mAuth.signInWithEmailAndPassword(email, password);
    }

    public boolean isLoggedIn(){
        return this.mAuth.getCurrentUser() != null;
    }

    public Task<AppUser> getUser(String uid){
        return this.db.collection(USER_COLLECTION).document(uid).get().continueWith(Executors.newSingleThreadExecutor(), task -> {
            if (!task.isSuccessful() || task.getResult() == null || !task.getResult().exists()){
                throw new Exception("Cannot get AppUser from Firestore");
            }
            return task.getResult().toObject(AppUser.class);
        });
    }

    public Task<Void> syncAuthenticationDataWithFirestore(@NonNull FirebaseUser firebaseUser){
        HashMap<String, Object> userData = new HashMap<>();

        userData.put(UID_FIELD, firebaseUser.getUid());
        userData.put(EMAIL_FIELD, firebaseUser.getEmail());

        String displayName = firebaseUser.getDisplayName();
        if (!TextUtils.isEmpty(displayName)){
            userData.put(DISPLAY_NAME_FIELD, displayName);
        }

        String phoneNumber = firebaseUser.getPhoneNumber();
        if (!TextUtils.isEmpty(phoneNumber)){
            userData.put(PHONE_NUMBER_FIELD, phoneNumber);
        }

        if (firebaseUser.getPhotoUrl() != null){
            String photoUrl = firebaseUser.getPhotoUrl().toString();
            if (!TextUtils.isEmpty(photoUrl)){
                userData.put(PHOTO_URL_FIELD, photoUrl);
            }
        }
        return this.db.collection(USER_COLLECTION).document(firebaseUser.getUid()).set(userData, SetOptions.merge());
    }

    public FirebaseUser getFirebaseUser(){
        return this.mAuth.getCurrentUser();
    }

    /**
     * @param uid user id
     * @param newData a hashmap (key: String, value: Object) of the data to be updated
     * @return a Task<Void> object, the task fails if the user doesn't exist in the Firestore
     * */
    public Task<Void> updateAppUser(@NonNull String uid, @NonNull HashMap<String, Object> newData){
        return this.db.collection(USER_COLLECTION).document(uid).update(newData);
    }

}
