package com.main.fitness.data.ViewModel;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.main.fitness.data.Model.Exercise;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseViewModel extends AndroidViewModel {
    private static final String EXERCISE_BANK_FOLDER_PATH = "exercise_bank"; // relative path
    private final Application application;
    private final AssetManager mAssetManager;
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

    public List<Exercise> getExercises(String type){
        String path = EXERCISE_BANK_FOLDER_PATH + "/" + type;

        List<Exercise> exerciseList = new ArrayList<>();
        try {
            String[] exerciseFolders = this.mAssetManager.list(path);
            // loop through folders and get .png, .txt files in each
            // .png will be converted to drawable and .txt will be the description
            // name of the exercise is the name of the folder. barbell_bench_press -> Barbell Bench Press
            for(String folder: exerciseFolders){
                String[] exerciseFiles = this.mAssetManager.list(path + "/" + folder);
                if (exerciseFiles.length != 2){
                    continue;
                }
                String name = toTitleCase(folder);
                String illustrationFileName;
                String descriptionFileName;

                if (exerciseFiles[0].contains(".png")){
                    illustrationFileName = path + "/" + folder + "/" + exerciseFiles[0];
                    descriptionFileName = path + "/" + folder + "/" + exerciseFiles[1];
                }
                else{
                    illustrationFileName = path + "/" + folder + "/" + exerciseFiles[1];
                    descriptionFileName = path + "/" + folder + "/" + exerciseFiles[0];
                }

                String description = getStringFromFile(descriptionFileName);
                Drawable drawable = getDrawableFromFile(illustrationFileName);

                if (TextUtils.isEmpty(description) || drawable == null){
                    Log.i("Exercise View Model", "fail");
                    continue;
                }
                exerciseList.add(new Exercise(name, description, drawable));
            }

            return exerciseList;
        } catch (IOException e) {
            return new ArrayList<>();
        }
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

        // Loop through builder
        for (int i = 0; i < builderLength; ++i) {

            char c = builder.charAt(i); // Get character at builders position

            // Check if character is not white space
            if (!Character.isWhitespace(c)) {
                // Convert to title case and leave whitespace mode.
                builder.setCharAt(i, Character.toTitleCase(c));
            }
            else if (Character.isWhitespace(c)) {
                // do nothing
            } else {
                builder.setCharAt(i, Character.toLowerCase(c)); // Set character to lowercase
            }
        }

        return builder.toString(); // Return builders text
    }
}
