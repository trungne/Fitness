package com.main.fitness.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.main.fitness.R;
import com.main.fitness.data.Model.UserLevel;

public class ChooseUserLevelDialog extends DialogFragment {
    public ChooseUserLevelDialog(){
        // required empty constructor
    }

    public static ChooseUserLevelDialog newInstance(){
        ChooseUserLevelDialog dialog = new ChooseUserLevelDialog();
        // put args here
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            // get args here
        }
    }

    OnUserLevelChangedListener mListener;

    public void setOnUserLevelChangedListener(@NonNull OnUserLevelChangedListener listener){
        this.mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_choose_user_level, container, false);

        Button beginnerButton = rootView.findViewById(R.id.chooseUserLevelBeginner);
        Button intermediateButton = rootView.findViewById(R.id.chooseUserLevelIntermediate);
        Button advancedButton = rootView.findViewById(R.id.chooseUserLevelAdvanced);

        beginnerButton.setOnClickListener(v -> {
            changeUserLevel(UserLevel.BEGINNER);
        });

        intermediateButton.setOnClickListener(v -> {
            changeUserLevel(UserLevel.INTERMEDIATE);
        });

        advancedButton.setOnClickListener(v -> {
            changeUserLevel(UserLevel.ADVANCED);
        });

        return rootView;
    }

    private void changeUserLevel(@NonNull UserLevel newUserLevel){
        if (this.mListener == null || this.getDialog() == null){
            return;
        }

        this.mListener.onUserLevelChanged(getDialog(), newUserLevel);
    }

    public interface OnUserLevelChangedListener {
        void onUserLevelChanged(Dialog dialog, UserLevel newUserLevel);
    }
}
