package com.main.fitness.service;

import android.app.IntentService;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class MyLocationUpdateService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
