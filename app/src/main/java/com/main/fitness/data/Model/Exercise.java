package com.main.fitness.data.Model;

import android.graphics.drawable.Drawable;

public class Exercise {
    private final String name;
    private final String description;
    private final Drawable illustration;
    public Exercise(String name, String description, Drawable illustration){
        this.name = name;
        this.description = description;
        this.illustration = illustration;
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
