package com.main.fitness.data.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.main.fitness.data.Model.WorkoutSchedule;
import com.main.fitness.data.Model.WorkoutSession;

public class WorkoutScheduleViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final WorkoutSession mSession;
    public WorkoutScheduleViewModelFactory(@NonNull Application application,@NonNull WorkoutSession workoutSession){
        this.mApplication = application;
        this.mSession = workoutSession;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WorkoutScheduleViewModel(mApplication, mSession);
    }
}
