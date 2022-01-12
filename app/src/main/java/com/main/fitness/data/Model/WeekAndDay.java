package com.main.fitness.data.Model;

public class WeekAndDay {
    private final int week;
    private final int day;
    public WeekAndDay(int week, int day){
        this.week = week;
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public int getWeek() {
        return week;
    }
}
