package com.main.fitness.data;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {
    private static final Set<String> VALID_IMAGE_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(".png", ".jpg", ".jpeg"));
    private static final Set<String> VALID_TEXT_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(".txt", ".md"));

    public static String getFileExtension(String filename){
        return filename.substring(filename.lastIndexOf('.'));
    }

    public static String getTextFilePath(AssetManager am, String folderPath){
        try {
            String[] files = am.list(folderPath);
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

    public static String getImageFilePath(AssetManager am, String folderPath){
        try {
            String[] files = am.list(folderPath);
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

    public static String getStringFromFile(AssetManager am, String filePath){
        try {
            InputStream inputStream = am.open(filePath);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            return new String(buffer);
        } catch (IOException e) {
            return "";
        }
    }

    public static Drawable getDrawableFromFile(AssetManager am, String filePath){
        try {
            InputStream inputStream = am.open(filePath);
            return Drawable.createFromStream(inputStream, null);
        } catch (IOException e){
            return null;
        }
    }

    public static String getJSONFilePath(AssetManager am, String folderPath){
        try {
            String[] files = am.list(folderPath);
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

    public static String getFilename(String path){
        if (TextUtils.isEmpty(path)){
            return "";
        }
        int lastIndexOfFileSeparator = path.lastIndexOf(File.separator);

        if (lastIndexOfFileSeparator == -1){
            return "";
        }
        return path.substring(lastIndexOfFileSeparator + 1);

    }

}
