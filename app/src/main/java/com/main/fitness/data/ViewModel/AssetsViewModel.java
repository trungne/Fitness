package com.main.fitness.data.ViewModel;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.main.fitness.data.Model.Exercise;
import com.main.fitness.data.Model.WorkoutProgram;
import com.main.fitness.data.Model.UserLevel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.grpc.internal.JsonParser;

public class AssetsViewModel extends AndroidViewModel {
    private static final String TAG = "ExerciseViewModel";
    private static final Set<String> VALID_IMAGE_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(".png", ".jpg", ".jpeg"));
    private static final Set<String> VALID_TEXT_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(".txt", ".md"));

    private static final String EXERCISE_BANK_FOLDER_PATH = "exercise_bank"; // relative path
    private static final String PROGRAMS_FOLDER_PATH = "programs";
    private static final String STRENGTH_PROGRAMS_FOLDER_PATH = PROGRAMS_FOLDER_PATH + File.separator + "strength";
    private static final String CARDIO_PROGRAMS_FOLDER_PATH = PROGRAMS_FOLDER_PATH + File.separator + "cardio";

    private static final String PROGRAM_NAME_FIELD = "name";
    private static final String PROGRAM_DURATION_FIELD = "duration";
    private static final String PROGRAM_DAYS_PER_WEEK_FIELD = "daysPerWeek";
    private static final String PROGRAM_GOAL_FIELD = "goal";
    private static final String PROGRAM_LEVELS_FIELD = "levels";
    private static final String PROGRAM_OVERVIEW_FIELD = "overview";


    private final Application application;
    private final AssetManager mAssetManager;
    public AssetsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.mAssetManager = application.getAssets();
    }

    private String getJSONFilePath(String folderPath){
        try {
            String[] files = this.mAssetManager.list(folderPath);
            for (String file: files){
                if(file.contains("json")){
                    return folderPath + File.separator + file;
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return "";
    }

    public Task<List<WorkoutProgram>> getPrograms(String type){
        final TaskCompletionSource<List<WorkoutProgram>> taskCompletionSource = new TaskCompletionSource<>();

        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            String folderPath = PROGRAMS_FOLDER_PATH + File.separator + type;
            try {
                String[] folders = this.mAssetManager.list(folderPath);

                List<WorkoutProgram> workoutProgramList = new ArrayList<>();
                for (String folder: folders){
                    WorkoutProgram w = getProgram(folderPath + File.separator + folder);
                    if (w != null){
                        workoutProgramList.add(w);
                    }
                }

                taskCompletionSource.setResult(workoutProgramList);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        return taskCompletionSource.getTask();
    }

    private String getTextFilePath(String folderPath){
        try {
            String[] files = this.mAssetManager.list(folderPath);
            for (String file: files){
                if (VALID_TEXT_FILE_EXTENSIONS.contains(getFileExtension(file))){
                    return folderPath + File.separator + file;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return "";
    }

    private String getImageFilePath(String folderPath){
        try {
            String[] files = this.mAssetManager.list(folderPath);
            for (String file: files){
                if (VALID_IMAGE_FILE_EXTENSIONS.contains(getFileExtension(file))){
                    return folderPath + File.separator + file;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private WorkoutProgram getProgram(String path){
        String jsonString = getStringFromFile(getJSONFilePath(path));
        if (TextUtils.isEmpty(jsonString)){
            return null;
        }

        try {
            Log.i(TAG, jsonString);

            JSONObject json = new JSONObject(jsonString);
            String programName = json.getString(PROGRAM_NAME_FIELD);
            Integer programDuration = json.getInt(PROGRAM_DURATION_FIELD);
            Integer programDPW = json.getInt(PROGRAM_DAYS_PER_WEEK_FIELD);
            String programGoal = json.getString(PROGRAM_GOAL_FIELD);

            JSONArray jsonArray = (JSONArray) json.get(PROGRAM_LEVELS_FIELD);
            List<UserLevel> userLevels = new ArrayList<>();

            for(int i = 0; i < jsonArray.length(); i++){
                String level = (String) jsonArray.get(i);
                UserLevel userLevel = UserLevel.fromString(level);
                if (userLevel != null){
                    userLevels.add(userLevel);
                }
            }

            String programOverview = json.getString(PROGRAM_OVERVIEW_FIELD);

            WorkoutProgram workoutProgram = new WorkoutProgram();
            workoutProgram.setName(programName);
            workoutProgram.setDuration(programDuration);
            workoutProgram.setDaysPerWeek(programDPW);
            workoutProgram.setGoal(programGoal);
            workoutProgram.setLevels(userLevels);
            workoutProgram.setOverview(programOverview);
            workoutProgram.setFolderPath(path);

            Drawable drawable = getDrawableFromFile(getImageFilePath(path));
            workoutProgram.setBanner(drawable);

            return workoutProgram;
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return null;
        }
    }

    public Task<WorkoutProgram> getProgram(String name, String type){
        final TaskCompletionSource<WorkoutProgram> taskCompletionSource = new TaskCompletionSource<>();
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            String folderPath;
            if (type.equals("strength")){
                folderPath = STRENGTH_PROGRAMS_FOLDER_PATH + File.separator + name;
            }
            else{
                folderPath = CARDIO_PROGRAMS_FOLDER_PATH + File.separator + name;
            }

            WorkoutProgram workoutProgram = getProgram(folderPath);
            if (workoutProgram != null){
                taskCompletionSource.setResult(workoutProgram);
            }
            else{
                taskCompletionSource.setException(new Exception("Cannot get program"));
            }
        });
        return taskCompletionSource.getTask();
    }

    public String[] getExerciseTypes(){
        try {
            String[] folders = this.mAssetManager.list(EXERCISE_BANK_FOLDER_PATH);
            Arrays.sort(folders);
            return folders;
        } catch (IOException e) {
            return null;
        }
    }

    private String getFilename(String path){
        if (TextUtils.isEmpty(path)){
            return "";
        }
        int lastIndexOfFileSeparator = path.lastIndexOf(File.separator);

        if (lastIndexOfFileSeparator == -1){
            return "";
        }
        return path.substring(lastIndexOfFileSeparator + 1);

    }

    public Exercise getExercise(String path){
        try {
            String[] exerciseFiles = this.mAssetManager.list(path);
            if (exerciseFiles.length != 2){
                return null;
            }
            String name = toTitleCase(getFilename(path));

            String illustrationFileName = getImageFilePath(path);
            String descriptionFileName = getTextFilePath(path);

            if (TextUtils.isEmpty(illustrationFileName) || TextUtils.isEmpty(descriptionFileName)) {
                return null;
            }

            String description = getStringFromFile(descriptionFileName);
            Drawable drawable = getDrawableFromFile(illustrationFileName);

            if (TextUtils.isEmpty(description) || drawable == null){
                Log.i("Exercise View Model", "fail");
            }

            return new Exercise(path, name, description, drawable);
        } catch (IOException e) {
            return null;
        }
    }

    public List<Exercise> getExercises(String type) {
        String pathToExerciseTypeFolder = EXERCISE_BANK_FOLDER_PATH + File.separator + type;

        List<Exercise> exerciseList = new ArrayList<>();
        try {
            String[] folders = this.mAssetManager.list(pathToExerciseTypeFolder);
            for (String folder: folders){
                String exerciseFolder = pathToExerciseTypeFolder + File.separator + folder;
                Exercise e = getExercise(exerciseFolder);
                if (e != null){
                    exerciseList.add(e);
                }
            }
        } catch (IOException ignore){

        }
        return exerciseList;
    }

    private String getFileExtension(String filename){
        return filename.substring(filename.lastIndexOf('.'));
    }

    private Drawable getDrawableFromFile(String file){
        try {
            InputStream inputStream = this.mAssetManager.open(file);
            return Drawable.createFromStream(inputStream, null);
        } catch (IOException e){
            return null;
        }
    }

    private String getStringFromFile(String file){
        try {
            InputStream inputStream = this.mAssetManager.open(file);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            return new String(buffer);
        } catch (IOException e) {
            return "";
        }
    }


    /**
     * Function to convert string to title case
     *
     * @param string - Passed string
     */
    public static String toTitleCase(String string) {

        // Check if String is null
        if (string == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder(string); // String builder to store string
        final int builderLength = builder.length();

        for (int i = 0; i <builderLength; i++){
            char c = builder.charAt(i);
            if (c == '_'){
                builder.setCharAt(i, ' ');
            }
        }
        boolean whiteSpace = true;

        // Loop through builder
        for (int i = 0; i < builderLength; ++i) {

            char c = builder.charAt(i); // Get character at builders position

            if (whiteSpace) {

                // Check if character is not white space
                if (!Character.isWhitespace(c)) {

                    // Convert to title case and leave whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    whiteSpace = false;
                }
            } else if (Character.isWhitespace(c)) {

                whiteSpace = true; // Set character is white space

            } else {
                builder.setCharAt(i, Character.toLowerCase(c)); // Set character to lowercase
            }
        }

        return builder.toString(); // Return builders text
    }
}
