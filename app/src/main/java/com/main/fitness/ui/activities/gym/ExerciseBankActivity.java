package com.main.fitness.ui.activities.gym;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.fitness.R;
import com.main.fitness.ui.adapters.ViewPagerAdapterForFragments;
import com.main.fitness.ui.fragments.CardioExerciseFragment;
import com.main.fitness.ui.fragments.WeightExerciseFragment;

import java.util.Arrays;

public class ExerciseBankActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_bank);
        this.tabLayout = findViewById(R.id.exerciseBankTabLayout);
        this.viewPager = findViewById(R.id.exerciseBankViewPager);
        ViewPagerAdapterForFragments viewPagerAdapterForFragments = new ViewPagerAdapterForFragments(this, Arrays.asList(WeightExerciseFragment.newInstance(), CardioExerciseFragment.newInstance()));
        this.viewPager.setAdapter(viewPagerAdapterForFragments);
        new TabLayoutMediator(this.tabLayout, this.viewPager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Weight");
                    break;
                case 1:
                    tab.setText("Cardio");
                    break;
            }
        }).attach();
    }



}