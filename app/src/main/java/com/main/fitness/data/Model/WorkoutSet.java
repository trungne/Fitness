package com.main.fitness.data.Model;

public class WorkoutSet {
    private final String exerciseName;
    public final int[] reps;
    public final int[] weight;


    public WorkoutSet(String exercise, int[] reps, int[] weight){
        this.exerciseName = exercise;
        this.reps = reps;
        this.weight = weight;
    }

    public String getExercise() {
        return exerciseName;
    }

    public int[] getReps() {
        return reps;
    }

    public int[] getWeights(){
        return weight;
    }
}
