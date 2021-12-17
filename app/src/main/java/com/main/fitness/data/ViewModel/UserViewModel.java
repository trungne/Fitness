package com.main.fitness.data.ViewModel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.main.fitness.data.Model.AppUser;
import com.main.fitness.data.Model.UserExperience;

import java.util.HashMap;
import java.util.concurrent.Executors;

public class UserViewModel extends AndroidViewModel {
    private static final String USER_COLLECTION = "users";

    private static final String UID_FIELD = "uid";
    private static final String EMAIL_FIELD = "email";
    private static final String PHONE_NUMBER_FIELD = "phoneNumber";
    private static final String PHOTO_URL_FIELD = "photoUrl";
    private static final String DISPLAY_NAME_FIELD = "displayName";
    private static final String WORKOUT_SCORE_FIELD = "workoutScore";
    private static final String USER_EXPERIENCE_FIELD = "userExperience";

    private final Application application;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> signInWithEmailAndPassword(String email, String password){
        return this.mAuth.signInWithEmailAndPassword(email, password);
    }

    public boolean isLoggedIn(){
        return this.mAuth.getCurrentUser() != null;
    }

    public Task<Void> createUserWithEmailAndPassword(@NonNull String email,
                                                     @NonNull String password,
                                                     @NonNull String displayName,
                                                     @NonNull String phoneNumber,
                                                     @NonNull UserExperience userExperience){
        return this.mAuth.createUserWithEmailAndPassword(email, password).continueWithTask(Executors.newSingleThreadExecutor(), new Continuation<AuthResult, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<AuthResult> task) throws Exception {
                if (!task.isSuccessful() || task.getResult() == null || task.getResult().getUser() == null){
                    throw new Exception("Cannot create user with email and password");
                }
                FirebaseUser firebaseUser = task.getResult().getUser();

                AppUser appUser = new AppUser(firebaseUser.getUid(),
                        displayName,
                        email,
                        "",
                        phoneNumber,
                        0,
                        userExperience);


                return createUserDataInFirestore(appUser);
            }
        });
    }

    public Task<Void> syncFirebaseUserDataWithFirestore(FirebaseUser firebaseUser){
        DocumentReference documentReference = this.db.collection(USER_COLLECTION).document(firebaseUser.getUid());

        HashMap<String, Object> userData = new HashMap<>();

        String displayName = firebaseUser.getDisplayName();
        if (!TextUtils.isEmpty(displayName)){
            userData.put(DISPLAY_NAME_FIELD, displayName);
        }

        String email = firebaseUser.getEmail();
        if (!TextUtils.isEmpty(email)){
            userData.put(EMAIL_FIELD, email);
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

    public void signOut(){
        this.mAuth.signOut();
    }

    private Task<Void> createUserDataInFirestore(AppUser appUser){
        HashMap<String, Object> userData = new HashMap<>();

        userData.put(UID_FIELD, appUser.getUid());
        userData.put(DISPLAY_NAME_FIELD, appUser.getDisplayName());
        userData.put(EMAIL_FIELD, appUser.getUid());
        userData.put(PHOTO_URL_FIELD, appUser.getUid());
        userData.put(PHONE_NUMBER_FIELD, appUser.getUid());
        userData.put(WORKOUT_SCORE_FIELD, appUser.getUid());
        userData.put(USER_EXPERIENCE_FIELD, appUser.getUid());

        return this.db.collection(USER_COLLECTION).document(appUser.getUid()).set(userData, SetOptions.merge());
    }


}
