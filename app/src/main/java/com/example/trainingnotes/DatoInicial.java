package com.example.trainingnotes;

public class DatoInicial {
    String id;
    private int repeticiones;
    private double peso;
    private double rpe;

    public DatoInicial() {
        // Constructor vacío necesario para Firestore
    }

    public DatoInicial( int repeticiones, double peso, double rpe) {
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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DatoInicial{" +
                "id='" + id + '\'' +
                ", repeticiones=" + repeticiones +
                ", peso=" + peso +
                ", rpe=" + rpe +
                '}';
    }
}
