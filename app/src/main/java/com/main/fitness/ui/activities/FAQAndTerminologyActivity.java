package com.main.fitness.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.main.fitness.R;
import com.main.fitness.data.ViewModel.AssetsViewModel;
import com.main.fitness.data.ViewModel.UserViewModel;
import com.main.fitness.ui.adapters.FAQAdapter;

public class FAQAndTerminologyActivity extends AppCompatActivity {

    private ImageButton backButton;
    private RecyclerView recyclerView;
    private AssetsViewModel assetsViewModel;
    private UserViewModel userViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_and_terminology);

        backButton = findViewById(R.id.activityFAQBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.FAQActivityRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assetsViewModel = new ViewModelProvider(this).get(AssetsViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.checkIfHasReadFAQ().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful() || !task.getResult().exists()){
                    userViewModel.readFAQ();
                }
            }
        });
        assetsViewModel.getFAQs().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                FAQAdapter adapter = new FAQAdapter(task.getResult());
                recyclerView.setAdapter(adapter);
            }
            else{
                Toast.makeText(this, "Error! Cannot get FAQs", Toast.LENGTH_SHORT).show();
            }
        });
    }
}