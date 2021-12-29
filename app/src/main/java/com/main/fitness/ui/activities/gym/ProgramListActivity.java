package com.main.fitness.ui.activities.gym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.main.fitness.R;
import com.main.fitness.data.GlideApp;
import com.main.fitness.data.Model.Program;
import com.main.fitness.data.MyAppGlideModule;
import com.main.fitness.data.ViewModel.ProgramViewModel;

public class ProgramListActivity extends AppCompatActivity {
    // this key is used to indicate whether user has selected to view strength or cardio programs
    public static final String PROGRAMS_KEY = "com.main.fitness.ui.activities.gym.ProgramListActivity.programKey";

    public static final String STRENGTH_PROGRAMS = "STRENGTH_PROGRAMS";
    public static final String CARDIO_PROGRAMS = "CARDIO_PROGRAMS";

    private RecyclerView programListRecycleView;
    private ProgramViewModel programViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_list);
        this.programListRecycleView = findViewById(R.id.ProgramListRecycleView);
        this.programViewModel = new ViewModelProvider(this).get(ProgramViewModel.class);

        Intent intent = getIntent();
        if (intent == null || TextUtils.isEmpty(intent.getStringExtra(PROGRAMS_KEY))){
            finish();
            return;
        }

        Query baseQuery;
        if (intent.getStringExtra(PROGRAMS_KEY).equals(CARDIO_PROGRAMS)){
            baseQuery = this.programViewModel.getBaseQueryForCardioPrograms();
        }
        else if (intent.getStringExtra(PROGRAMS_KEY).equals(STRENGTH_PROGRAMS)){
            baseQuery = this.programViewModel.getBaseQueryForStrengthPrograms();
        }
        else{
            finish();
            return;
        }

        PagingConfig config = new PagingConfig(5, 10, false);
        FirestorePagingOptions<Program> options = new FirestorePagingOptions.Builder<Program>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, Program.class)
                .build();
        // recycle view:
        // 1. Adapter (firebase)
        // 2. ViewHolder (inner class of Adapter)
        FirestorePagingAdapter<Program, ProgramViewHolder> adapter =
                new FirestorePagingAdapter<Program, ProgramViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProgramViewHolder holder, int position, @NonNull Program program) {
                        holder.programName.setText(program.getName());
                        String imagePath = program.getImagePath();
                        Log.i("ProgramListActivity", imagePath);
                        if (!TextUtils.isEmpty(imagePath)){
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);
                            GlideApp.with(getApplicationContext()).load(storageReference).into(holder.banner);
                        }

                        holder.banner.setOnClickListener(v -> {
                            Intent intent = new Intent(getApplicationContext(), ProgramDetailActivity.class);
                            intent.putExtra(ProgramDetailActivity.PROGRAM_ID_KEY, program.getId());

                            startActivity(intent);
                        });
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
        public final ImageView banner;
        public ProgramViewHolder(@NonNull View itemView) {
            super(itemView);
            this.programName = itemView.findViewById(R.id.rowProgramName);
            this.banner = itemView.findViewById(R.id.rowProgramBanner);
        }
    }

}