package com.main.fitness.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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
import com.main.fitness.ui.fragments.RequireSignInFragment;
import com.main.fitness.ui.fragments.UserFragment;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private UserViewModel userViewModel;
    private FirebaseUser user;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        this.bottomNavigationView = findViewById(R.id.MainActivityBottomNavigationView);
        this.fragmentContainer = findViewById(R.id.MainActivityFragmentContainer);

        if (this.userViewModel.isLoggedIn()) {
            // redirect to dashboard
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.MainActivityFragmentContainer,
                            UserFragment.newInstance())
                    .commit();
        } else {
            //redirect to login activity
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.MainActivityFragmentContainer,
                            RequireSignInFragment.newInstance())
                    .commit();
        }
    }

}