package com.main.fitness.ui.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.fitness.R;
import com.main.fitness.data.Model.WorkoutProgram;

import java.util.List;

public class WorkoutProgramAdapter extends RecyclerView.Adapter<WorkoutProgramAdapter.WorkoutProgramHolder> {
    private List<WorkoutProgram> workoutProgramList;

    public WorkoutProgramAdapter(List<WorkoutProgram> workoutProgramList){
        this.workoutProgramList = workoutProgramList;
    }

    @NonNull
    @Override
    public WorkoutProgramHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_program, parent, false);
        return new WorkoutProgramHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutProgramHolder holder, int position) {
        WorkoutProgram workoutProgram = this.workoutProgramList.get(position);
        holder.programName.setText(workoutProgram.getName());
        holder.programBanner.setImageDrawable(workoutProgram.getBanner());
    }

    @Override
    public int getItemCount() {
        return this.workoutProgramList.size();
    }

    static class WorkoutProgramHolder extends RecyclerView.ViewHolder {
        public final TextView programName;
        public final ImageView programBanner;

        public WorkoutProgramHolder(@NonNull View itemView) {
            super(itemView);
            this.programName = itemView.findViewById(R.id.rowProgramName);
            this.programBanner = itemView.findViewById(R.id.rowProgramBanner);
        }
    }
}
