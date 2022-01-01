package com.main.fitness.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.main.fitness.R;
import com.main.fitness.data.ViewModel.UserViewModel;
import com.main.fitness.ui.fragments.GymFragment;
import com.main.fitness.ui.fragments.RequireSignInFragment;
import com.main.fitness.ui.fragments.RunningFragment;
import com.main.fitness.ui.fragments.UserFragment;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private UserViewModel userViewModel;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);


        this.bottomNavigationView = findViewById(R.id.MainActivityBottomNavigationView);
        this.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (!this.userViewModel.isLoggedIn()){
                return false;
            }

            if (id == R.id.bottom_nav_user){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MainActivityFragmentContainer,
                                UserFragment.newInstance())
                        .commit();
                return true;
            }
            else if (id == R.id.bottom_nav_gym) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MainActivityFragmentContainer,
                                GymFragment.newInstance())
                        .commit();
                return true;
            }

            else if (id == R.id.bottom_nav_running){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MainActivityFragmentContainer,
                                RunningFragment.newInstance())
                        .commit();
                return true;
            }

            return false;
        });

        // user not signed in
//        if (!this.userViewModel.isLoggedIn()){
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.MainActivityFragmentContainer,
//                            RequireSignInFragment.newInstance())
//                    .commit();
//            return;
//        }

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
}