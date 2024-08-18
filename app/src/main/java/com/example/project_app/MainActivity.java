package com.example.project_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.util.HashMap;
import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore database;
    private String meetup_hash;
    @SuppressLint("InflateParams")
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(), result -> {
                if (result.getContents() != null) {
                    Intent intent = new Intent(this, ScanResult.class);
                    intent.putExtra("MEET_HASH", meetup_hash);
                    intent.putExtra("QR_STRING", result.getContents());
                    startActivity(intent);
                }
            });

    public void authorizationWithGoogle(View view) {
        GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, googleOptions);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, 123);
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, @Nullable Intent data) {
        super.onActivityResult(request_code, result_code, data);

        if (request_code == 123) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Task<GoogleSignInAccount> singInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = singInTask.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                mAuth.signInWithCredential(credential).addOnSuccessListener(authResult -> {
                    if (singInTask.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        assert user != null;

                        database.collection("USERS")
                                .document(user.getUid())
                                .get()
                                .addOnCompleteListener(task -> {
                                    DocumentSnapshot document = task.getResult();

                                    if (document.exists()) {
                                        startActivity(new Intent(MainActivity.this, PersonalAccount.class));
                                        finish();
                                    } else {
                                        HashMap<String, Object> userMap = new HashMap<>();
                                        userMap.put("user_name", user.getDisplayName());
                                        userMap.put("user_email", user.getEmail());
                                        database.collection("USERS")
                                                .document(user.getUid())
                                                .set(userMap)
                                                .addOnSuccessListener(unused -> {
                                                    startActivity(new Intent(MainActivity.this, PersonalAccount.class));
                                                    finish();
                                                });
                                    }
                                });

                    } else {
                        StyleableToast.makeText(this, "Пользователь с указанной почтой не зарегистрирован", R.style.invalid_toast).show();
                    }
                });
            } catch (ApiException ignored) {

            }
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserAuthorization();

        setContentView(R.layout.activity_main);

        database = FirebaseFirestore.getInstance();

        Button enter = findViewById(R.id.enter_view);
        Button register = findViewById(R.id.register_view);
        Button scan = findViewById(R.id.scan_button);
        TextView password_recovery = findViewById(R.id.forget_password);

        scan.setOnClickListener(view -> qrRead());
        password_recovery.setOnClickListener(view -> recoverPassword());
        enter.setOnClickListener(view -> enter());
        register.setOnClickListener(view -> registerUser());
    }

    @SuppressLint({"ResourceAsColor"})
    private EditText createHashEdit() {
        EditText hash_field = new EditText(this);
        hash_field.setHintTextColor(Color.WHITE);
        hash_field.setHint(R.string.meetup_key_word);
        hash_field.setTextSize(20);
        return hash_field;
    }

    private void scanMode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Поднесите QR к камере");
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setBeepEnabled(false);
        barcodeLauncher.launch(options);
    }

    private void qrRead() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.custom_dialog);
        EditText hash_input = createHashEdit();

        builder.setView(hash_input)
                .setPositiveButton("Ввод", (dialog, which) -> {
                    if (!hash_input.getText().toString().isEmpty()) {
                        meetup_hash = hash_input.getText().toString();
                        scanMode();
                    } else {
                        StyleableToast.makeText(getApplicationContext(), "Ключевое слово обязательно!", R.style.invalid_toast).show();
                    }
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.cancel())
                .create()
                .show();
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
    }

    private void checkUserAuthorization() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, PersonalAccount.class));
            finish();
        }
    }

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