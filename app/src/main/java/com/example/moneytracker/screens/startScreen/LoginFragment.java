package com.example.moneytracker.screens.startScreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneytracker.R;
import com.example.moneytracker.contract.NavigatorStart;
import com.example.moneytracker.databinding.FragmentLoginBinding;
import com.example.moneytracker.util.MySignal;

public class LoginFragment extends Fragment implements GoogleSignInWrapper.GoogleSignInResultCallback {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;
    private NavigatorStart navigatorStart;
    private GoogleSignInWrapper googleSignInWrapper;
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    googleSignInWrapper.handleSignInResult(data);
                }
            }
    );


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        googleSignInWrapper = new GoogleSignInWrapper(requireActivity(), this, viewModel);
        navigatorStart = NavigatorStart.getNavigator(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding
                .inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel = null;
        navigatorStart = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerActions();
        observeViewModel();
    }

    private void registerActions() {
        binding.loginBTNLogin.setOnClickListener(v -> login());

        binding.loginTXTForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());

        binding.loginBTNLoginGoogle.setOnClickListener(v -> googleSignInWrapper.signIn(googleSignInLauncher));

        binding.loginTXTRegister.setOnClickListener(v -> navigatorStart.navigateToRegister());
    }

    private void observeViewModel() {

        viewModel.getIsAuth().observe(getViewLifecycleOwner(), value -> {
            if (value) {
                navigatorStart.navigateToDashboard();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.loginPGBProgressbar.setVisibility(View.VISIBLE);
            } else {
                binding.loginPGBProgressbar.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                MySignal.getInstance().toast(errorMessage);
            }
        });
    }

    private void login() {
        String email = binding.loginTXTEmail.getText().toString();
        String password = binding.loginTXTPassword.getText().toString();
        viewModel.signIn(email, password);
    }


    @Override
    public void onSignInSuccess(String idToken) {
        navigatorStart.navigateToDashboard();
    }

    @Override
    public void onSignInError(int statusCode) {

    }

    @Override
    public void onSignOut() {

    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Forgot Password");
        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        EditText emailEditText = view.findViewById(R.id.forgot_password_email_edittext);
        builder.setView(view);


        builder.setPositiveButton("Submit", (dialogInterface, i) -> {
            String email = emailEditText.getText().toString();
            if (!email.isEmpty()) {
                viewModel.sendPasswordResetEmail(email);
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleSignInWrapper.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleSignInWrapper.disconnect();
    }
}

