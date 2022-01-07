package com.main.fitness.data.Model;

public class ExerciseSet {
    private final String exerciseName;
    private final int[] reps;
    private final int[] weight;
    private int currentSet;

    public ExerciseSet(String exercise, int[] reps, int[] weight){
        this.exerciseName = exercise;
        this.reps = reps;
        this.weight = weight;
        this.currentSet = 0;
    }

    public ExerciseSet(String exercise, int[] reps, int[] weight, int currentSet){
        this.exerciseName = exercise;
        this.reps = reps;
        this.weight = weight;
        this.currentSet = currentSet;
    }

    public String getExercise() {
        return exerciseName;
    }

    public boolean nextSet(){
        if (this.currentSet == reps.length - 1){
            return false;
        }
        else{
            this.currentSet++;
            return true;
        }
    }

    public int getCurrentReps(){
        return reps[currentSet];
    }

    public int getCurrentWeight(){
        return weight[currentSet];
    }
}
