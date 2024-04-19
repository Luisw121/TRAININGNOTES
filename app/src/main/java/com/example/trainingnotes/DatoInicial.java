package com.example.trainingnotes;

public class DatoInicial {
    String id;
    private int repeticiones;
    private int peso;
    private int rpe;

    public DatoInicial() {
        // Constructor vacío necesario para Firestore
    }

    public DatoInicial( int repeticiones, int peso, int rpe) {
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

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
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
