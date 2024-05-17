package com.example.trainingnotes;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class entrenamiento {
    private String blockName;
    @ServerTimestamp
    private Date timestamp;

    public entrenamiento() {
        // Constructor vacío requerido por Firestore
    }

    public entrenamiento(String blockName) {
        this.blockName = blockName;
        // La propiedad timestamp se establecerá automáticamente en Firestore
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}


