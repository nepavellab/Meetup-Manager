package com.example.project_app;

import java.io.Serializable;
import java.util.ArrayList;

// ФОРМАТ ID МЕРОПРИЯТИЯ: "строковый ID" + "?" + "имя мероприятия" + "?" + "цифровой id"

public class MeetUpCard implements Serializable {
    public String name, address, date, start_time, end_time;

    // Массив гостей
    public ArrayList<Guest> guests;

    public MeetUpCard() {
        // default to interface
        guests = new ArrayList<>();
    }

    public MeetUpCard(String _name,
                      String _address,
                      String _date,
                      String _start_time,
                      String _end_time) {
        guests = new ArrayList<>();
        name       = _name;
        address    = _address;
        date       = _date;
        start_time = _start_time;
        end_time   = _end_time;
    }
}
