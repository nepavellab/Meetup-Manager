package com.example.project_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Objects;
import io.github.muddz.styleabletoast.StyleableToast;

public class Registration extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        database = FirebaseDatabase.getInstance();
    }

    public void createAdmin(View view) {
        String email = ((EditText)findViewById(R.id.new_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.new_password)).getText().toString();
        String full_name = ((EditText)findViewById(R.id.admin_name)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        StyleableToast.makeText(this, "Регистрация прошла успешно", R.style.valid_toast).show();

                        FirebaseUser user = mAuth.getCurrentUser();

                        HashMap<String, Object> userMap = new HashMap<>();
                        assert user != null;
                        userMap.put("user_id", user.getUid());
                        userMap.put("user_name", full_name);
                        userMap.put("user_email", user.getEmail());
                        database.getReference()
                                .child("USERS")
                                .child(Objects.requireNonNull(user.getEmail()).replaceAll("[.#$\\[\\]]", ""))
                                .setValue(userMap);

                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        StyleableToast.makeText(this, "Возникли проблемы при регистрации", R.style.invalid_toast).show();
                    }
                });
    }
}