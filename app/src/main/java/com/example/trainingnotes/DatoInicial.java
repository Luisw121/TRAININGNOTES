package com.example.trainingnotes;

public class DatoInicial {
    private String dato1;
    private int dato2;

    public DatoInicial() {
        // Constructor vacío necesario para Firestore
    }

    public DatoInicial(String dato1, int dato2) {
        this.dato1 = dato1;
        this.dato2 = dato2;
    }

    public String getDato1() {
        return dato1;
    }

    public void setDato1(String dato1) {
        this.dato1 = dato1;
    }

    public int getDato2() {
        return dato2;
    }

    public void setDato2(int dato2) {
        this.dato2 = dato2;
    }
}
