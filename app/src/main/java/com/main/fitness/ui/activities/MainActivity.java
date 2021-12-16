package com.main.fitness.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.main.fitness.R;
import com.main.fitness.data.Model.User;
import com.main.fitness.data.ViewModel.UserViewModel;

public class MainActivity extends AppCompatActivity {
    private UserViewModel userViewModel;
    private BottomNavigationView bottomNavigationView;

    // https://firebase.google.com/docs/auth/android/firebaseui?authuser=0
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        this.bottomNavigationView = findViewById(R.id.MainActivityBottomNavigationView);

        if (this.userViewModel.isLoggedIn()){
            // redirect to login activity
        }
        else {
            // redirect to dashboard
        }
    }

    public void signInWithEmailAndPassword(View view){
        String email = ""; // get email from UI
        String password = ""; // get password from UI
        this.userViewModel.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()){
                        Toast.makeText(this, "sai roi!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    task.getResult().getUser();
                    // update user
                });
        //
        //
        //
    }

}