package com.example.trainingnotes;

public class ListaEjercicio {
    private String nombre;
    private int imagen;

    public ListaEjercicio(String nombre, int imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }
    public String getNombre() {
        return nombre;
    }
    public int getImagen() {
        return imagen;
    }
}
