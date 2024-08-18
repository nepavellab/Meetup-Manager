package com.example.project_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public class ScanResult extends AppCompatActivity {
    private FirebaseFirestore database;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressLint("InflateParams")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        database = FirebaseFirestore.getInstance();

        Bundle arguments = getIntent().getExtras();
        assert arguments != null;

        String meet_hash = arguments.getString("MEET_HASH");
        String qr_string = arguments.getString("QR_STRING");

        assert meet_hash != null;
        assert qr_string != null;
        try {
            readAndProcess(qr_string, meet_hash);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    private void readAndProcess(@NonNull String qr,
                                @NonNull String hash)
            throws NoSuchFieldException, IllegalAccessException {
        String[] meet_info = hash.split("=");
        String[] qr_info = qr.split("=");
        String type = qr_info[0];
        String user_id = meet_info[0];
        String meet_id = meet_info[1];
        String id = qr_info[qr_info.length - 1];

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ConstraintLayout validLayout = (ConstraintLayout) inflater.inflate(R.layout.valid_qr_layout, null);
        ConstraintLayout invalidLayout = (ConstraintLayout) inflater.inflate(R.layout.invalid_qr_layout, null);

        TextView valid_info_label = validLayout.findViewById(R.id.valid_info_label);
        TextView invalid_info_label = invalidLayout.findViewById(R.id.invalid_info_label);

        if (Objects.equals(type, "GUESTS")) {
            database.collection("USERS")
                    .document(user_id)
                    .collection("MEETS")
                    .document(meet_id)
                    .collection(type)
                    .document(id).get().addOnCompleteListener(task -> {
                        boolean check_result;

                        Guest guest = task.getResult().toObject(Guest.class);
                        assert guest != null;

                        if (Objects.equals(guest.status, "off") || Objects.equals(guest.status, "exit")) {
                            if (Objects.equals(guest.entrance_count, "inf")) {
                                valid_info_label.setText(guest.name + ", добро пожаловать!");
                                check_result = true;
                            } else {
                                int enter = Integer.parseInt(guest.entrance_count);
                                if (enter > 0) {
                                    enter -= 1;
                                    guest.entrance_count = Integer.toString(enter);
                                    guest.status = "on";
                                    writeGuest(guest, user_id, meet_id);
                                    check_result = true;
                                    valid_info_label.setText(guest.name +
                                            ", добро пожаловать!\nЧисло разрешённых входов: " +
                                            guest.entrance_count);
                                } else {
                                    invalid_info_label.setText(guest.name +
                                            " вы израсходовали все пропуски на вход :(");
                                    check_result = false;
                                }
                            }
                            if (check_result) {
                                setContentView(validLayout);
                            } else {
                                setContentView(invalidLayout);
                            }
                        } else { // "on" status
                            if (!Objects.equals(guest.exit_count, "inf")) {
                                database.collection("USERS")
                                        .document(user_id)
                                        .collection("MEETS")
                                        .document(meet_id).get().addOnCompleteListener(task_time -> {
                                            boolean isChecked;
                                            Meetup current_meet = task_time.getResult().toObject(Meetup.class);
                                            assert current_meet != null;

                                            // Если на мероприятии только 1 выход, то проверяем дату и время
                                            if (Integer.parseInt(current_meet.exit_control) == 0) {

                                                ZoneId zoneId = ZoneId.of("Europe/Moscow");

                                                ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
                                                LocalTime localTime = zonedDateTime.toLocalTime();
                                                LocalDate localDate = zonedDateTime.toLocalDate();

                                                int meet_hour = Integer.parseInt(current_meet.end_time.split(":")[0]);
                                                int meet_minute = Integer.parseInt(current_meet.end_time.split(":")[1]);

                                                int meet_day = Integer.parseInt(current_meet.date.split("\\.")[0]);
                                                int meet_month = Integer.parseInt(current_meet.date.split("\\.")[1]);
                                                int meet_year = Integer.parseInt(current_meet.date.split("\\.")[2]);

                                                LocalTime meetTime = LocalTime.of(meet_hour, meet_minute);
                                                LocalDate meetDate = LocalDate.of(meet_year, meet_month, meet_day);

                                                if (!localDate.isAfter(meetDate) && localTime.isAfter(meetTime)) {
                                                    int exit = Integer.parseInt(guest.exit_count);
                                                    if (exit > 0) {
                                                        exit -= 1;
                                                        guest.exit_count = Integer.toString(exit);
                                                        guest.status = "exit";
                                                        writeGuest(guest, user_id, meet_id);
                                                        isChecked = true;
                                                        valid_info_label.setText(guest.name +
                                                                ", до свидания!\nЧисло разрешённых входов: " +
                                                                guest.entrance_count);
                                                    } else {
                                                        invalid_info_label.setText(guest.name +
                                                                " вы израсходовали все пропуски на выход :(");
                                                        isChecked = false;
                                                    }
                                                } else {
                                                    invalid_info_label.setText(guest.name +
                                                            ", вы осуществляете выход раньше положенного времени!\n" +
                                                            "Конец мероприятия в " + current_meet.end_time);
                                                    isChecked = false;
                                                }
                                            } else {
                                                int exit = Integer.parseInt(guest.exit_count);
                                                if (exit > 0) {
                                                    exit -= 1;
                                                    guest.exit_count = Integer.toString(exit);
                                                    guest.status = "exit";
                                                    writeGuest(guest, user_id, meet_id);
                                                    isChecked = true;
                                                    valid_info_label.setText(guest.name +
                                                            ", до свидания!\nЧисло разрешённых входов: " +
                                                            guest.entrance_count);
                                                } else {
                                                    invalid_info_label.setText(guest.name +
                                                            " вы израсходовали все пропуски на выход :(");
                                                    isChecked = false;
                                                }
                                            }

                                            if (isChecked) {
                                                setContentView(validLayout);
                                            } else {
                                                setContentView(invalidLayout);
                                            }
                                        });
                            } else {
                                guest.status = "exit";
                                writeGuest(guest, user_id, meet_id);
                                valid_info_label.setText(guest.name +
                                        " до свидания, приходите ещё!");
                                setContentView(validLayout);
                            }
                        }
                    });
        } else {
            database.collection("USERS")
                    .document(hash)
                    .collection("MEETS")
                    .document(meet_id)
                    .collection(type)
                    .document(id).get().addOnCompleteListener(task -> {
                        boolean check_result;
                        Group group = task.getResult().toObject(Group.class);

                        assert group != null;

                        if (Objects.equals(group.entrance_count, "inf")) {
                            check_result = true;
                        } else {
                            int count = Integer.parseInt(group.entrance_count);
                            if (count > 0) {
                                count -= 1;
                                group.entrance_count = Integer.toString(count);
                                writeGroup(group, user_id, meet_id);
                                check_result = true;
                            } else {
                                check_result = false;
                            }
                        }
                        if (check_result) {
                            setContentView(R.layout.valid_qr_layout);
                        } else {
                            setContentView(R.layout.invalid_qr_layout);
                        }
                    });
        }
    }

    private void writeGuest(@NonNull Guest guest, String user_id, String meet_id) {
        database.collection("USERS")
                .document(user_id)
                .collection("MEETS")
                .document(meet_id)
                .collection("GUESTS").document(guest.id)
                .set(guest);
    }

    private void writeGroup(@NonNull Group group, String user_id, String meet_id) {
        database.collection("USERS")
                .document(user_id)
                .collection("MEETS")
                .document(meet_id)
                .collection("GROUPS").document(group.id)
                .set(group);
    }
}