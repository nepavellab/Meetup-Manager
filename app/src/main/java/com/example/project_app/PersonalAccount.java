package com.example.project_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import io.github.muddz.styleabletoast.StyleableToast;
import java.util.Objects;

public class PersonalAccount extends AppCompatActivity {
    private FirebaseFirestore database; // ссылка на базу данных
    private LinearLayout linearLayout; // контейнер, динамически обновляющий карточки пользователя

    // Функция открывает форму для создания мероприятия
    public void createMeet(View view) {
        startActivity(new Intent(this, CreateMeetUp.class));
    }

    public void displayMeets() {
        FirebaseAuth user = FirebaseAuth.getInstance();
        database.collection("USERS")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("MEETS")
                .get().addOnCompleteListener(task -> {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (linearLayout.findViewById(Integer.parseInt(
                                Objects.requireNonNull(document.toObject(Meetup.class)).id)) == null) {
                            meetCardPlacement(document);
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_personal_account);
        linearLayout = findViewById(R.id.meet_up_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayMeets();
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    private void meetCardPlacement(DocumentSnapshot document) {
        Meetup meet_card = document.toObject(Meetup.class);
        assert meet_card != null;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.meetup_card, null);
        constraintLayout.setId(Integer.parseInt(meet_card.id));

        TextView name = constraintLayout.findViewById(R.id.meet_card_name);
        name.setText(Objects.requireNonNull(meet_card.name));

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.bottomMargin = 100;
        params.leftMargin = 70;
        params.rightMargin = 70;

        constraintLayout.setLayoutParams(params);

        // Создание кнопки редактирования
        ImageButton edit_btn = constraintLayout.findViewById(R.id.edit_button);
        edit_btn.setOnClickListener(view -> {
            // Передаём данные о выбранном мероприятии
            Intent current_card = new Intent(PersonalAccount.this, MeetInfoDesk.class);
            current_card.putExtra(Meetup.class.getSimpleName(), meet_card);
            // Передача ключа текущего мероприятия
            current_card.putExtra("KEY", meet_card.id);
            startActivity(current_card);
        });

        // Создание кнопки удаления
        ImageButton delete_btn = constraintLayout.findViewById(R.id.delete_button);
        delete_btn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(PersonalAccount.this, R.style.custom_dialog);
            builder.setTitle(R.string.confirm_delete)
                    .setIcon(android.R.drawable.ic_delete)
                    .setMessage("Удалить мероприятие " + name.getText().toString() + "?")
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        // Удаление мероприятия и его карточки
                        database.collection("USERS")
                                .document(Objects.requireNonNull(mAuth.getUid()))
                                .collection("MEETS")
                                .document(meet_card.id).delete().addOnCompleteListener(task -> {
                                    linearLayout.removeView(findViewById(Integer.parseInt(meet_card.id)));
                                    StyleableToast.makeText(this, "Мероприятие " + name.getText() + " удалено", R.style.valid_toast).show();
                                });
                    })
                    .setNegativeButton(R.string.cancellation, null)
                    .create().show();
        });

        linearLayout.addView(constraintLayout);
    }

    // Функция выхода из аккаунта
    public void logout(View view) {
        // Разлогиниваем пользователя
        FirebaseAuth.getInstance().signOut();
        // Выход из Google аккаунта
        GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(PersonalAccount.this, googleOptions);
        googleSignInClient.signOut().addOnCompleteListener(PersonalAccount.this, task -> {

        });
        // Переходим на страницу входа
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}