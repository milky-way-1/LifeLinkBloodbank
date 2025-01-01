package com.example.lifelinkbloodbank;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lifelinkbloodbank.api.RetrofitClient;
import com.example.lifelinkbloodbank.model.MessageResponse;
import com.example.lifelinkbloodbank.model.SignupRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import retrofit2.Response;

public class Signup extends AppCompatActivity {

    private TextInputEditText fullNameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton signupButton;
    private TextView loginText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Set status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blood_red));

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signupButton = findViewById(R.id.signupButton);
        loginText = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progressBar);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupClickListeners() {
        signupButton.setOnClickListener(v -> {
            if (validateInputs()) {
                performSignup();
            }
        });

        loginText.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate full name
        if (fullName.isEmpty()) {
            fullNameInput.setError("Full name is required");
            isValid = false;
        }

        // Validate email
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            isValid = false;
        }

        // Validate password
        if (password.isEmpty() || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            isValid = false;
        }

        // Validate confirm password
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private void performSignup() {
        showLoading();

        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        SignupRequest request = new SignupRequest(fullName, email, password, "Blood");


    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        signupButton.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        signupButton.setEnabled(true);
    }

    private void showSuccessDialog(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("Login", (dialog, which) -> {
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                })
                .setCancelable(false)
                .show();
    }

    private void showError(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}