package com.main.fitness.data.ViewModel;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.main.fitness.data.Model.Exercise;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssetsViewModel extends AndroidViewModel {
    private static final String TAG = "ExerciseViewModel";
    private static final Set<String> VALID_IMAGE_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(".png", ".jpg", ".jpeg"));
    private static final Set<String> VALID_TEXT_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(".txt", ".md"));

    private static final String EXERCISE_BANK_FOLDER_PATH = "exercise_bank"; // relative path
    private static final String STRENGTH_PROGRAMS_FOLDER_PATH = "strength_programs";
    private static final String CARDIO_PROGRAMS_FOLDER_PATH = "cardio_programs";

    private final Application application;
    private final AssetManager mAssetManager;
    public AssetsViewModel(@NonNull Application application) {
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
            String illustrationFileName = "";
            String descriptionFileName = "";

            if (VALID_IMAGE_FILE_EXTENSIONS.contains(getFileExtension(exerciseFiles[0]))) {
                illustrationFileName = path + File.separator + exerciseFiles[0];
            }
            else if (VALID_IMAGE_FILE_EXTENSIONS.contains(getFileExtension(exerciseFiles[1]))){
                illustrationFileName = path + File.separator + exerciseFiles[1];
            }

            if (VALID_TEXT_FILE_EXTENSIONS.contains(getFileExtension(exerciseFiles[0]))) {
                descriptionFileName = path + File.separator + exerciseFiles[0];
            }
            else if (VALID_TEXT_FILE_EXTENSIONS.contains(getFileExtension(exerciseFiles[1]))){
                descriptionFileName = path + File.separator + exerciseFiles[1];
            }

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
