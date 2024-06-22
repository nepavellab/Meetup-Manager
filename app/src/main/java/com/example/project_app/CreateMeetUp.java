package com.example.project_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;
import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;

public class CreateMeetUp extends AppCompatActivity {
    private FirebaseFirestore database;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createMeetUp(View view) {
        // Получение данных из представлений
        TimePicker meetup_start_time = findViewById(R.id.meet_start_time);
        TimePicker meetup_end_time = findViewById(R.id.meet_end_time);
        DatePicker meetup_date = findViewById(R.id.meet_date);
        String meet_name = ((EditText) findViewById(R.id.meet_naming)).getText().toString().trim();
        String meet_address = ((EditText) findViewById(R.id.address)).getText().toString().trim();
        String meet_enter_count = ((EditText) findViewById(R.id.enter_count))
                .getText().toString().replaceAll("[^0-9]", "");
        String meet_exit_count = ((EditText) findViewById(R.id.exit_count))
                .getText().toString().replaceAll("[^0-9]", "");;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        String enter_count = meet_enter_count.isEmpty() ? "inf" : meet_enter_count;
        String exit_count = meet_exit_count.isEmpty() ? "inf" : meet_exit_count;

        // Форматируем полученные данные даты и времени
        String meet_date = date_format(meetup_date);
        String meet_start_time = time_format(meetup_start_time);
        String meet_end_time = time_format(meetup_end_time);

        // Проверка заполнения всех полей (все текстовые представления обязательны для заполнения)
        if (meet_name.isEmpty() || meet_address.isEmpty()) {
            StyleableToast.makeText(this, "Не все поля заполнены!", R.style.invalid_toast).show();
            return;
        }

        String meet_id = generateMeetId();
        String meet_hash = mAuth.getUid() + "=" + meet_id;

        Meetup card = new Meetup(
                meet_name, meet_address, meet_date, meet_start_time,
                meet_end_time, meet_id, meet_hash, enter_count, exit_count
        );
        database.collection("USERS")
                .document(user_id)
                .collection("MEETS")
                .document(meet_id).set(card).addOnCompleteListener(task -> {
                    StyleableToast.makeText(this, "Мероприятие " + meet_name + " успешно добавлено", R.style.valid_toast).show();
                    startActivity(new Intent(this, PersonalAccount.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_meet_up);
        TimePicker startTime = findViewById(R.id.meet_start_time);
        TimePicker endTime = findViewById(R.id.meet_end_time);

        // Задание 24-часового формата таймера
        startTime.setIs24HourView(true);
        endTime.setIs24HourView(true);

        CheckBox enter_check = findViewById(R.id.enter_check);
        enter_check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            EditText enter_count = findViewById(R.id.enter_count);
            enter_count.setEnabled(isChecked);
        });

        CheckBox exit_check = findViewById(R.id.exit_check);
        exit_check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            EditText exit_count = findViewById(R.id.exit_count);
            exit_count.setEnabled(isChecked);
        });

        database = FirebaseFirestore.getInstance();
    }

    // Форматирование строки даты к виду: "дд.мм.гггг"
    private String date_format(DatePicker datePicker) {
        return  datePicker.getDayOfMonth() + "." +
                (datePicker.getMonth() < 9 ? "0" + (datePicker.getMonth() + 1) : (datePicker.getMonth() + 1)) + "." +
                datePicker.getYear();
    }

    // Форматирование строки времени к виду: "чч:мм:00" (точность до минуты)
    private String time_format(TimePicker timePicker) {
        return  (timePicker.getHour() < 10 ? "0" + timePicker.getHour() : timePicker.getHour()) + ":" +
                (timePicker.getMinute() < 10 ? "0" + timePicker.getMinute() : timePicker.getMinute());
    }

    private String generateMeetId() {
        return Integer.toString(new Random().nextInt(Integer.MAX_VALUE - 10));
    }
}