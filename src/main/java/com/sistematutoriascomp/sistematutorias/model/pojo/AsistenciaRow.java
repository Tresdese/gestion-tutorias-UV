/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.model.pojo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class AsistenciaRow {
    private int idTutorado;
    private String matricula;
    private String nombreCompleto;
    private int semestre;
    private BooleanProperty asistio;

    public AsistenciaRow(int idTutorado, String matricula, String nombreCompleto, int semestre, boolean asistio) {
        this.idTutorado = idTutorado;
        this.matricula = matricula;
        this.nombreCompleto = nombreCompleto;
        this.semestre = semestre;
        this.asistio = new SimpleBooleanProperty(asistio);
    }

    public BooleanProperty asistioProperty() {
        return asistio;
    }

    public int getIdTutorado() {
        return idTutorado;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public int getSemestre() {
        return semestre;
    }

    public boolean isAsistio() {
        return asistio.get();
    }

    public void setAsistio(boolean asistio) {
        this.asistio.set(asistio);
    }
}
