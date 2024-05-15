package com.example.project_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNewGuest extends AppCompatActivity {
    private DatabaseReference database;
    private MeetUpCard local_card;
    String KEY;

    public void addGuestClick(View view) {
        EditText guest_id = findViewById(R.id.guest_id_input);
        EditText guest_name = findViewById(R.id.guest_name_input);
        EditText guest_phone = findViewById(R.id.guest_phone_input);
        EditText guest_email = findViewById(R.id.guest_email_input);

        Guest guest = new Guest(
                guest_id.getText().toString(),
                guest_name.getText().toString(),
                guest_email.getText().toString(),
                guest_phone.getText().toString());

        database.child(guest.id).setValue(guest);

        Toast msg = Toast.makeText(AddNewGuest.this,"Гость " + guest.name + " добавлен", Toast.LENGTH_SHORT);
        msg.setGravity(Gravity.TOP, 0, 100);
        msg.show();
        Intent intent = new Intent(this, MeetInfoDesk.class);
        intent.putExtra("KEY", KEY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MeetUpCard.class.getSimpleName(), local_card);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_guest);

        // Получение ключа текущего мероприятия
        Bundle arguments = getIntent().getExtras();
        KEY = arguments.getString("KEY");
        local_card = (MeetUpCard) arguments.getSerializable(MeetUpCard.class.getSimpleName());
        database = FirebaseDatabase.getInstance().getReference("Meets").child(KEY).child("GUESTS");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}