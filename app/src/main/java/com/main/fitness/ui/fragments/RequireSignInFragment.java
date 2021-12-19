package com.main.fitness.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.main.fitness.R;
import com.main.fitness.ui.activities.DashboardActivity;
import com.main.fitness.ui.activities.MainActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class RequireSignInFragment extends Fragment {
    private static final String TAG = "RequireSignInFragment";

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    public RequireSignInFragment() {
        // Required empty public constructor
    }

    public static RequireSignInFragment newInstance() {
        RequireSignInFragment fragment = new RequireSignInFragment();
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

    private Button signInButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_require_sign_in, container, false);
        this.signInButton =  view.findViewById(R.id.SignInFragmentSignInButton);
        this.signInButton.setOnClickListener(v -> {
            createSignInIntent();
        });
        return view;
    }



    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.MainActivityFragmentContainer,
                            UserFragment.newInstance())
                    .commit();
            return;
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            if (response == null) {
                return;
            }
            if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(requireActivity(), "No internet", Toast.LENGTH_SHORT).show();
            } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                Toast.makeText(requireActivity(), String.valueOf(response.getError().getErrorCode()), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void createSignInIntent() {
        Log.d(TAG, "createSignInIntent: called");
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.login_logo)
                .build();
        signInLauncher.launch(signInIntent);
    }


}