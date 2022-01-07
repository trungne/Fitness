package com.main.fitness.data.Factory;

import androidx.annotation.NonNull;

import com.main.fitness.data.Model.WorkoutSet;
import com.main.fitness.data.Model.TrainingSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrainingSessionFactory {
    private static final String TARGET_MUSCLES_FIELD = "target_muscles";
    private static final String EXERCISES_FIELD = "exercises";

    private static final String EXERCISE_NAME_FIELD = "name";
    private static final String REPS_FIELD = "reps";
    private static final String WEIGHT_FIELD = "weight";



    public static TrainingSession fromJSON(@NonNull JSONObject jsonObject) {


        try{
            JSONArray targetMusclesArray = jsonObject.getJSONArray(TARGET_MUSCLES_FIELD);
            String[] targetMuscles = new String[targetMusclesArray.length()];
            for(int i = 0; i < targetMusclesArray.length(); i++){
                targetMuscles[i] = targetMusclesArray.getString(i);
            }

            JSONArray exerciseArray = jsonObject.getJSONArray(EXERCISES_FIELD);

            WorkoutSet[] exercises = new WorkoutSet[exerciseArray.length()];
            int indexForExerciseSet = 0;

            for(int i = 0; i < exerciseArray.length(); i++){
                JSONObject exerciseJSONObject = exerciseArray.getJSONObject(i);

                String name = exerciseJSONObject.getString(EXERCISE_NAME_FIELD);

                JSONArray repsJSONArray = exerciseJSONObject.getJSONArray(REPS_FIELD);
                int[] reps = new int[repsJSONArray.length()];
                for (int x = 0; x < repsJSONArray.length(); x++){
                    reps[x] = repsJSONArray.getInt(x);
                }

                JSONArray weightJSONArray = exerciseJSONObject.getJSONArray(WEIGHT_FIELD);
                int[] weight = new int[weightJSONArray.length()];
                for (int x = 0; x < weightJSONArray.length(); x++){
                    weight[x] = weightJSONArray.getInt(x);
                }

                exercises[indexForExerciseSet++] = new WorkoutSet(name, reps, weight);
            }

            return new TrainingSession(targetMuscles, exercises);
        } catch (JSONException jsonException){
            jsonException.printStackTrace();
        }

        return null;
    }
}
