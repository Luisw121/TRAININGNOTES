package com.example.trainingnotes;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class block {
    private String name;
    @ServerTimestamp
    private Date timestamp;

    public block() {}

    public block(String name) {
        this.name = name;
        // La propiedad timestamp se establecerá automáticamente en Firestore
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
