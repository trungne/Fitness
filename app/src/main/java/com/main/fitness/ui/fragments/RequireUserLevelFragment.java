package com.main.fitness.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.main.fitness.R;
import com.main.fitness.data.Model.AppUser;
import com.main.fitness.data.ViewModel.UserViewModel;
import com.main.fitness.ui.dialogs.ChooseUserLevelDialog;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequireUserLevelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequireUserLevelFragment extends Fragment {
    private static final String TAG = "GymFragment";
    public RequireUserLevelFragment() {
        // Required empty public constructor
    }

    public static RequireUserLevelFragment newInstance() {
        RequireUserLevelFragment fragment = new RequireUserLevelFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    private UserViewModel userViewModel;
    private String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        if (!this.userViewModel.isLoggedIn()){
            getParentFragmentManager().beginTransaction().replace(R.id.MainActivityFragmentContainer, RequireSignInFragment.newInstance()).commit();
            return;
        }

        this.userViewModel.getUser(this.userViewModel.getFirebaseUser().getUid()).addOnCompleteListener(requireActivity(), task -> {
            if (!task.isSuccessful() || task.getResult() == null){
                Toast.makeText(requireActivity(), "Cannot load user's information", Toast.LENGTH_SHORT).show();
                return;
            }
            AppUser appUser = task.getResult();
            this.uid = appUser.getUid();
            if (appUser.getUserLevel() == null){
                askUserToUpdateLevel();
            }
        });
    }

    private void askUserToUpdateLevel(){
        ChooseUserLevelDialog chooseUserLevelDialog = ChooseUserLevelDialog.newInstance();
        chooseUserLevelDialog.setOnUserLevelChangedListener((dialog, newUserLevel) -> {
            HashMap<String, Object> newData = new HashMap<>();
            newData.put(UserViewModel.USER_LEVEL_FIELD, newUserLevel);
            this.userViewModel.updateAppUser(this.uid, newData).addOnCompleteListener(requireActivity(), updateTask -> {
                if (updateTask.isSuccessful()){
                    getParentFragmentManager().beginTransaction().replace(R.id.MainActivityFragmentContainer, GymFragment.newInstance()).commit();
                    dialog.dismiss();
                    Toast.makeText(requireActivity(), "Level updated successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(requireActivity(), "Failed to update!", Toast.LENGTH_SHORT).show();
                }
            });
        });
        chooseUserLevelDialog.show(getParentFragmentManager(), TAG);
    }

    private Button chooseLevelButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_require_user_level, container, false);
        this.chooseLevelButton = rootView.findViewById(R.id.RequireUserLevelFragmentChooseLevelButton);
        this.chooseLevelButton.setOnClickListener(v -> {askUserToUpdateLevel();});
        return rootView;
    }
}