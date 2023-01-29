package com.example.moneytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.moneytracker.activities.MainActivityHolder;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    EditText txt_email;
    EditText txt_password;
    Button btn_login;
    TextView txt_view_forgot_password;
    TextView txt_view_signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        findViews();
        registerListeners();
    }

    private void findViews() {
        txt_email = findViewById(R.id.signin_TXT_email);
        txt_password = findViewById(R.id.signin_TXT_password);
        btn_login = findViewById(R.id.signin_BTN_login);
        txt_view_forgot_password = findViewById(R.id.signin_TXT_forgot_password);
        txt_view_signup = findViewById(R.id.signin_TXT_signup);
    }

    private void registerListeners() {
        listenerTransferToSignUP();
        listenerLOGIN();
    }

    private void listenerTransferToSignUP() {
        txt_view_signup.setOnClickListener(v -> navigateToSignUpActivity(SignInActivity.this));
    }

    private void navigateToSignUpActivity(Context context) {
        Intent intent = new Intent(context, SignUpActivity.class);
        finish();
        startActivity(intent);
    }

    private void listenerLOGIN() {
        btn_login.setOnClickListener(this::userLOGIN);
    }

    private void userLOGIN(View v) {
        String email = txt_email.getText().toString();
        String password = txt_password.getText().toString();
        if (!validateInputs(email, password)) {
            return;
        }
        signIn(email, password);
    }

    private boolean validateInputs(String email, String password) {
        return email.trim().length() > 0 && password.trim().length() > 0;
    }

    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> openDashBoardActivity())
                .addOnFailureListener(e -> Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void openDashBoardActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivityHolder.class);
        finish();
        startActivity(intent);
    }



}

