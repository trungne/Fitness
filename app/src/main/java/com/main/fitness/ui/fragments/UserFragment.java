package com.main.fitness.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.main.fitness.R;
import com.main.fitness.data.Model.AppUser;
import com.main.fitness.data.ViewModel.UserViewModel;
import com.main.fitness.ui.dialogs.ChooseUserLevelDialog;

import java.util.HashMap;


public class UserFragment extends Fragment {
    private static final String TAG = "UserFragment";
    public UserFragment() {
        // Required empty public constructor
    }


    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }


    }

    private Button signOutButton;
    private UserViewModel userViewModel;
    private TextView setLevelTextView, userScore;
    private EditText userDisplayName,userEmail,userPhone;
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_user, container, false);
        this.signOutButton = this.rootView.findViewById(R.id.UserFragmentSignoutButton);
        this.signOutButton.setOnClickListener(this::signOut);
        this.userViewModel = new ViewModelProvider(this)
                .get(UserViewModel .class);

        this.setLevelTextView = this.rootView.findViewById(R.id.fragmentUserSetLevelTextView);
        this.setLevelTextView.setOnClickListener(this::setUserLevel);

        this.userScore = this.rootView.findViewById(R.id.fragmentUserScoreValue);
        this.userDisplayName = this.rootView.findViewById(R.id.fragmentUserDisplayNameValue);
        this.userEmail = this.rootView.findViewById(R.id.fragmentUserEmailValue);
        this.userPhone = this.rootView.findViewById(R.id.fragmentUserPhoneValue);




        String uid = this.userViewModel.getFirebaseUser().getUid();
        this.userViewModel.getUser(uid)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (!task.isSuccessful() || task.getResult() == null){
                        //handle
                        return;
                    }

                    AppUser appUser = task.getResult();
                    this.userDisplayName.setText(appUser.getDisplayName());
                    this.userEmail.setText(appUser.getEmail());
                    this.userPhone.setText(appUser.getPhoneNumber());
                    if(appUser.getWorkoutScore() != null){
                        this.userScore.setText(appUser.getWorkoutScore().toString());
                    }

                    // set other attributes
                });

        setMainLayout();
        return this.rootView;
    }

    public void setMainLayout(){
        String uid = this.userViewModel.getFirebaseUser().getUid();
        this.userViewModel.getUser(uid).addOnCompleteListener(requireActivity(), task -> {
            if (!task.isSuccessful() || task.getResult() == null){
                // handle
                return;
            }
            AppUser appUser = task.getResult();

            if (appUser.getUserLevel() == null){
                this.rootView.findViewById(R.id.fragmentUserDashboard)
                        .setVisibility(View.GONE);
                this.rootView.findViewById(R.id.fragmentUserSetLevelLayout)
                        .setVisibility(View.VISIBLE);
            }
            else{
                this.rootView.findViewById(R.id.fragmentUserDashboard)
                        .setVisibility(View.VISIBLE);
                this.rootView.findViewById(R.id.fragmentUserSetLevelLayout)
                        .setVisibility(View.GONE);
            }
        });
    }

    public void setUserLevel(View v){
        ChooseUserLevelDialog dialog = ChooseUserLevelDialog.newInstance();
        // callback: reactive function
        dialog.setOnUserLevelChangedListener((d, newUserLevel) -> {
            String uid = this.userViewModel.getFirebaseUser().getUid();
            HashMap<String, Object> newData = new HashMap<>();
            newData.put(UserViewModel.USER_LEVEL_FIELD, newUserLevel);

            this.userViewModel.updateAppUser(uid, newData).addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        // handle
                        return;
                    }

                    d.dismiss();
                    setMainLayout();
                }
            });
        });

        dialog.show(getParentFragmentManager(), TAG);
    }

    public void signOut(View v){
        AuthUI.getInstance()
                .signOut(requireActivity())
                .addOnCompleteListener(requireActivity(), task -> {
                    if (!task.isSuccessful()){
                        // handle
                        return;
                    }

                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.MainActivityFragmentContainer,
                                    RequireSignInFragment.newInstance())
                            .commit();
                    Toast.makeText(requireActivity(), "User signed out successfully!", Toast.LENGTH_SHORT).show();
                });
    }


}