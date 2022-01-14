package com.main.fitness.ui.adapters;


import android.content.Context;
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
    private OnViewWithFilePathClickListener mListener;
    private String currentProgramName;
    private Context context;

    public WorkoutProgramAdapter(Context context, List<WorkoutProgram> workoutProgramList, String currentProgramName){
        this.context = context;
        this.workoutProgramList = workoutProgramList;
        this.currentProgramName = currentProgramName;
    }

    public void setOnViewClickListener(OnViewWithFilePathClickListener listener){
        this.mListener = listener;
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
        if (workoutProgram.getName().equals(this.currentProgramName)){
            holder.programName.setTextColor(context.getResources().getColor(R.color.green_main, context.getTheme()));
            holder.programName.setText("");
            holder.programName.setBackgroundResource(R.drawable.custom_background2);
            holder.programBanner.setAlpha(0.8f);
        }
        else{
            holder.programName.setBackgroundColor(context.getResources().getColor(R.color.blur, context.getTheme()));
        }
        holder.programBanner.setOnClickListener(v -> {
            if (this.mListener == null){
                return;
            }

            this.mListener.onViewClicked(workoutProgram.getFolderPath());
        });
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
