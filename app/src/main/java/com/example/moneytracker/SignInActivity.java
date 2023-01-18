package com.example.moneytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    EditText signin_TXT_email;
    EditText signin_TXT_password;
    Button signin_BTN_login;
    TextView signin_TXT_forgot_password;
    TextView signin_TXT_signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        findViews();
        registerListeners();
    }

    private void findViews() {
        signin_TXT_email = findViewById(R.id.signin_TXT_email);
        signin_TXT_password = findViewById(R.id.signin_TXT_password);
        signin_BTN_login = findViewById(R.id.signin_BTN_login);
        signin_TXT_forgot_password = findViewById(R.id.signin_TXT_forgot_password);
        signin_TXT_signup = findViewById(R.id.signin_TXT_signup);
    }

    private void registerListeners() {
        registerTransferToSignUP();
        registerLogin();
    }

    private void registerTransferToSignUP() {
        signin_TXT_signup.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            finish();
            startActivity(intent);
        });
    }

    private void registerLogin() {
        firebaseAuth = FirebaseAuth.getInstance();
        signin_BTN_login.setOnClickListener(v -> {
            String email = signin_TXT_email.getText().toString();
            String password = signin_TXT_password.getText().toString();
            if (!validateInputs(email, password)) {
                return;
            }
            signIn(email, password);
        });
    }

    private boolean validateInputs(String email, String password) {
        return email.trim().length() > 0 && password.trim().length() > 0;
    }

    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    openDashBoardActivity();
                })
                .addOnFailureListener(e -> Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void openDashBoardActivity() {
        Intent intent = new Intent(SignInActivity.this,DashBoardActivity.class);
        finish();
        startActivity(intent);
    }


}

