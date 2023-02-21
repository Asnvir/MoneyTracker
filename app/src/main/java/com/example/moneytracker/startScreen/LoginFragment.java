package com.example.moneytracker.startScreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
        googleSignInWrapper = new GoogleSignInWrapper(requireActivity(), this,viewModel);
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

        binding.loginBTNLogin.setOnClickListener(v -> login());
        viewModel.getAuthResultLiveData().observe(getViewLifecycleOwner(), myAuthResult -> {
            if (myAuthResult.isSuccess()) {
                viewModel.onAuthComplete();
                navigatorStart.navigateToDashboard();
            } else {
                Toast.makeText(requireContext(), myAuthResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.loginTXTForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());

        binding.loginBTNLoginGoogle.setOnClickListener(v -> {
            Log.d("SignInFragment", "Google sign-in button clicked");
            googleSignInWrapper.signIn(googleSignInLauncher);
        });

        binding.loginTXTRegister.setOnClickListener(v -> navigatorStart.navigateToRegister());

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.loginPGBProgressbar.setVisibility(View.VISIBLE);
            } else {
                binding.loginPGBProgressbar.setVisibility(View.GONE);
            }
        });
    }


    private void login() {
        String email = binding.loginTXTEmail.getText().toString();
        String password = binding.loginTXTPassword.getText().toString();
        if (!validateInputs(email, password)) {
            return;
        }
        viewModel.signIn(email, password);
    }

    private boolean validateInputs(String email, String password) {
        return email.trim().length() > 0 && password.trim().length() > 0;
    }


    @Override
    public void onSignInSuccess(String idToken) {
        Toast.makeText(getActivity(), "Sign-in SUCCESS", Toast.LENGTH_SHORT).show();
        Log.d("MyTag", "Sign-in SUCCESS");
        navigatorStart.navigateToDashboard();
    }

    @Override
    public void onSignInError(int statusCode) {
        MySignal.getInstance().toast("Sign-in failed: " + statusCode);
    }

    @Override
    public void onSignOut() {
        MySignal.getInstance().toast("You have been signed out");
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

