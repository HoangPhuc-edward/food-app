package com.example.foodapp.Activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.foodapp.Listener.NavigateListener;
import com.example.foodapp.Fragment.NotifyDialogFragment;
import com.example.foodapp.Utils.DialogHelper;
import com.example.foodapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends BaseActivity implements NavigateListener {
    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setVariable();

    }

    private void setVariable() {
        binding.loginBtn.setOnClickListener(view -> {
            String email = binding.userEdit.getText().toString();
            String password = binding.passEdit.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()){

                if (!isValidEmail(email)){
                    NotifyDialogFragment notifyDialogFragment = new NotifyDialogFragment("Email error", "Your email is not in a valid format");
                    notifyDialogFragment.show(getSupportFragmentManager(), "email");
                    return;
                }

                if (password.length() < 6){
                    NotifyDialogFragment notifyDialogFragment = new NotifyDialogFragment("Password error", "Your password must be longer than 6 characters");
                    notifyDialogFragment.show(getSupportFragmentManager(), "email");
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        else {
                            DialogHelper.showNotifyPopup(LoginActivity.this, "Login failed", "Email or password is not correct!", "error");
                        }
                    }
                }).addOnCanceledListener(() -> DialogHelper.showNotifyPopup(LoginActivity.this, "Login failed", "Failed to log in your account", "error"));
            }
            else {
                NotifyDialogFragment notifyDialogFragment = new NotifyDialogFragment("Information is not enough", "You have not entered your email and password");
                notifyDialogFragment.show(getSupportFragmentManager(), "email");
            }
        });

        binding.signupNavigateBtn.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    @Override
    public void onClick() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}