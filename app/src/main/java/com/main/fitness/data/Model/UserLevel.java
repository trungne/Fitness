package com.main.fitness.data.Model;

public enum UserLevel {
    BEGINNER("BEGINNER"),
    INTERMEDIATE("INTERMEDIATE"),
    ADVANCED("ADVANCED");

    private final String level;
    UserLevel(String level){
        this.level = level;
    }

    public String getLevel(){
        return this.level;
    }

    public static UserLevel fromString(String str){
        for (UserLevel userLevel :UserLevel.values()){
            if (userLevel.level.equalsIgnoreCase(str)){
                return userLevel;
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

