package com.main.fitness.data.Factory;

import com.main.fitness.data.Model.TrainingSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ScheduleFactory {
    private static final String EXERCISE_FIELD = "exercise";
    private static final String REP_FIELD = "reps";
    private static final String WEIGHT_FIELD = "weight";
    public static List<List<TrainingSession>> fromJSONArray(JSONArray jsonArray, int sessionNumber){
        try {
            JSONArray trainingSession = jsonArray.getJSONArray(sessionNumber);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return null;
    }
}
