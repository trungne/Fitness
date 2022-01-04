package com.main.fitness.ui.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.fitness.R;
import com.main.fitness.data.Model.Exercise;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseHolder> {
    private static final String TAG = "ExerciseAdapter";
    private List<Exercise> exerciseList;
    private OnExerciseClickListener mListener = null;

    public ExerciseAdapter(List<Exercise> exerciseList){
        this.exerciseList = exerciseList;
    }

    public void setOnExerciseClickListener(OnExerciseClickListener listener){
        this.mListener = listener;
    }

    public void updateExerciseList(List<Exercise> exerciseList){
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public ExerciseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_exercise, parent, false);
        return new ExerciseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseHolder holder, int position) {
        Exercise exercise = this.exerciseList.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.exerciseIllustration.setImageDrawable(exercise.getIllustration());
        holder.exerciseIllustration.setOnClickListener(v -> {
            if (this.mListener != null){
                this.mListener.onExerciseClick(exercise.getFolderPath());
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    static class ExerciseHolder extends RecyclerView.ViewHolder {
        public TextView exerciseName;
        public ImageView exerciseIllustration;
        public ExerciseHolder(@NonNull View itemView) {
            super(itemView);
            this.exerciseName = itemView.findViewById(R.id.rowExerciseName);
            this.exerciseIllustration = itemView.findViewById(R.id.rowExerciseImage);
        }
    }

    public interface OnExerciseClickListener {
        public void onExerciseClick(String folderPath);
    }
}
