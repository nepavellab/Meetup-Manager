package com.example.project_app;

import java.io.Serializable;

// ФОРМАТ ID ГОСТЯ: "строковый ID" + "?" + "имя мероприятия" + "?" + "цифровой id"

public class Guest implements Serializable {
    public String id, name, email, phone_number;

    public Guest() {
        // default to interface
    }

    public Guest(String _id,
                 String _name,
                 String _email,
                 String _phone_number) {
        id = _id;
        name = _name;
        email = _email;
        phone_number = _phone_number;
    }

    protected String getDataForQr() {
        return id + " " + name + " " + email + " " + phone_number;
    }
}
