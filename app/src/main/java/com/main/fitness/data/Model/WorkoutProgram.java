package com.main.fitness.data.Model;

import android.graphics.drawable.Drawable;

import java.util.List;

public class WorkoutProgram {
    private String name;
    private String goal;
    private String overview;
    private List<UserLevel> levels;
    private Integer duration;
    private Integer daysPerWeek;
    private Drawable banner;

    private String folderPath;

    public WorkoutProgram() {}

    public Drawable getBanner() {
        return banner;
    }

    public String getFolderPath() {
        return folderPath;
    }
    public String getName() {
        return name;
    }

    public Integer getDaysPerWeek() {
        return daysPerWeek;
    }

    public List<UserLevel> getLevels() {
        return levels;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getGoal() {
        return goal;
    }

    public String getOverview() {
        return overview;
    }

    public void setBanner(Drawable banner) {
        this.banner = banner;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDaysPerWeek(Integer daysPerWeek) {
        this.daysPerWeek = daysPerWeek;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setLevels(List<UserLevel> levels) {
        this.levels = levels;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
