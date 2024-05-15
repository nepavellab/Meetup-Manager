package com.example.project_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class AddNewGuest extends AppCompatActivity {
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_guest);

        // Получение ключа текущего мероприятия
        String key = getIntent().getExtras().getString("KEY");
        database = FirebaseDatabase.getInstance().getReference("Meets").child(key).child("GUESTS");

        Button add_button = findViewById(R.id.add_click);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                startActivity(new Intent(AddNewGuest.this, MeetInfoDesk.class));
            }
        });
    }
}