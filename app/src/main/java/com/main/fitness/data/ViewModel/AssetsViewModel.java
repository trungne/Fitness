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
import com.main.fitness.data.Factory.WorkoutProgramFactory;
import com.main.fitness.data.FileUtils;
import com.main.fitness.data.Model.Exercise;
import com.main.fitness.data.Model.WorkoutProgram;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            JSONObject programJson = json.getJSONObject("program");
            WorkoutProgram workoutProgram = WorkoutProgramFactory.fromJSON(programJson);

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

    public Exercise getExercise(String path){
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

            return new Exercise(path, name, description, drawable);
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
}
