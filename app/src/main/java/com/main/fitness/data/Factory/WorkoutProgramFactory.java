package com.main.fitness.data.Factory;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.main.fitness.data.FileUtils;
import com.main.fitness.data.Model.UserLevel;
import com.main.fitness.data.Model.WorkoutProgram;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WorkoutProgramFactory {
    private static final String PROGRAM_NAME_FIELD = "name";
    private static final String PROGRAM_DURATION_FIELD = "duration";
    private static final String PROGRAM_DAYS_PER_WEEK_FIELD = "daysPerWeek";
    private static final String PROGRAM_GOAL_FIELD = "goal";
    private static final String PROGRAM_LEVELS_FIELD = "levels";
    private static final String PROGRAM_OVERVIEW_FIELD = "overview";

    public static WorkoutProgram fromJSON(JSONObject json){

        try {
            WorkoutProgram workoutProgram = new WorkoutProgram();
            String programName = json.getString(PROGRAM_NAME_FIELD);
            Integer programDuration = json.getInt(PROGRAM_DURATION_FIELD);
            Integer programDaysPerWeek = json.getInt(PROGRAM_DAYS_PER_WEEK_FIELD);
            String programGoal = json.getString(PROGRAM_GOAL_FIELD);
            String programOverview = json.getString(PROGRAM_OVERVIEW_FIELD);

            if (TextUtils.isEmpty(programName)
                    || TextUtils.isEmpty(programGoal)
                    || TextUtils.isEmpty(programOverview)
            ){
                return null;
            }



            JSONArray jsonArray = json.getJSONArray(PROGRAM_LEVELS_FIELD);
            List<UserLevel> programUserLevels = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++){
                String level = (String) jsonArray.get(i);
                UserLevel userLevel = UserLevel.fromString(level);
                if (userLevel != null){
                    programUserLevels.add(userLevel);
                }
            }

            workoutProgram.setName(programName);
            workoutProgram.setDuration(programDuration);
            workoutProgram.setDaysPerWeek(programDaysPerWeek);
            workoutProgram.setGoal(programGoal);
            workoutProgram.setOverview(programOverview);
            workoutProgram.setLevels(programUserLevels);

            return workoutProgram;
        } catch (JSONException jsonException){
            return null;
        }
    }
}
