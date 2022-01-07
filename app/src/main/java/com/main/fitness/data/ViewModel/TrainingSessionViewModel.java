package com.main.fitness.data.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.main.fitness.data.Model.WorkoutSet;
import com.main.fitness.data.Model.TrainingSession;

public class TrainingSessionViewModel extends AndroidViewModel {
    private TrainingSession mTrainingSession;

    private MutableLiveData<Integer> exerciseOrderLiveData;
    private MutableLiveData<WorkoutSet> workoutSetMutableLiveData;
    public TrainingSessionViewModel(@NonNull Application application) {
        super(application);
    }

    // only set Training session ONCE!
    public void setTrainingSession(TrainingSession trainingSession){
        this.mTrainingSession = trainingSession;
        this.exerciseOrderLiveData = new MutableLiveData<>(mTrainingSession.getCurrentExerciseOrder());
        this.workoutSetMutableLiveData = new MutableLiveData<>(mTrainingSession.getCurrentExercise());
    }

    public MutableLiveData<Integer> getExerciseOrderLiveData() {
        return exerciseOrderLiveData;
    }

    public int getExerciseSize(){
        return this.mTrainingSession.getExerciseSets().length;
    }

    public MutableLiveData<WorkoutSet> getWorkoutSetMutableLiveData() {
        return workoutSetMutableLiveData;
    }

    public void nextExercise(){
        if(this.mTrainingSession.nextExercise()){
            this.exerciseOrderLiveData.setValue(mTrainingSession.getCurrentExerciseOrder());
            this.workoutSetMutableLiveData.setValue(mTrainingSession.getCurrentExercise());
        }
    }

    public void previousExercise(){
        if(this.mTrainingSession.previousExercise()){
            this.exerciseOrderLiveData.setValue(mTrainingSession.getCurrentExerciseOrder());
            this.workoutSetMutableLiveData.setValue(mTrainingSession.getCurrentExercise());
        }
    }
}
