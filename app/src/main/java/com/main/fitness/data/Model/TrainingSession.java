package com.main.fitness.data.Model;

import java.util.List;

public class TrainingSession {
    private final ExerciseSet[] exerciseSets;
    private int currentExercise;
    private final String[] targetMuscles;

    public TrainingSession(String[] targetMuscles, ExerciseSet[] exerciseSets){
        this.targetMuscles = targetMuscles;
        this.exerciseSets = exerciseSets;
        this.currentExercise = 0;
    }

    public String[] getTargetMuscles() {
        return targetMuscles;
    }

    public boolean nextExercise(){
        if (this.currentExercise == exerciseSets.length - 1){
            return false;
        }
        else{
            this.currentExercise++;
            return true;
        }
    }

    public ExerciseSet getCurrentExercise(){
        return this.exerciseSets[this.currentExercise];
    }


}
