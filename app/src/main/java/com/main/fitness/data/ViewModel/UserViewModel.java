package com.main.fitness.data.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserViewModel extends AndroidViewModel {
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
}
