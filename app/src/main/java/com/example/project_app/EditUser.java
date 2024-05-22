package com.example.project_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class EditUser extends AppCompatActivity {
    private DatabaseReference database;
    private Guest guest;
    private MeetUpCard local_card;
    String KEY;

    @SuppressLint("CutPasteId")
    public void saveUpdates(View view) {
        guest.name = ((EditText) findViewById(R.id.update_guest_name)).getText().toString();
        guest.phone_number = ((EditText) findViewById(R.id.update_phone_number))
                .getText()
                .toString()
                .replaceAll("[^0-9]", "");
        guest.email = ((EditText) findViewById(R.id.update_email)).getText().toString();

        database.setValue(guest);
        Intent intent = new Intent(this, MeetInfoDesk.class);
        intent.putExtra(MeetUpCard.class.getSimpleName(), local_card);
        intent.putExtra("KEY", KEY);
        startActivity(intent);
        StyleableToast.makeText(this, "Данные успешно обновлены", R.style.valid_toast).show();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Bundle arguments = getIntent().getExtras();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        assert arguments != null;

        guest = (Guest) arguments.getSerializable(Guest.class.getSimpleName());
        local_card = (MeetUpCard) arguments.getSerializable(MeetUpCard.class.getSimpleName());
        KEY = arguments.getString("MEET_KEY");

        EditText new_email = findViewById(R.id.update_email);
        new_email.setText(guest.email);

        EditText new_name = findViewById(R.id.update_guest_name);
        new_name.setText(guest.name);

        EditText new_phone = findViewById(R.id.update_phone_number);
        new_phone.setText(guest.phone_number);

        database = FirebaseDatabase.getInstance()
                .getReference("USERS")
                .child(Objects.requireNonNull(
                        Objects.requireNonNull(
                                Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).replaceAll("[.#$\\[\\]]", "")
                        )
                )
                .child("MEETS")
                .child(Objects.requireNonNull(KEY))
                .child("GUESTS")
                .child(Objects.requireNonNull(arguments.getString("GUEST_KEY")));
    }

    private boolean phoneNumberValidate(String phone_numb) { // проверка валидации корректна только для телефонных номеров РФ
        return phone_numb.length() == 11 && (phone_numb.charAt(0) == '8' || phone_numb.charAt(0) == '7');
    }
}