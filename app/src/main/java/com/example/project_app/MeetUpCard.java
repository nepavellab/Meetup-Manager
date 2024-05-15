package com.example.project_app;


import java.io.Serializable;
import java.util.ArrayList;

public class MeetUpCard implements Serializable {
    public String name, address, date, start_time, end_time;

    // Массив гостей
    public ArrayList<Guest> guests;

    public MeetUpCard() {
        // default to interface
        guests = new ArrayList<>();
    }

    public MeetUpCard(String name,
                      String address,
                      String date,
                      String start_time,
                      String end_time) {
        guests = new ArrayList<>();
        this.name       = name;
        this.address    = address;
        this.date       = date;
        this.start_time = start_time;
        this.end_time   = end_time;
    }
}
