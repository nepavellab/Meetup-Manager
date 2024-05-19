package com.example.project_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity {
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    StyleableToast.makeText(this, "Сканирование прошло успешно", R.style.valid_toast).show();
                }
            });


    public void qrRead(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Поднесите QR к камере");
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setBeepEnabled(false);
        barcodeLauncher.launch(options);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserAuthorization();

        setContentView(R.layout.activity_main);

        TextView enter = findViewById(R.id.enter_view);
        TextView register = findViewById(R.id.register_view);

        enter.setOnClickListener(view -> { enter(); });
        register.setOnClickListener(view -> { registerUser(); });
    }

    private void enter() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String email = ((EditText)findViewById(R.id.edit_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.edit_password)).getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            StyleableToast.makeText(this, "Не все поля заполнены", R.style.invalid_toast).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                         startActivity(new Intent(this, PersonalAccount.class));
                         finish();
                    } else {
                        StyleableToast.makeText(this, "Неверный логин или пароль", R.style.invalid_toast).show();
                    }
                });
    }

    private void registerUser() {
        startActivity(new Intent(this, Registration.class));
        finish();
    }

    private void checkUserAuthorization() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, PersonalAccount.class));
            finish();
        }
    }
}