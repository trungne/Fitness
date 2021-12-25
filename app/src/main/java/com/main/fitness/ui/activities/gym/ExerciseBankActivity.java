package com.main.fitness.ui.activities.gym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.main.fitness.R;
import com.main.fitness.ui.fragments.CardioExerciseFragment;
import com.main.fitness.ui.fragments.WeightExerciseFragment;

public class ExerciseBankActivity extends FragmentActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_bank);
        this.tabLayout = findViewById(R.id.exerciseBankTabLayout);
        this.viewPager = findViewById(R.id.exerciseBankViewPager);
        MyTabViewAdapter myTabViewAdapter = new MyTabViewAdapter(this);
        this.viewPager.setAdapter(myTabViewAdapter);
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

    private static class MyTabViewAdapter extends FragmentStateAdapter {
        public MyTabViewAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: {
                    return WeightExerciseFragment.newInstance();
                }
                case 1: {
                    return CardioExerciseFragment.newInstance();
                }
                default: {return null;}
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

}