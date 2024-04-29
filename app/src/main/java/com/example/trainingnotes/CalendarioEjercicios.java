package com.example.trainingnotes;

public class CalendarioEjercicios {
    private String nombre;
    private String block;
    private String elemento;

    public CalendarioEjercicios(){}
    public CalendarioEjercicios(String nombre, String block, String elemento){
        this.nombre = nombre;
        this.block = block;
        this.elemento = elemento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }
}
