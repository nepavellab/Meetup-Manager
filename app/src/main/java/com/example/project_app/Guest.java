package com.example.project_app;

import java.io.Serializable;

public class Guest implements Serializable {
    public String id, name, email, phone_number;

    public Guest() {
        // default to interface
    }

    public Guest(String id,
                 String name,
                 String email,
                 String phone_number) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
    }
}
