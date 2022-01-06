package com.main.fitness.data.Factory;

import com.main.fitness.data.Model.ExerciseSet;
import com.main.fitness.data.Model.TrainingSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrainingSessionFactory {
    private static final String EXERCISE_FIELD = "exercise";
    private static final String REPS_FIELD = "reps";
    private static final String WEIGHT_FIELD = "weight";
    public static TrainingSession fromJSONArray(JSONArray jsonArray){
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String exerciseName = jsonObject.getString(EXERCISE_FIELD);
                JSONArray repsJSONArray = jsonObject.getJSONArray(REPS_FIELD);

                int[] reps = new int[repsJSONArray.length()];
                for (int x = 0; x < repsJSONArray.length(); x++){
                    reps[x] = repsJSONArray.getInt(x);
                }

                JSONArray weightJSONArray = jsonObject.getJSONArray(WEIGHT_FIELD);
                int[] weight = new int[weightJSONArray.length()];
                for (int x = 0; x < weightJSONArray.length(); x++){
                    weight[x] = weightJSONArray.getInt(x);
                }

                ExerciseSet e = new ExerciseSet(exerciseName, reps, weight);
                exerciseSetList.add(e);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
                return null;
            }
        }


        return new TrainingSession(exerciseSetList);
    }
}
