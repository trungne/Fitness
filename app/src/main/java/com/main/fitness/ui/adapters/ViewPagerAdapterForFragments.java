package com.main.fitness.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ViewPagerAdapterForFragments<T extends Fragment> extends FragmentStateAdapter {
    private final List<T> fragmentList;

    public ViewPagerAdapterForFragments(@NonNull FragmentActivity fragmentActivity, @NonNull List<T> fragmentList) {
        super(fragmentActivity);
        this.fragmentList = fragmentList;

        if (this.fragmentList.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
