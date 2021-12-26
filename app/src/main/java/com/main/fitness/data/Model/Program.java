package com.main.fitness.data.Model;

import java.util.List;

public class Program {
    private String goal;
    private String overview;
    private List<UserLevel> levels;
    private String duration;
    private Integer daysPerWeek;

    public Program() {}

    public Integer getDaysPerWeek() {
        return daysPerWeek;
    }

    public List<UserLevel> getLevels() {
        return levels;
    }

    public String getDuration() {
        return duration;
    }

    public String getGoal() {
        return goal;
    }

    public String getOverview() {
        return overview;
    }

    public void setDaysPerWeek(Integer daysPerWeek) {
        this.daysPerWeek = daysPerWeek;
    }

    public void setDuration(String duration) {
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