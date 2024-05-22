package com.example.project_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import io.github.muddz.styleabletoast.StyleableToast;

public class AddNewGuest extends AppCompatActivity {
    private DatabaseReference database;
    private MeetUpCard local_card;
    String KEY;

    public void addGuestClick(View view) {
        EditText guest_name = findViewById(R.id.guest_name_input);
        EditText guest_phone = findViewById(R.id.guest_phone_input);
        EditText guest_email = findViewById(R.id.guest_email_input);

        String phone_numb = guest_phone.getText().toString();
        phone_numb = phone_numb.replaceAll("[^0-9]", "");

        if (guest_email.getText().toString().isEmpty() ||
            guest_name.getText().toString().isEmpty()  ||
            phone_numb.isEmpty()) {
            StyleableToast.makeText(this, "Не все поля заполнены!", R.style.invalid_toast).show();
            return;
        } else if (!phoneNumberValidate(phone_numb))  { // номер телефона не валидный
            StyleableToast.makeText(this, "Указанный номер телефона не является корретным", R.style.invalid_toast).show();
            return;
        }

        String guest_id = generateUserId(guest_name.getText().toString());

        Guest guest = new Guest(guest_id,
                guest_name.getText().toString(),
                guest_email.getText().toString(),
                phone_numb);

        database.child(guest.id).setValue(guest);
        StyleableToast.makeText(this, "Гость " + guest.name + " успешно добавлен", R.style.valid_toast).show();

        Intent intent = new Intent(this, MeetInfoDesk.class);
        intent.putExtra("KEY", KEY);
        intent.putExtra(MeetUpCard.class.getSimpleName(), local_card);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_guest);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Получение ключа текущего мероприятия
        Bundle arguments = getIntent().getExtras();
        assert arguments != null;
        KEY = arguments.getString("KEY");
        local_card = (MeetUpCard) arguments.getSerializable(MeetUpCard.class.getSimpleName());
        database = FirebaseDatabase.getInstance()
                .getReference("USERS")
                .child(Objects.requireNonNull(
                        Objects.requireNonNull(
                                Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).replaceAll("[.#$\\[\\]]", "")
                        )
                )
                .child("MEETS")
                .child(KEY)
                .child("GUESTS");
    }

    private boolean phoneNumberValidate(String phone_numb) { // проверка валидации корректна только для телефонных номеров РФ
        return phone_numb.length() == 11 && (phone_numb.charAt(0) == '8' || phone_numb.charAt(0) == '7');
    }

    private String generateUserId(String name) {
        return Integer.toString(new Random().nextInt(Integer.MAX_VALUE - 10));
    }
}