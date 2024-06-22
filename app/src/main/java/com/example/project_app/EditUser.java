package com.example.project_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;
import io.github.muddz.styleabletoast.StyleableToast;

public class EditUser extends AppCompatActivity {
    private FirebaseFirestore database;
    private Guest guest;
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

    @SuppressLint("CutPasteId")
    public void saveUpdates(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        guest.name = ((EditText) findViewById(R.id.update_guest_name)).getText().toString().trim();
        guest.phone_number = ((EditText) findViewById(R.id.update_phone_number))
                .getText()
                .toString()
                .replaceAll("[^0-9]", "").trim();
        guest.email = ((EditText) findViewById(R.id.update_email)).getText().toString().trim();

        if (!phoneNumberValidate(guest.phone_number)) {
            StyleableToast.makeText(this, "Некорретный номер телефона", R.style.invalid_toast).show();
        }

        //database.setValue(guest);
        database.collection("USERS")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .collection("MEETS")
                .document(KEY)
                .collection("GUESTS")
                .document(guest.id).set(guest).addOnCompleteListener(task -> {
                    Intent intent = new Intent(this, MeetInfoDesk.class);
                    intent.putExtra(Meetup.class.getSimpleName(), local_card);
                    intent.putExtra("KEY", KEY);
                    startActivity(intent);
                    StyleableToast.makeText(this, "Данные успешно обновлены", R.style.valid_toast).show();
                    finish();
                });
    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        database = FirebaseFirestore.getInstance();
        Bundle arguments = getIntent().getExtras();

        assert arguments != null;

        guest = (Guest) arguments.getSerializable(Guest.class.getSimpleName());
        local_card = (Meetup) arguments.getSerializable(Meetup.class.getSimpleName());
        KEY = arguments.getString("MEET_KEY");

        EditText new_email = findViewById(R.id.update_email);
        new_email.setText(guest.email);

        EditText new_name = findViewById(R.id.update_guest_name);
        new_name.setText(guest.name);

        EditText new_phone = findViewById(R.id.update_phone_number);
        new_phone.setText(guest.phone_number);

        ImageButton call = findViewById(R.id.phone_icon);
        call.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + guest.phone_number));
            startActivity(intent);
        });

        ImageView qrImage = findViewById(R.id.QR_image);
        byte[] decodedByte = Base64.decode(guest.QR, Base64.DEFAULT);
        Bitmap qrMap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        qrImage.setImageBitmap(qrMap);

        CheckBox edit_mode = findViewById(R.id.is_user_edit);
        edit_mode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            EditText update_name = findViewById(R.id.update_guest_name);
            EditText update_email = findViewById(R.id.update_email);
            EditText update_phone = findViewById(R.id.update_phone_number);
            update_name.setEnabled(isChecked);
            update_phone.setEnabled(isChecked);
            update_email.setEnabled(isChecked);
        });
    }

    private boolean phoneNumberValidate(String phone_numb) { // проверка валидации корректна только для телефонных номеров РФ
        return phone_numb.length() == 11 && (phone_numb.charAt(0) == '8' || phone_numb.charAt(0) == '7');
    }
}