package com.example.trainingnotes;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Ejercicio {
    private String nombre;
    @ServerTimestamp
    private Date timestamp;

    public Ejercicio() {
    }

    public Ejercicio(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
