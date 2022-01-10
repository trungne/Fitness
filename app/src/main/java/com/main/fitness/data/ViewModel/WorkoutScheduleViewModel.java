package com.main.fitness.data.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.main.fitness.data.Model.WorkoutSchedule;
import com.main.fitness.data.Model.WorkoutSet;
import com.main.fitness.data.Model.WorkoutSession;

public class WorkoutScheduleViewModel extends AndroidViewModel {
    private static final String TAG = "WorkOutScheduleViewModel";
    private WorkoutSession mCurrentWorkoutSession;

    private MutableLiveData<Integer> exerciseOrderLiveData;
    private MutableLiveData<WorkoutSet> workoutSetMutableLiveData;

    public WorkoutScheduleViewModel(@NonNull Application application, @NonNull WorkoutSession currentSession){
        super(application);
        this.mCurrentWorkoutSession = currentSession;
        this.exerciseOrderLiveData = new MutableLiveData<>(this.mCurrentWorkoutSession.getCurrentExerciseOrder());
        this.workoutSetMutableLiveData = new MutableLiveData<>(this.mCurrentWorkoutSession.getCurrentExercise());
    }

    public MutableLiveData<Integer> getExerciseOrderLiveData() {
        return exerciseOrderLiveData;
    }

    public int getExerciseSize(){
        return this.mCurrentWorkoutSession.getExerciseSets().length;
    }

    public MutableLiveData<WorkoutSet> getWorkoutSetMutableLiveData() {
        return workoutSetMutableLiveData;
    }

    public void nextExercise(){
        WorkoutSet w = this.mCurrentWorkoutSession.nextExercise();
        if (w != null){
            this.exerciseOrderLiveData.setValue(mCurrentWorkoutSession.getCurrentExerciseOrder());
            this.workoutSetMutableLiveData.setValue(w);
        }
    }

    public void previousExercise(){
        WorkoutSet w = this.mCurrentWorkoutSession.previousExercise();
        if (w != null){
            this.exerciseOrderLiveData.setValue(this.mCurrentWorkoutSession.getCurrentExerciseOrder());
            this.workoutSetMutableLiveData.setValue(w);
        }
    }
}
