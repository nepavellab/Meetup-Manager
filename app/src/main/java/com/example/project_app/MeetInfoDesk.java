package com.example.project_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MeetInfoDesk extends AppCompatActivity {
    private DatabaseReference database;

    LinearLayout linearLayout;

    public void addGuest(View view) {
        Intent intent = new Intent(this, AddNewGuest.class);
        intent.putExtra("KEY", database.getParent().getKey());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_info_desk);

        Bundle meet_up_info = getIntent().getExtras();
        // Информация о текущем мероприятии
        MeetUpCard local_card = (MeetUpCard) meet_up_info.getSerializable(MeetUpCard.class.getSimpleName());
        // Ключ текущего (кодовое слово) мероприятия для базы данных
        String key = meet_up_info.getString("KEY");

        database = FirebaseDatabase.getInstance().getReference("Meets").child(key).child("GUESTS");

        TextView meet_name = findViewById(R.id.local_meet_name);
        meet_name.setText("Название: " + local_card.name);

        TextView meet_address = findViewById(R.id.local_meet_address);
        meet_address.setText("Адрес: " + local_card.address);

        linearLayout = findViewById(R.id.guest_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayGuests();
    }

    private void displayGuests() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    linearLayout.removeAllViews();
                    TextView meet_label = new TextView(MeetInfoDesk.this);
                    meet_label.setText("Здесь будут гости мероприятия");
                    meet_label.setTextColor(0xFF000080);
                    meet_label.setTextSize(30);
                    meet_label.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                    meet_label.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
                    ));

                    linearLayout.addView(meet_label);
                } else {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    for (DataSnapshot db : snapshot.getChildren()) {
                        // Если гостя ещё нет в списке
                        if (linearLayout.findViewById(Integer.parseInt(db.getValue(Guest.class).id)) == null) {
                            Guest guest = db.getValue(Guest.class);

                            ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.guest_card, null);
                            constraintLayout.setId(Integer.parseInt(guest.id));

                            TextView guest_name = constraintLayout.findViewById(R.id.guest_name);
                            guest_name.setText(guest.name);

                            TextView guest_email = constraintLayout.findViewById(R.id.guest_email);
                            guest_email.setText(guest.email);

                            TextView guest_phone_number = constraintLayout.findViewById(R.id.guest_phone_number);
                            guest_phone_number.setText(guest.phone_number);

                            linearLayout.addView(constraintLayout);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}