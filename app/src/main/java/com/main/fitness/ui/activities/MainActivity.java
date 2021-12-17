package com.main.fitness.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.main.fitness.R;
import com.main.fitness.data.ViewModel.UserViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private UserViewModel userViewModel;
    private FirebaseUser user;
    private BottomNavigationView bottomNavigationView;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        this.bottomNavigationView = findViewById(R.id.MainActivityBottomNavigationView);
        if (this.userViewModel.isLoggedIn()) {
            // redirect to dashboard
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
            return;
        } else {
            //redirect to login activity
            createSignInIntent();
        }
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            user = mAuth.getCurrentUser();
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            if (response == null) {
                finish();
                return;
            }
            if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
            } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                Toast.makeText(this, String.valueOf(response.getError().getErrorCode()), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void createSignInIntent() {
        Log.d(TAG, "createSignInIntent: called");
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.login_logo)
                .build();
        signInLauncher.launch(signInIntent);
    }

    public void signInWithEmailAndPassword(View view) {
        String email = user.getEmail(); // get email from UI
        String password = ""; // get password from UI
        this.userViewModel.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Error with username or password!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Objects.requireNonNull(task.getResult()).getUser();
                    // update user
                });
    }
}