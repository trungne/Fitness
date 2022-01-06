package com.main.fitness.data.Model;

import java.util.List;

public class TrainingSession {
    private final List<ExerciseSet> exerciseSets;
    private int currentExercise;

    public TrainingSession(List<ExerciseSet> exerciseSets){
        this.exerciseSets = exerciseSets;
        this.currentExercise = 0;
    }

    public TrainingSession(List<ExerciseSet> exerciseSets, int currentExercise){
        this.exerciseSets = exerciseSets;
        this.currentExercise = currentExercise;
    }

    public boolean nextExercise(){
        if (this.currentExercise == exerciseSets.size() - 1){
            return false;
        }
        else{
            this.currentExercise++;
            return true;
        }
    }

    public ExerciseSet getCurrentExercise(){
        return this.exerciseSets.get(this.currentExercise);
    }


}
