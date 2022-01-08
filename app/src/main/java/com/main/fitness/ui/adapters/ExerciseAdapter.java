package com.main.fitness.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutExercise;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseHolder> {
    private static final String TAG = "ExerciseAdapter";
    private List<WorkoutExercise> workoutExerciseList;
    private OnViewWithFilePathClickListener mListener = null;

    public ExerciseAdapter(List<WorkoutExercise> workoutExerciseList){
        this.workoutExerciseList = workoutExerciseList;
    }

    public void setOnExerciseClickListener(OnViewWithFilePathClickListener listener){
        this.mListener = listener;
    }

    public void updateExerciseList(List<WorkoutExercise> workoutExerciseList){
        this.workoutExerciseList = workoutExerciseList;
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
        WorkoutExercise workoutExercise = this.workoutExerciseList.get(position);
        holder.exerciseName.setText(workoutExercise.getName());
        holder.exerciseIllustration.setImageDrawable(workoutExercise.getIllustration());
        holder.exerciseIllustration.setOnClickListener(v -> {
            if (this.mListener != null){
                this.mListener.onViewClicked(workoutExercise.getFolderPath());
            }
        });
    }

    @Override
    public int getItemCount() {
        return workoutExerciseList.size();
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

}
