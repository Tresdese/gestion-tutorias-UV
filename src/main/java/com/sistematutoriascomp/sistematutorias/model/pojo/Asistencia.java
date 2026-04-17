/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.pojo;

public class Asistencia {
    private int idAsistencia;
    private int idTutoria;
    private int idTutorado;
    private Boolean asistio;

    public Asistencia() {
    }

    public Asistencia(int idAsistencia, int idTutoria, int idTutorado, Boolean asistio) {
        this.idAsistencia = idAsistencia;
        this.idTutoria = idTutoria;
        this.idTutorado = idTutorado;
        this.asistio = asistio;
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public int getIdTutoria() {
        return idTutoria;
    }

    public void setIdTutoria(int idTutoria) {
        this.idTutoria = idTutoria;
    }

    public int getIdTutorado() {
        return idTutorado;
    }

    public void setIdTutorado(int idTutorado) {
        this.idTutorado = idTutorado;
    }

    public Boolean getAsistio() {
        return asistio;
    }

    public void setAsistio(Boolean asistio) {
        this.asistio = asistio;
    }
}
