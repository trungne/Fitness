package com.main.fitness.ui.activities.gym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.Query;
import com.main.fitness.R;
import com.main.fitness.data.Model.Program;
import com.main.fitness.data.ViewModel.ProgramViewModel;

public class ProgramListActivity extends AppCompatActivity {

    private RecyclerView programListRecycleView;
    private ProgramViewModel programViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_list);
        this.programListRecycleView = findViewById(R.id.ProgramListRecycleView);
        this.programViewModel = new ViewModelProvider(this).get(ProgramViewModel.class);

        PagingConfig config = new PagingConfig(20, 10, false);
        FirestorePagingOptions<Program> options = new FirestorePagingOptions.Builder<Program>()
                .setLifecycleOwner(this)
                .setQuery(this.programViewModel.getBaseQuery(), config, Program.class)
                .build();

        FirestorePagingAdapter<Program, ProgramViewHolder> adapter =
                new FirestorePagingAdapter<Program, ProgramViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProgramViewHolder holder, int position, @NonNull Program model) {
                        holder.programName.setText(model.getOverview());
                    }

                    @NonNull
                    @Override
                    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                        View view = inflater.inflate(R.layout.row_program, parent, false);
                        return new ProgramViewHolder(view);
                    }
                };
        this.programListRecycleView.setLayoutManager(new LinearLayoutManager(this));
        this.programListRecycleView.setAdapter(adapter);

    }

    protected static class ProgramViewHolder extends RecyclerView.ViewHolder {
        public final TextView programName;
        public ProgramViewHolder(@NonNull View itemView) {
            super(itemView);
            this.programName = itemView.findViewById(R.id.rowProgramName);
        }
    }

}