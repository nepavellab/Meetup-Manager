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

public class AddNewGuest extends AppCompatActivity {
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

    public void addGuestClick(View view) throws WriterException {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        EditText guest_name = findViewById(R.id.guest_name_input);
        EditText guest_phone = findViewById(R.id.guest_phone_input);
        EditText guest_email = findViewById(R.id.guest_email_input);

        String phone_numb = guest_phone.getText().toString().trim().replaceAll("[^0-9]", "");

        if (guest_email.getText().toString().isEmpty() ||
            guest_name.getText().toString().isEmpty()  ||
            phone_numb.isEmpty()) {
            StyleableToast.makeText(this, "Не все поля заполнены!", R.style.invalid_toast).show();
            return;
        } else if (!phoneNumberValidate(phone_numb))  {
            StyleableToast.makeText(this, "Указанный номер телефона не является корретным", R.style.invalid_toast).show();
            return;
        }

        String guest_id = generateUserId();

        Guest guest = new Guest(guest_id,
                guest_name.getText().toString().trim(),
                guest_email.getText().toString().trim(),
                phone_numb, "",
                local_card.entrance_control, local_card.exit_control,
                "off");

        Bitmap qrMap = createQr(guest);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        qrMap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        byte[] bytes = byteStream.toByteArray();
        guest.QR = Base64.encodeToString(bytes, Base64.DEFAULT);

        database.collection("USERS")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .collection("MEETS")
                .document(KEY)
                .collection("GUESTS")
                .document(guest_id).set(guest).addOnCompleteListener(task -> {
                    StyleableToast.makeText(this, "Гость " + guest.name + " успешно добавлен", R.style.valid_toast).show();

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
        setContentView(R.layout.activity_add_new_guest);
        Bundle arguments = getIntent().getExtras();
        assert arguments != null;
        KEY = arguments.getString("KEY");
        local_card = (Meetup) arguments.getSerializable(Meetup.class.getSimpleName());
        database = FirebaseFirestore.getInstance();
    }

    private boolean phoneNumberValidate(String phone_numb) {
        return phone_numb.length() == 11 && (phone_numb.charAt(0) == '8' || phone_numb.charAt(0) == '7');
    }

    private String generateUserId() {
        return Integer.toString(new Random().nextInt(Integer.MAX_VALUE - 10));
    }

    private Bitmap createQr(Guest guest) throws WriterException {
        BitMatrix bitMatrix = new QRCodeWriter().encode(guest.getDataForQr(), BarcodeFormat.QR_CODE, 500, 500);
        Bitmap qrMap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < 500; x++) {
            for (int y = 0; y < 500; y++) {
                qrMap.setPixel(x, y, bitMatrix.get(x, y) ? Color.RED : Color.WHITE);
            }
        }
        return qrMap;
    }
}