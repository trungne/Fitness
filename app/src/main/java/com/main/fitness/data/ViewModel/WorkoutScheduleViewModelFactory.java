package com.main.fitness.data.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.main.fitness.data.Model.WorkoutSchedule;

public class WorkoutScheduleViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final WorkoutSchedule mSchedule;
    public WorkoutScheduleViewModelFactory(@NonNull Application application,@NonNull WorkoutSchedule workoutSchedule){
        this.mApplication = application;
        this.mSchedule = workoutSchedule;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WorkOutScheduleViewModel(mApplication, mSchedule);
    }
}
