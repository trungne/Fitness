package com.main.fitness.data.Model;

public class ExerciseSet {
    private final Exercise exercise;
    private final int[] reps;
    private final int[] weight;
    private int currentSet;

    public ExerciseSet(Exercise exercise, int[] reps, int[] weight){
        this.exercise = exercise;
        this.reps = reps;
        this.weight = weight;
        this.currentSet = 0;
    }

    public ExerciseSet(Exercise exercise, int[] reps, int[] weight, int currentSet){
        this.exercise = exercise;
        this.reps = reps;
        this.weight = weight;
        this.currentSet = currentSet;
    }

    public Exercise getExercise() {
        return exercise;
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
