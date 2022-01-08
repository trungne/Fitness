package com.main.fitness.data.Model;

import android.graphics.drawable.Drawable;

public class WorkoutExercise {
    private final String folderPath;
    private final String name;
    private final String description;
    private final Drawable illustration;
    public WorkoutExercise(String folderPath, String name, String description, Drawable illustration){
        this.folderPath = folderPath;
        this.name = name;
        this.description = description;
        this.illustration = illustration;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public String getName() {
        return name;
    }

    public Drawable getIllustration() {
        return illustration;
    }

    public String getDescription() {
        return description;
    }
}
