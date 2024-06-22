package com.example.project_app;

import java.io.Serializable;

// Формат ключевого слова: <id админа (организатора)> + "?" + <id мероприятия>

public class Meetup implements Serializable {
    public String name, address, date,
            start_time, end_time, id, key_word,
            entrance_control, exit_control;

    public Meetup() {
        // default to interface
    }

    public Meetup(String _name,
                  String _address,
                  String _date,
                  String _start_time,
                  String _end_time,
                  String _id,
                  String _key_word,
                  String _entrance_control,
                  String _exit_control) {
        name = _name;
        address = _address;
        date = _date;
        start_time = _start_time;
        end_time = _end_time;
        id = _id;
        key_word = _key_word;
        entrance_control = _entrance_control;
        exit_control = _exit_control;
    }
}
