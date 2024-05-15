package com.example.project_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class MeetInfoDesk extends AppCompatActivity {
    private DatabaseReference database;
    private LinearLayout linearLayout;
    private MeetUpCard local_card;

    public void addGuest(View view) {
        Intent intent = new Intent(this, AddNewGuest.class);
        intent.putExtra("KEY", database.getParent().getKey());
        intent.putExtra(MeetUpCard.class.getSimpleName(), local_card);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_info_desk);

        Bundle meet_up_info = getIntent().getExtras();
        // Информация о текущем мероприятии
        local_card = (MeetUpCard) meet_up_info.getSerializable(MeetUpCard.class.getSimpleName());
        // Ключ текущего (кодовое слово) мероприятия для базы данных
        String KEY = meet_up_info.getString("KEY");

        database = FirebaseDatabase.getInstance().getReference("Meets").child(KEY).child("GUESTS");

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

                            ImageButton delete_btn = constraintLayout.findViewById(R.id.delete_guest_button);
                            delete_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MeetInfoDesk.this);
                                    builder.setTitle("Подтверждение удаления")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setMessage("Удалить гостя " + guest.name + "?")
                                            .setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Удаление мероприятия и его карточки
                                                    database.child(guest.id).removeValue();
                                                    linearLayout.removeView(findViewById(Integer.parseInt(db.getKey())));
                                                    Toast msg = Toast.makeText(MeetInfoDesk.this,"Удалено", Toast.LENGTH_SHORT);
                                                    msg.setGravity(Gravity.TOP, 0, 100);
                                                    msg.show();
                                                }
                                            })
                                            .setNegativeButton("Отмена", null)
                                            .create().show();
                                }
                            });

                            Button generate_qr_btn = constraintLayout.findViewById(R.id.generate_qr);
                            generate_qr_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ImageView QR_view = constraintLayout.findViewById(R.id.QR);
                                    try {
                                        // Генерируем QR
                                        BitMatrix bitMatrix = new QRCodeWriter().encode(guest.getDataForQr(), BarcodeFormat.QR_CODE, 500, 500);
                                        // КОД ДЛЯ ТЕСТИРОВАНИЯ
                                        Bitmap bmp = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
                                        for (int x = 0; x < 500; x++) {
                                            for (int y = 0; y < 500; y++) {
                                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                            }
                                        }
                                        QR_view.setImageBitmap(bmp);
                                    } catch (WriterException exp) {
                                        Toast msg = Toast.makeText(MeetInfoDesk.this,"Ошибка генерации QR", Toast.LENGTH_SHORT);
                                        msg.setGravity(Gravity.TOP, 0, 100);
                                        msg.show();
                                    }
                                }
                            });

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