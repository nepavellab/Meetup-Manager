package com.example.project_app;

import java.io.Serializable;
import java.util.Objects;

public class Group implements Serializable  {
    public String name, size, id, QR, entrance_count, exit_count;

    public Group () {
        // default to interface
    }

    public Group (String _name,
                  String _size,
                  String _id,
                  String _QR,
                  String _entrance_count) {
        name = _name;
        size = _size;
        id = _id;
        QR = _QR;
        entrance_count = Objects.equals(_entrance_count, "inf") ? "inf" :
                Integer.toString(Integer.parseInt(_entrance_count) * Integer.parseInt(_size));
    }

    protected String getDataForQr() {
        return "GROUPS=" + name + "=" + id ;
    }
}
