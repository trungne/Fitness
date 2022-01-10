package com.main.fitness.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;


import com.main.fitness.R;
import com.main.fitness.data.Model.RunningRecord;

import java.util.List;

public class RecordListAdapter extends ArrayAdapter<RunningRecord> {
    private List<RunningRecord> runningRecords;
    private Context context;

    public RecordListAdapter(List<RunningRecord> runningRecords, Context ctx){
        super(ctx, R.layout.run_record_list, runningRecords);
        this.runningRecords = runningRecords;
        this.context = ctx;

    }
    //return needed data of a site in the layout of list view
    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( convertView == null){
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.run_record_list, parent, false);
        }
        RunningRecord recordItem = runningRecords.get(position);
        if( convertView != null){

            TextView startDate = convertView.findViewById(R.id.startDateTimeValue);
            startDate.setText(recordItem.getTime()+"");

            TextView finishDate = convertView.findViewById(R.id.finishDateTimeValue);
            finishDate.setText(recordItem.getFinishTime()+"");

            TextView duration = convertView.findViewById(R.id.durationValue);
            duration.setText(recordItem.getDuration()+"");

            TextView steps = convertView.findViewById(R.id.stepsValue);
            steps.setText("("+recordItem.getStep()+" steps)");

            TextView track = convertView.findViewById(R.id.trackValue);
            track.setText(recordItem.getTravelledDistance()+"/"+recordItem.getTotalDistance());

            TextView condition = convertView.findViewById(R.id.booleanValue);

            condition.setText(recordItem.getIsTrackCompleted()+"");
            if(recordItem.getIsTrackCompleted()){
                condition.setTextColor( R.color.green_main);
            }else{
                condition.setTextColor( R.color.red_100);
            }
        }
        return convertView;
    }
}
