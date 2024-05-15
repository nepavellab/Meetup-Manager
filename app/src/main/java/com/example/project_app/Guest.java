package com.example.project_app;

import java.io.Serializable;

public class Guest implements Serializable {
    public String id, name, email, phone_number;

    public Guest() {
        // default to interface
    }

    public Guest(String _id,
                 String _name,
                 String _email,
                 String _phone_number) {
        this.id = _id;
        this.name = _name;
        this.email = _email;
        this.phone_number = _phone_number;
    }
}
