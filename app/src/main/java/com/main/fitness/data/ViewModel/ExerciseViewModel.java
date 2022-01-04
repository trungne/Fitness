package com.main.fitness.data.ViewModel;

import android.app.Application;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.IOException;
import java.util.Arrays;

public class ExerciseViewModel extends AndroidViewModel {
    private static final String EXERCISE_BANK_FOLDER_PATH = "exercise_bank"; // relative path
    private Application application;
    private AssetManager mAssetManager;
    public ExerciseViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.mAssetManager = application.getAssets();
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
}
