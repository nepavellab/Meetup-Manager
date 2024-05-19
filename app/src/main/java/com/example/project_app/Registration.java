package com.example.project_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import io.github.muddz.styleabletoast.StyleableToast;

public class Registration extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void createAdmin(View view) {
        String email = ((EditText)findViewById(R.id.new_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.new_password)).getText().toString();
        String full_name = ((EditText)findViewById(R.id.admin_name)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                    StyleableToast.makeText(this, "Регистрация прошла успешно", R.style.valid_toast).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        StyleableToast.makeText(this, "Возникли проблемы при регистрации", R.style.invalid_toast).show();
                    }
                });
    }
}