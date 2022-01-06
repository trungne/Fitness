package com.main.fitness.data.Model;

import java.util.List;

public class Schedule {
    private final List<TrainingSession> trainingSessionList;
    private int currentTrainingSessionIndex;
    public Schedule(List<TrainingSession> trainingSessionList){
        this.trainingSessionList = trainingSessionList;
        this.currentTrainingSessionIndex = 0;
    }

    public Schedule(List<TrainingSession> trainingSessionList, int current){
        this.trainingSessionList = trainingSessionList;
        this.currentTrainingSessionIndex = current;
    }

    public boolean nextDay(){
        if (this.currentTrainingSessionIndex == this.trainingSessionList.size() - 1){
            return false;
        }
        else{
            this.currentTrainingSessionIndex++;
            return true;
        }
    }

    public TrainingSession getCurrentSession(){
        return this.trainingSessionList.get(currentTrainingSessionIndex);
    }


}
