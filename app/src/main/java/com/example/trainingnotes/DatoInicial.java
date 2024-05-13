package com.example.trainingnotes;

public class DatoInicial {
    private String id;
    private int repeticiones;
    private float peso;
    private int rpe;

    public DatoInicial() {
        // Constructor vacío necesario para Firestore
    }

    public DatoInicial( int repeticiones, float peso, int rpe) {
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

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public int getRpe() {
        return rpe;
    }

    public void setRpe(int rpe) {
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
