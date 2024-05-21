package com.example.project_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity {
    // Обработчик сканирования QR кода
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                // Если информация была считана
                if (result.getContents() != null) {
                    StyleableToast.makeText(this, "Сканирование прошло успешно", R.style.valid_toast).show();
                } else {
                    StyleableToast.makeText(this, "Неудачная попытка сканирования", R.style.invalid_toast).show();
                }
            });


    // Функция настраивает опции сканера и вызывает его
    public void qrRead(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Поднесите QR к камере");
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setBeepEnabled(false);
        barcodeLauncher.launch(options);
    }

    public void authorizationWithGoogle(View view) {

    }

    public void authorizationWithGMail(View view) {

    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Проверить, входил ли пользователь в свой аккаунт
        checkUserAuthorization();

        setContentView(R.layout.activity_main);

        Button enter = findViewById(R.id.enter_view);
        Button register = findViewById(R.id.register_view);
        TextView password_recovery = findViewById(R.id.forget_password);

        password_recovery.setOnClickListener(view -> { recoverPassword(); });
        enter.setOnClickListener(view -> { enter(); });
        register.setOnClickListener(view -> { registerUser(); });
    }

    private void enter() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Получаем введённый почтовый адрес и пароль пользователя
        String email = ((EditText)findViewById(R.id.edit_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.edit_password)).getText().toString();

        // Если пользователь забыл ввести какие-либо данные и некоторые оставил поля пустыми
        if (email.isEmpty() || password.isEmpty()) {
            StyleableToast.makeText(this, "Не все поля заполнены", R.style.invalid_toast).show();
            return;
        }

        // Проверка существования пользователя с указанным почтовым адресом и паролем
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Если пользователь найден в базе
                    if (task.isSuccessful()) {
                        // Перейти в личный кабинет
                        startActivity(new Intent(this, PersonalAccount.class));
                        finish();
                    } else {
                        StyleableToast.makeText(this, "Неверный логин или пароль", R.style.invalid_toast).show();
                    }
                });
    }

    // Функция открывает форму регистрации для пользователя
    private void registerUser() {
        startActivity(new Intent(this, Registration.class));
    }

    // Функция на этапе запуска приложения проверяет, авторизовался ли пользователь ранее
    private void checkUserAuthorization() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Если пользователь уже выполнил вход в аккаунт ранее
        if (mAuth.getCurrentUser() != null) {
            // Перейти в личный кабинет
            startActivity(new Intent(this, PersonalAccount.class));
            finish();
        }
    }

    //  Функция восстановления пароля пользователя
    private void recoverPassword() {
        String email = ((EditText)findViewById(R.id.edit_email)).getText().toString();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (email.isEmpty()) {
            StyleableToast.makeText(this, "Введите адрес электронной почты", R.style.invalid_toast).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        StyleableToast.makeText(this, "Письмо для восстановления пароля почту", R.style.valid_toast).show();
                    } else {
                        StyleableToast.makeText(this, "Что-то пошло не так :(", R.style.invalid_toast).show();
                    }
                });
    }
}