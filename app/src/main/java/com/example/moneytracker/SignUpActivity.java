package com.example.moneytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity implements OnUserCreationListener  {

    private EditText txt_email;
    private EditText txt_password;
    private Button btn_signup;
    private TextView txt_already_account;
    private UserService userService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
        registerListeners();
        userService = new UserService(this);
    }

    private void findViews() {
        txt_email = findViewById(R.id.signup_TXT_email);
        txt_password = findViewById(R.id.signup_TXT_password);
        btn_signup = findViewById(R.id.signup_BTN_signup);
        txt_already_account = findViewById(R.id.signin_TXT_already_acoount);
    }

    private void registerListeners() {
        listenerTransferToSignIN();
        listenerSINGUP();
    }

    private void listenerTransferToSignIN() {
        txt_already_account.setOnClickListener(v -> navigateToSignInActivity(SignUpActivity.this));
    }

    private void navigateToSignInActivity(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        finish();
        startActivity(intent);
    }

    private void listenerSINGUP() {
        btn_signup.setOnClickListener(this::createUser);
    }

    private void createUser(View v) {
        String email = txt_email.getText().toString();
        String password = txt_password.getText().toString();
        if (!validateInputs(email, password)) {
            return;
        }
        userService.createUser(email, password);
    }

    private boolean validateInputs(String email, String password) {
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password);
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        openDashBoardActivity();
    }

    private void openDashBoardActivity() {
        Intent intent = new Intent(SignUpActivity.this,DashBoardActivity.class);
        finish();
        startActivity(intent);
    }


}