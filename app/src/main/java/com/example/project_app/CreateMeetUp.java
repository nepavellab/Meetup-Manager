package com.example.project_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Random;
import java.util.UUID;

import io.github.muddz.styleabletoast.StyleableToast;

public class CreateMeetUp extends AppCompatActivity {
    private DatabaseReference database;

    public void createMeetUp(View view) {
        // Получение данных из представлений
        TimePicker meetup_start_time = findViewById(R.id.meet_start_time);
        TimePicker meetup_end_time = findViewById(R.id.meet_end_time);
        DatePicker meetup_date = findViewById(R.id.meet_date);
        String meet_name = ((EditText)findViewById(R.id.meet_naming)).getText().toString();
        String meet_address = ((EditText)findViewById(R.id.address)).getText().toString();

        // Форматируем полученные данные даты и времени
        String meet_date = date_format(meetup_date);
        String meet_start_time = time_format(meetup_start_time);
        String meet_end_time = time_format(meetup_end_time);

        // Проверка заполнения всех полей (все текстовые представления обязательны для заполнения)
        if (meet_name.isEmpty() || meet_address.isEmpty()) {
            StyleableToast.makeText(this, "Не все поля заполнены!", R.style.invalid_toast).show();
            return;
        }

        // Запись информации и созданной встречи в базу данных
        String meet_id = generateMeetId(meet_name);
        MeetUpCard card = new MeetUpCard(meet_name, meet_address, meet_date, meet_start_time, meet_end_time);
        database.child(meet_id).setValue(card);
        StyleableToast.makeText(this, "Мероприятие " + meet_name + " успешно добавлено", R.style.valid_toast).show();
        startActivity(new Intent(this, PersonalAccount.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
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

        database = FirebaseDatabase.getInstance().getReference("Meets");
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
                (timePicker.getMinute() < 10 ? "0" + timePicker.getMinute() : timePicker.getMinute()) + ":00";
    }

    private String generateMeetId(String name) {
        return UUID.randomUUID().toString() + "?" + name + "?" + new Random().nextInt(Integer.MAX_VALUE - 10);
    }
}