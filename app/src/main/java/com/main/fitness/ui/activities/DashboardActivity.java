package com.main.fitness.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.main.fitness.R;

public class DashboardActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

//        ImageView imgDashboardGym = findViewById(R.id.imgDashboardGym);
//        ImageView imgDashboardRunning = findViewById(R.id.imgDashboardRunning);

//        imgDashboardGym.setOnClickListener(view ->
//                startActivity(new Intent(DashboardActivity.this, GymActivity.class)));
//        imgDashboardRunning.setOnClickListener(view ->
//                startActivity(new Intent(DashboardActivity.this, RunningActivity.class)));
    }
}
