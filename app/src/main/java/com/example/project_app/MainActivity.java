package com.example.project_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    private DatabaseReference database;
    private LinearLayout linearLayout;

    public void createMeet(View view) {
        startActivity(new Intent(this, CreateMeetUp.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference("Meets");
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.meet_up_list);
        displayCards();
    }

    private void setWelcomeLabel() {
        linearLayout.removeAllViews();
        TextView meet_label = new TextView(MainActivity.this);
        meet_label.setText(R.string.welcome_meets);
        meet_label.setTextColor(0xFF000080);
        meet_label.setTextSize(30);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        meet_label.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(meet_label);
    }

    private void meetCardPlacement(DataSnapshot dataSnapshot) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.card_layout, null);
        constraintLayout.setId(getNumericId(dataSnapshot.getKey()));

        TextView name = constraintLayout.findViewById(R.id.meet_card_name);
        name.setText(dataSnapshot.getValue(MeetUpCard.class).name);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.bottomMargin = 25;
        params.leftMargin = 15;
        params.rightMargin = 15;

        constraintLayout.setLayoutParams(params);

        // Создание кнопки редактирования
        ImageButton edit_btn = constraintLayout.findViewById(R.id.edit_button);
        edit_btn.setOnClickListener(view -> {
            // Передаём данные о выбранном мероприятии
            MeetUpCard card = dataSnapshot.getValue(MeetUpCard.class);
            Intent current_card = new Intent(MainActivity.this, MeetInfoDesk.class);
            current_card.putExtra(MeetUpCard.class.getSimpleName(), card);
            // Передача ключа текущего мероприятия
            current_card.putExtra("KEY", dataSnapshot.getKey());
            startActivity(current_card);
        });

        // Создание кнопки удаления
        ImageButton delete_btn = constraintLayout.findViewById(R.id.delete_button);
        delete_btn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.confirm_meet_delete)
                    .setIcon(android.R.drawable.ic_delete)
                    .setMessage("Удалить мероприятие " + name.getText().toString() + "?")
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        // Удаление мероприятия и его карточки
                        int numeric_id = getNumericId(dataSnapshot.getKey());
                        database.child(dataSnapshot.getKey()).removeValue();
                        linearLayout.removeView(findViewById(numeric_id));
                        CustomToast.makeText(this, "Мероприятие " + name.getText() + " удалено", true).show();
                    })
                    .setNegativeButton(R.string.cancellation, null)
                    .create().show();
        });

        linearLayout.addView(constraintLayout);
    }

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
                        if (linearLayout.findViewById(getNumericId(db.getKey())) == null) {
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

    private int getNumericId(String full_meet_id) {
        return Integer.parseInt(full_meet_id.split("\\?")[2]);
    }
}