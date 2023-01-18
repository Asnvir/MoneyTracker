package com.example.moneytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText signup_TXT_email;
    EditText signup_TXT_password;
    Button signup_BTN_signup;
    TextView signin_TXT_already_acoount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
        registerListeners();
    }

    private void findViews() {
        signup_TXT_email = findViewById(R.id.signup_TXT_email);
        signup_TXT_password = findViewById(R.id.signup_TXT_password);
        signup_BTN_signup = findViewById(R.id.signup_BTN_signup);
        signin_TXT_already_acoount = findViewById(R.id.signin_TXT_already_acoount);
    }

    private void registerListeners() {
        registerTransferToSignIN();
        registerLogin();
    }

    private void registerTransferToSignIN() {
        signin_TXT_already_acoount.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            finish();
            startActivity(intent);
        });
    }

    private void registerLogin() {
        firebaseAuth = FirebaseAuth.getInstance();
        signup_BTN_signup.setOnClickListener(v -> {
            String email = signup_TXT_email.getText().toString();
            String password = signup_TXT_password.getText().toString();
            if (!validateInputs(email, password)) {
                return;
            }
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> Toast.makeText(SignUpActivity.this, "User created.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private boolean validateInputs(String email, String password) {
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password);
    }



}