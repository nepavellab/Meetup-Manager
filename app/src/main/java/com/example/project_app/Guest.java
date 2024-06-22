package com.example.project_app;

import java.io.Serializable;

// off - не пришёл
// on - пришёл
// exit - вышел

public class Guest implements Serializable {
    public String id, name, email,
            phone_number, QR, entrance_count, exit_count, status;

    public Guest() {
        // default to interface
    }

    public Guest(String _id,
                 String _name,
                 String _email,
                 String _phone_number,
                 String _QR,
                 String _entrance_count,
                 String _exit_count,
                 String _status) {
        id  = _id;
        name = _name;
        email = _email;
        phone_number = _phone_number;
        QR = _QR;
        entrance_count = _entrance_count;
        exit_count = _exit_count;
        status = _status;
    }

    protected String getDataForQr() {
        return "GUESTS=" + name + "=" + id;
    }
}
