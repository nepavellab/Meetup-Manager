package com.example.project_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;

public class CreateGroup extends AppCompatActivity {
    private FirebaseFirestore database;
    private Meetup local_card;
    String KEY;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addGroupClick(View view) throws WriterException {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        EditText group_title = findViewById(R.id.group_name_input);
        EditText group_size = findViewById(R.id.group_size_input);

        if (group_title.getText().toString().isEmpty() ||
            group_size.getText().toString().isEmpty()) {
            StyleableToast.makeText(this, "Не все поля заполнены!", R.style.invalid_toast).show();
            return;
        }

        String group_id = generateUserId();
        String group_enter_count = Objects.equals(local_card.exit_control, "inf") ?
                local_card.entrance_control : Integer.toString(Integer.parseInt(local_card.entrance_control) * 2);

        Group group = new Group(
                group_title.getText().toString().trim(),
                group_size.getText().toString().trim(),
                group_id, "",
                group_enter_count);

        Bitmap qrMap = createQr(group);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        qrMap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        byte[] bytes = byteStream.toByteArray();
        group.QR = Base64.encodeToString(bytes, Base64.DEFAULT);

        database.collection("USERS")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .collection("MEETS")
                .document(KEY)
                .collection("GROUPS")
                .document(group_id).set(group).addOnCompleteListener(task -> {
                    StyleableToast.makeText(this, "Группа " + group.name + " успешно добавлена", R.style.valid_toast).show();

                    Intent intent = new Intent(this, MeetInfoDesk.class);
                    intent.putExtra("KEY", KEY);
                    intent.putExtra(Meetup.class.getSimpleName(), local_card);
                    startActivity(intent);
                    finish();
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        // Получение ключа текущего мероприятия
        Bundle arguments = getIntent().getExtras();
        assert arguments != null;
        KEY = arguments.getString("KEY");
        local_card = (Meetup) arguments.getSerializable(Meetup.class.getSimpleName());
        database = FirebaseFirestore.getInstance();
    }

    private Bitmap createQr(Group group) throws WriterException {
        BitMatrix bitMatrix = new QRCodeWriter().encode(group.getDataForQr(), BarcodeFormat.QR_CODE, 500, 500);
        Bitmap qrMap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < 500; x++) {
            for (int y = 0; y < 500; y++) {
                qrMap.setPixel(x, y, bitMatrix.get(x, y) ? Color.RED : Color.WHITE);
            }
        }
        return qrMap;
    }

    private String generateUserId() {
        return Integer.toString(new Random().nextInt(Integer.MAX_VALUE - 10));
    }
}