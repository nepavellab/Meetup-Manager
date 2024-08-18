package com.example.project_app;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import io.github.muddz.styleabletoast.StyleableToast;

public class MeetInfoDesk extends AppCompatActivity {
    private FirebaseFirestore database;
    private LinearLayout linearLayout;
    private Meetup local_card;
    private String MEET_KEY;
    FirebaseAuth mAuth;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("InflateParams")
    public void showStatusInformation(View view) {
        AlertDialog.Builder status = new AlertDialog.Builder(MeetInfoDesk.this, R.style.custom_dialog);
        status.setTitle("Статусы гостя")
                .setPositiveButton("Закрыть", null)
                .setIcon(R.drawable.status_info_icon);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.status_information, null);
        status.setView(constraintLayout);
        status.show();
    }

    public void copyMeetKeyWord(View view) {
        TextView key_word = findViewById(R.id.meet_key_word);
        key_word.setText(local_card.key_word);
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Label", local_card.key_word);
        clipboard.setPrimaryClip(clip);
        StyleableToast.makeText(this, "Ключевое слово скопировано", R.style.valid_toast).show();
    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_info_desk);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        Bundle meet_up_info = getIntent().getExtras();
        assert meet_up_info != null;
        local_card = (Meetup) meet_up_info.getSerializable(Meetup.class.getSimpleName());
        MEET_KEY = meet_up_info.getString("KEY");
        assert MEET_KEY != null;

        TextView meet_name = findViewById(R.id.local_meet_name);
        meet_name.setText(local_card.name);

        TextView meet_address = findViewById(R.id.local_meet_address);
        meet_address.setText(local_card.address);

        Button add_button = findViewById(R.id.add_mode_button);

        RadioGroup mode = findViewById(R.id.modes);

        mode.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton current_mode = findViewById(checkedId);
            switch (current_mode.getText().toString()) {
                case "Гости":
                    displayGuest();
                    add_button.setText("Добавить гостя");
                    add_button.setOnClickListener(view -> {
                        Intent intent = new Intent(this, AddNewGuest.class);
                        intent.putExtra("KEY", Objects.requireNonNull(MEET_KEY));
                        intent.putExtra(Meetup.class.getSimpleName(), local_card);
                        startActivity(intent);
                    });
                    break;
                case "Группы":
                    displayGroups();
                    add_button.setText("Добавить группу");
                    add_button.setOnClickListener(view -> {
                        Intent intent = new Intent(this, CreateGroup.class);
                        intent.putExtra("KEY", Objects.requireNonNull(MEET_KEY));
                        intent.putExtra(Meetup.class.getSimpleName(), local_card);
                        startActivity(intent);
                    });
                    break;
            }
        });
        linearLayout = findViewById(R.id.guest_list);
        displayGuest();
        add_button.setText("Добавить гостя");
        add_button.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddNewGuest.class);
            intent.putExtra("KEY", Objects.requireNonNull(MEET_KEY));
            intent.putExtra(Meetup.class.getSimpleName(), local_card);
            startActivity(intent);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        HorizontalScrollView horizontalScrollView = findViewById(R.id.scroll_meet_hash);
        horizontalScrollView.setVisibility(View.INVISIBLE);
        finish();
    }

    private void displayGroups() {
        linearLayout.removeAllViews();
        database.collection("USERS")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .collection("MEETS")
                .document(MEET_KEY)
                .collection("GROUPS")
                .get().addOnCompleteListener(task -> {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (linearLayout.findViewById(Integer.parseInt(
                                Objects.requireNonNull(document.toObject(Group.class)).id)) == null) {
                            groupCardPlacement(document);
                        }
                    }
                });
    }

    private void displayGuest() {
        linearLayout.removeAllViews();
        database.collection("USERS")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .collection("MEETS")
                .document(MEET_KEY)
                .collection("GUESTS")
                .get().addOnCompleteListener(task -> {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (linearLayout.findViewById(Integer.parseInt(
                                Objects.requireNonNull(document.toObject(Guest.class)).id)) == null) {
                            guestCardPlacement(document);
                        }
                    }
                });
    }

    @SuppressLint("InflateParams")
    private void guestCardPlacement(DocumentSnapshot document) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Guest guest = document.toObject(Guest.class);

        ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.guest_card, null);
        assert guest != null;
        constraintLayout.setId(Integer.parseInt(guest.id));

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = 100;
        params.leftMargin = 70;
        params.rightMargin = 70;
        constraintLayout.setLayoutParams(params);

        ImageView guest_status_icon = constraintLayout.findViewById(R.id.guest_status_icon);
        switch (guest.status) {
            case "on":
                guest_status_icon.setImageResource(R.drawable.arrived_icon_black);
                break;
            case "off":
                guest_status_icon.setImageResource(R.drawable.not_arrived_icon_black);
                break;
            case "exit":
                guest_status_icon.setImageResource(R.drawable.exit_icon_black);
                break;
        }

        TextView guest_name = constraintLayout.findViewById(R.id.guest_name);
        guest_name.setText(guest.name);

        ImageButton edit_guest = constraintLayout.findViewById(R.id.edit_guest_card);
        edit_guest.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditUser.class);
            intent.putExtra("MEET_KEY", MEET_KEY);
            intent.putExtra(Meetup.class.getSimpleName(), local_card);
            intent.putExtra(Guest.class.getSimpleName(), guest);
            startActivity(intent);
            finish();
        });

        ImageButton delete_btn = constraintLayout.findViewById(R.id.delete_guest_button);
        delete_btn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MeetInfoDesk.this, R.style.custom_dialog);
            builder.setTitle(R.string.confirm_delete)
                    .setIcon(android.R.drawable.ic_delete)
                    .setMessage("Удалить гостя " + guest.name + "?")
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        // Удаление гостя
                        database.collection("USERS")
                                .document(Objects.requireNonNull(mAuth.getUid()))
                                .collection("MEETS")
                                .document(MEET_KEY)
                                .collection("GUESTS")
                                .document(guest.id).delete().addOnCompleteListener(task -> {
                                    linearLayout.removeView(findViewById(Integer.parseInt(guest.id)));
                                    StyleableToast.makeText(MeetInfoDesk.this, "Гость " + guest.name + " успешно удалён", R.style.valid_toast).show();
                                });
                    })
                    .setNegativeButton(R.string.cancellation, null)
                    .create().show();
        });

        ImageButton send_qr_btn = constraintLayout.findViewById(R.id.send_qr);
        send_qr_btn.setOnClickListener(view -> {
            byte[] decodedByte = Base64.decode(guest.QR, Base64.DEFAULT);
            Bitmap qrMap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            Uri qrUri;
            try {
                qrUri = getContentUriFromBitmap(qrMap, guest);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("images/png");
            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            shareIntent.setClipData(ClipData.newRawUri("", qrUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, qrUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, generateInvitationMessage(guest));
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Поделиться"));
        });
        linearLayout.addView(constraintLayout);
    }

    private String generateInvitationMessage(Guest guest) {
        return String.format("%s, вы приглашены на мероприятие: \"%s\", %s\n\nАдрес: \"%s\"\n\n" +
                        "К сообщению прикреплён QR код, " +
                        "покажите его при входе\n\n" +
                        "Начало в %s, не опаздывайте!\n%s ждёт вас ;)",
                guest.name,
                local_card.name,
                local_card.date,
                local_card.address,
                local_card.start_time,
                Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
    }

    private String generateInvitationMessage(Group group) {
        return String.format("Группа \"%s\" приглашена на мероприятие \"%s\"" +
                " по адресу: \"%s\".\nНе опаздывайте, начало в %s",
                group.name, local_card.name, local_card.address, local_card.start_time);
    }

    @SuppressLint("InflateParams")
    private void groupCardPlacement(DocumentSnapshot document) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Group group = document.toObject(Group.class);

        ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.group_card, null);

        assert group != null;
        constraintLayout.setId(Integer.parseInt(group.id));

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = 100;
        params.leftMargin = 70;
        params.rightMargin = 70;
        constraintLayout.setLayoutParams(params);

        TextView guest_name = constraintLayout.findViewById(R.id.group_name);
        guest_name.setText(group.name);

        ImageButton delete_btn = constraintLayout.findViewById(R.id.delete_group_button);
        delete_btn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MeetInfoDesk.this, R.style.custom_dialog);
            builder.setTitle(R.string.confirm_delete)
                    .setIcon(android.R.drawable.ic_delete)
                    .setMessage("Удалить группу " + group.name + "?")
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        database.collection("USERS")
                                .document(Objects.requireNonNull(mAuth.getUid()))
                                .collection("MEETS")
                                .document(MEET_KEY)
                                .collection("GROUPS")
                                .document(group.id).delete().addOnCompleteListener(task -> {
                                    linearLayout.removeView(findViewById(Integer.parseInt(group.id)));
                                    StyleableToast.makeText(MeetInfoDesk.this, "Группа " + group.name + " успешно удалён", R.style.valid_toast).show();
                                });
                    })
                    .setNegativeButton(R.string.cancellation, null)
                    .create().show();
        });

        ImageButton generate_qr_btn = constraintLayout.findViewById(R.id.QR_group_button);
        generate_qr_btn.setOnClickListener(view -> {
            byte[] decodedByte = Base64.decode(group.QR, Base64.DEFAULT);
            Bitmap qrMap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            Uri qrUri;
            try {
                qrUri = getContentUriFromBitmap(qrMap, group);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("images/png");
            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            shareIntent.setClipData(ClipData.newRawUri("", qrUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, qrUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, generateInvitationMessage(group));
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Поделиться"));
        });
        linearLayout.addView(constraintLayout);
    }

    @SuppressLint("RestrictedApi")
    private <CODE_TYPE> Uri getContentUriFromBitmap(Bitmap bitmap, CODE_TYPE object)
            throws NoSuchFieldException, IllegalAccessException {
        final File file = new File(
                getCacheDir(), Objects.requireNonNull(
                        object.getClass().getField("id").get(object)) + ".png");
        try {
            FileOutputStream cacheStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, cacheStream);
            cacheStream.flush();
            cacheStream.close();
        } catch (IOException e) {
            return null;
        }
        return FileProvider.getUriForFile(this, "com.example.project_app.provider", file);
    }
}