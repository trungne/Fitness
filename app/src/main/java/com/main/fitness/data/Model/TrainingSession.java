package com.main.fitness.data.Model;

public class TrainingSession {
    private final WorkoutSet[] workoutSets;
    private int currentExerciseOrder;
    private final String[] targetMuscles;

    public TrainingSession(String[] targetMuscles, WorkoutSet[] workoutSets){
        this.targetMuscles = targetMuscles;
        this.workoutSets = workoutSets;
        this.currentExerciseOrder = 0;
    }

    public String[] getTargetMuscles() {
        return targetMuscles;
    }

    public boolean nextExercise(){
        if (this.currentExerciseOrder == workoutSets.length - 1){
            return false;
        }
        else{
            this.currentExerciseOrder++;
            return true;
        }
    }

    public boolean previousExercise(){
        if (this.currentExerciseOrder == 0){
            return false;
        }
        else{
            this.currentExerciseOrder--;
            return true;
        }
    }

    public WorkoutSet[] getExerciseSets() {
        return workoutSets;
    }

    public int getCurrentExerciseOrder(){
        return this.currentExerciseOrder;
    }

    public WorkoutSet getCurrentExercise(){
        return this.workoutSets[this.currentExerciseOrder];
    }


}
