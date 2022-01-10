package com.main.fitness.data.Model;

public enum WorkoutProgramLevel {
    BEGINNER("BEGINNER"),
    INTERMEDIATE("INTERMEDIATE"),
    ADVANCED("ADVANCED");

    private final String level;
    WorkoutProgramLevel(String level){
        this.level = level;
    }

    public String getLevel(){
        return this.level;
    }

    public static WorkoutProgramLevel fromString(String str){
        for (WorkoutProgramLevel workoutProgramLevel : WorkoutProgramLevel.values()){
            if (workoutProgramLevel.level.equalsIgnoreCase(str)){
                return workoutProgramLevel;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "UserLevel{" +
                "level='" + level + '\'' +
                '}';
    }
}

