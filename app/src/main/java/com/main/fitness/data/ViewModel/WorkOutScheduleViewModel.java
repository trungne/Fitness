package com.main.fitness.data.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.main.fitness.data.Model.WorkoutSchedule;
import com.main.fitness.data.Model.WorkoutSet;
import com.main.fitness.data.Model.WorkoutSession;

public class WorkOutScheduleViewModel extends AndroidViewModel {
    private static final String TAG = "WorkOutScheduleViewModel";
    private WorkoutSession mCurrentWorkoutSession;
    private WorkoutSchedule mSchedule;

    private MutableLiveData<Integer> exerciseOrderLiveData;
    private MutableLiveData<WorkoutSet> workoutSetMutableLiveData;

    public WorkOutScheduleViewModel(@NonNull Application application, @NonNull WorkoutSchedule schedule){
        super(application);
        this.mSchedule = schedule;
        this.mCurrentWorkoutSession = this.mSchedule.getCurrentSession();
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
        Log.i(TAG, this.mCurrentWorkoutSession.getCurrentExercise().getExercise());

        WorkoutSet w = this.mCurrentWorkoutSession.nextExercise();
        Log.i(TAG, this.mCurrentWorkoutSession.getCurrentExercise().getExercise());
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
