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
import com.main.fitness.data.Factory.WorkoutSessionFactory;
import com.main.fitness.data.Factory.WorkoutProgramFactory;
import com.main.fitness.data.FileUtils;
import com.main.fitness.data.Model.WorkoutExercise;
import com.main.fitness.data.Model.WorkoutSchedule;
import com.main.fitness.data.Model.WorkoutSession;
import com.main.fitness.data.Model.WorkoutProgram;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssetsViewModel extends AndroidViewModel {
    private static final String TAG = "ExerciseViewModel";

    private static final String EXERCISE_BANK_FOLDER_PATH = "exercise_bank"; // relative path
    private static final String PROGRAMS_FOLDER_PATH = "programs";

    private final Application application;
    private final AssetManager mAssetManager;
    public AssetsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.mAssetManager = application.getAssets();
    }

    /* SCHEDULE PROGRAM SECTION
     *
     *
     *
     *
     *  */
    public Task<WorkoutSchedule> getWorkoutSchedule(String path, int day){
        final TaskCompletionSource<WorkoutSchedule> taskCompletionSource = new TaskCompletionSource<>();
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            String jsonFilePath = FileUtils.getJSONFilePath(this.mAssetManager, path);
            String jsonString = FileUtils.getStringFromFile(this.mAssetManager, jsonFilePath);
            try {
                JSONObject json = new JSONObject(jsonString);
                JSONArray sessionArray = json.getJSONArray("schedule");
                String workoutProgram = json.getString("name");

                if (day >= sessionArray.length()){
                    throw new IllegalArgumentException("Day is out of schedule range!");
                }

                WorkoutSession[] sessions = new WorkoutSession[sessionArray.length()];
                for (int i = 0; i < sessionArray.length(); i++){
                    WorkoutSession w = WorkoutSessionFactory.fromJSON(sessionArray.getJSONObject(i));
                    sessions[i] = w;
                }
                WorkoutSchedule schedule = new WorkoutSchedule(workoutProgram, sessions, day);

                taskCompletionSource.setResult(schedule);


            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
                taskCompletionSource.setException(jsonException);
            }
            catch (IllegalArgumentException exception){
                taskCompletionSource.setException(exception);
            }
        });

        return taskCompletionSource.getTask();
    }


    /* WORKOUT PROGRAM SECTION
     *
     *
     *
     *
     *  */

    public Task<List<WorkoutProgram>> getWorkoutPrograms(String type){
        final TaskCompletionSource<List<WorkoutProgram>> taskCompletionSource = new TaskCompletionSource<>();

        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            String folderPath = PROGRAMS_FOLDER_PATH + File.separator + type;
            try {
                String[] folders = this.mAssetManager.list(folderPath);

                List<WorkoutProgram> workoutProgramList = new ArrayList<>();
                for (String folder: folders){
                    WorkoutProgram w = _getWorkoutProgram(folderPath + File.separator + folder);
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

    private WorkoutProgram _getWorkoutProgram(String path){
        String jsonFilePath = FileUtils.getJSONFilePath(this.mAssetManager, path);
        String jsonString = FileUtils.getStringFromFile(this.mAssetManager, jsonFilePath);

        if (TextUtils.isEmpty(jsonString)){
            return null;
        }

        try {
            JSONObject json = new JSONObject(jsonString);
            WorkoutProgram workoutProgram = WorkoutProgramFactory.fromJSON(json);

            if (workoutProgram == null){
                return null;
            }

            Drawable drawable = FileUtils.getDrawableFromFile(this.mAssetManager,
                    FileUtils.getImageFilePath(this.mAssetManager, path));
            workoutProgram.setBanner(drawable);
            workoutProgram.setFolderPath(path);

            return workoutProgram;
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return null;
        }
    }

    public Task<WorkoutProgram> getWorkoutProgram(String path){
        final TaskCompletionSource<WorkoutProgram> taskCompletionSource = new TaskCompletionSource<>();
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            WorkoutProgram workoutProgram = _getWorkoutProgram(path);
            if (workoutProgram != null){
                taskCompletionSource.setResult(workoutProgram);
            }
            else{
                taskCompletionSource.setException(new Exception("Cannot get workout program!"));
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

    private boolean isDirectory(String file){
        return !file.contains(".");
    }
    private String findFilePath(String folder, String name){
        try {
            String[] files = this.mAssetManager.list(folder);
            for(String file: files){
                if (isDirectory(file)){
                    String path = folder + File.separator + file;

                    if (path.contains(name)){
                        return path;
                    }

                    String found = findFilePath(path, name);
                    if (found.contains(name)){
                        return found;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public WorkoutExercise getExerciseByName(String name){
        String path = findFilePath(EXERCISE_BANK_FOLDER_PATH, name);
        if (TextUtils.isEmpty(path)){
            return null;
        }
        else{
            return getExercise(path);
        }
    }

    public WorkoutExercise getExercise(String path){
        try {
            String[] exerciseFiles = this.mAssetManager.list(path);
            if (exerciseFiles.length != 2){
                return null;
            }

            String filename = FileUtils.getFilename(path);
            String name = FileUtils.toTitleCase(filename);

            String illustrationFileName = FileUtils.getImageFilePath(this.mAssetManager, path);
            String descriptionFileName = FileUtils.getTextFilePath(this.mAssetManager, path);

            if (TextUtils.isEmpty(illustrationFileName) || TextUtils.isEmpty(descriptionFileName)) {
                return null;
            }

            String description = FileUtils.getStringFromFile(this.mAssetManager, descriptionFileName);
            Drawable drawable = FileUtils.getDrawableFromFile(this.mAssetManager, illustrationFileName);

            if (TextUtils.isEmpty(description) || drawable == null){
                Log.i("Exercise View Model", "fail");
            }

            return new WorkoutExercise(path, name, description, drawable);
        } catch (IOException e) {
            return null;
        }
    }

    /* EXERCISE SECTION
    *
    *
    *
    *
    *  */

    public List<WorkoutExercise> getExercises(String type) {
        String pathToExerciseTypeFolder = EXERCISE_BANK_FOLDER_PATH + File.separator + type;

        List<WorkoutExercise> workoutExerciseList = new ArrayList<>();
        try {
            String[] folders = this.mAssetManager.list(pathToExerciseTypeFolder);
            for (String folder: folders){
                String exerciseFolder = pathToExerciseTypeFolder + File.separator + folder;
                WorkoutExercise e = getExercise(exerciseFolder);
                if (e != null){
                    workoutExerciseList.add(e);
                }
            }
        } catch (IOException ignore){

        }
        return workoutExerciseList;
    }
}
