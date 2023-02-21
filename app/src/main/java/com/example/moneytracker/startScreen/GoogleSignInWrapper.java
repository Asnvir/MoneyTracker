package com.example.moneytracker.startScreen;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;

import com.example.moneytracker.util.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleSignInWrapper {
    public interface GoogleSignInResultCallback {
        void onSignInSuccess(String idToken);
        void onSignInError(int statusCode);
        void onSignOut();
    }

    private GoogleSignInClient googleSignInClient;
    private Activity activity;
    private GoogleSignInResultCallback googleSignInResultCallback;
    private LoginViewModel loginViewModel;

    public GoogleSignInWrapper(Activity activity, GoogleSignInResultCallback googleSignInResultCallback, LoginViewModel loginViewModel) {

        this.activity = activity;
        this.googleSignInResultCallback = googleSignInResultCallback;
        this.loginViewModel = loginViewModel;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.GOOGLE_SIGN_IN_WEB_CLIENT_ID) // set the web client ID from your Google Cloud Console project
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void signIn(ActivityResultLauncher<Intent> launcher) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        Log.d(Constants.GoogleSignInWrapper_TAG, "Sign-in intent created"); // TODO: 19.02.2023
        launcher.launch(signInIntent);
    }

    public void signOut() {
        googleSignInClient.signOut().addOnCompleteListener(task -> {

            Log.d(Constants.GoogleSignInWrapper_TAG, "GOOGLE SIGNOUT CALLED");// TODO: 19.02.2023
            googleSignInResultCallback.onSignOut();

        });
    }

    public void onStopSignOut() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account != null) {
            signOut();
        }
    }


    public void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String idToken = account.getIdToken();
            loginViewModel.signInWithGoogle(idToken); // delegate sign-in with Google to the SignInViewModel
        } catch (ApiException e) {
            googleSignInResultCallback.onSignInError(e.getStatusCode());
        }
    }

    public void connect() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account != null) {
            signOut();
        }
    }

    public void disconnect() {
        googleSignInClient.revokeAccess().addOnCompleteListener(task -> {
            googleSignInResultCallback.onSignOut();
        });
    }
}

