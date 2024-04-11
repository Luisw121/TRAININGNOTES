package com.example.trainingnotes;

public class Ejercicio {
    private String EjercicioName;
    public Ejercicio(){}
    public Ejercicio(String EjercicioName){this.EjercicioName = EjercicioName;}

    public String getEjercicioName() {
        return EjercicioName;
    }

    public void setEjercicioName(String ejercicioName) {
        EjercicioName = ejercicioName;
    }
}
