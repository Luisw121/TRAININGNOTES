package com.example.trainingnotes;

public class DatoInicial {
    private int repeticiones;
    private double peso;
    private double rpe;

    public DatoInicial() {
        // Constructor vacío necesario para Firestore
    }

    public DatoInicial(int repeticiones, double peso, double rpe) {
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.rpe = rpe;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getRpe() {
        return rpe;
    }

    public void setRpe(double rpe) {
        this.rpe = rpe;
    }
}
