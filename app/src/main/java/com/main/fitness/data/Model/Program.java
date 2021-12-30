package com.main.fitness.data.Model;

import java.util.List;

public class Program {
    private String id;
    private String name;
    private String goal;
    private String overview;
    private List<UserLevel> levels;
    private Integer duration;
    private Integer daysPerWeek;
    private String imagePath;
    private String type;

    public Program() {}

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getImagePath() {
        return imagePath;
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

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOverview() {
        return overview;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
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
