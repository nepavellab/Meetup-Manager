package com.example.project_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.github.muddz.styleabletoast.StyleableToast;
import java.util.Objects;

public class PersonalAccount extends AppCompatActivity {
    private DatabaseReference database; // ссылка на базу данных
    private LinearLayout linearLayout; // контейнер, динамически обновляющий карточки пользователя


    // Функция открывает форму для создания мероприятия
    public void createMeet(View view) {
        startActivity(new Intent(this, CreateMeetUp.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // В базе данных переходим в раздел текущего пользователя
        database = FirebaseDatabase.getInstance().getReference("Meets");
        setContentView(R.layout.activity_personal_account);
        linearLayout = findViewById(R.id.meet_up_list);

        // Вывод всех карточек мероприятий пользователя
        displayCards();
    }

    // Функция отображает приветственную надпись на экране
    private void setWelcomeLabel() {
        linearLayout.removeAllViews();
        TextView meet_label = new TextView(PersonalAccount.this);
        meet_label.setText(R.string.welcome_meets);
        meet_label.setTextColor(0xFFFFFFFF);
        meet_label.setTextSize(30);
        meet_label.setGravity(Gravity.CENTER);
        meet_label.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(meet_label);
    }

    @SuppressLint("InflateParams")
    private void meetCardPlacement(DataSnapshot dataSnapshot) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.card_layout, null);
        constraintLayout.setId(getNumericId(Objects.requireNonNull(dataSnapshot.getKey())));

        TextView name = constraintLayout.findViewById(R.id.meet_card_name);
        name.setText(Objects.requireNonNull(dataSnapshot.getValue(MeetUpCard.class)).name);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.bottomMargin = 25;
        params.leftMargin = 15;
        params.rightMargin = 15;

        constraintLayout.setLayoutParams(params);

        // Создание кнопки редактирования
        Button edit_btn = constraintLayout.findViewById(R.id.edit_button);
        edit_btn.setOnClickListener(view -> {
            // Передаём данные о выбранном мероприятии
            MeetUpCard card = dataSnapshot.getValue(MeetUpCard.class);
            Intent current_card = new Intent(PersonalAccount.this, MeetInfoDesk.class);
            current_card.putExtra(MeetUpCard.class.getSimpleName(), card);
            // Передача ключа текущего мероприятия
            current_card.putExtra("KEY", dataSnapshot.getKey());
            startActivity(current_card);
        });

        // Создание кнопки удаления
        Button delete_btn = constraintLayout.findViewById(R.id.delete_button);
        delete_btn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(PersonalAccount.this);
            builder.setTitle(R.string.confirm_meet_delete)
                    .setIcon(android.R.drawable.ic_delete)
                    .setMessage("Удалить мероприятие " + name.getText().toString() + "?")
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        // Удаление мероприятия и его карточки
                        int numeric_id = getNumericId(dataSnapshot.getKey());
                        database.child(dataSnapshot.getKey()).removeValue();
                        linearLayout.removeView(findViewById(numeric_id));
                        StyleableToast.makeText(this, "Мероприятие " + name.getText() + " удалено", R.style.valid_toast).show();
                    })
                    .setNegativeButton(R.string.cancellation, null)
                    .create().show();
        });

        linearLayout.addView(constraintLayout);
    }

    // Функция отображения карточек мероприятий
    private void displayCards() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Если в базе данных нет мероприятий
                if (snapshot.getChildrenCount() == 0) {
                    // Вывод приглашения к созданию мероприятия
                    setWelcomeLabel();
                } else {
                    for (DataSnapshot db : snapshot.getChildren()) {
                        // Если карточка ещё не добавлена в контейнер (избежание повторной отрисовки)
                        if (linearLayout.findViewById(getNumericId(Objects.requireNonNull(db.getKey()))) == null) {
                            // Добавление карточки мероприятия
                            meetCardPlacement(db);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }

    // Функция выхода из аккаунта
    public void logout(View view) {
        // Разлогиниваем пользователя
        FirebaseAuth.getInstance().signOut();
        // Переходим на страницу входа
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Функция возвращает числовое значение, записанное в строке id
    private int getNumericId(String full_meet_id) {
        return Integer.parseInt(full_meet_id.split("\\?")[2]);
    }
}