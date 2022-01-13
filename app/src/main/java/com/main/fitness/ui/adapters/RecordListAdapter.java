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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RecordListAdapter extends ArrayAdapter<RunningRecord> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");


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
            startDate.setText(recordItem.getStartTime()+"");

            TextView finishDate = convertView.findViewById(R.id.finishDateTimeValue);

            // convert the string to LocalDateTime and format it according to the DATE_TIME_FORMATTER
            LocalDateTime endTime = LocalDateTime.parse(recordItem.getFinishTime());
            String endTimeString = endTime.format(DATE_TIME_FORMATTER); // set this to text view

            finishDate.setText(recordItem.getFinishTime()+"");

            LocalDateTime start = LocalDateTime.parse(recordItem.getStartTime());
            LocalDateTime finish = LocalDateTime.parse(recordItem.getFinishTime());

            long seconds = ChronoUnit.SECONDS.between(start, finish) % 60;

            // only get the minutes
            long minutes = ChronoUnit.MINUTES.between(start, finish) % 60;
            // no need for modular % 24, I don't think anyone is gonna use this app for more than 24 hours
            // if they did, let them crash the app, they deserve it
            long hours = ChronoUnit.HOURS.between(start, finish);


            String secondsText = seconds + (seconds == 1 ? " Second" : " Seconds");

            String hoursText = "";
            if (hours >= 1){
                hoursText = hours + (hours == 1 ? " Hour " : " Hours ");
            }

            String minutesText = "";
            if (minutes >= 1){
                minutesText = minutes + (minutes == 1 ? " Minute " : " Minutes ");
            }

            String durationText = hoursText + minutesText + secondsText;

            TextView duration = convertView.findViewById(R.id.durationValue);
            duration.setText(durationText+"");
            // calculate duration from start end end

            TextView steps = convertView.findViewById(R.id.stepsValue);
            steps.setText("("+recordItem.getSteps()+" steps)");

            TextView track = convertView.findViewById(R.id.trackValue);
            track.setText(recordItem.getDistance()+ " m");

        }
        return convertView;
    }
}
