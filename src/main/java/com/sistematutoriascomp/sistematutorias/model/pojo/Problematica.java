/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.pojo;

import java.time.LocalDate;

public class Problematica {
    private int idProblematica;
    private int idTutorado;
    private int idTutoria;
    private String titulo;
    private LocalDate fecha;
    private String descripcion;
    private String estatus;

    public Problematica() {
    }

    public Problematica(int idProblematica, int idTutorado, int idTutoria, String titulo, LocalDate fecha, String descripcion, String estatus) {
        this.idProblematica = idProblematica;
        this.idTutorado = idTutorado;
        this.idTutoria = idTutoria;
        this.titulo = titulo;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.estatus = estatus;
    }

    public int getIdProblematica() {
        return idProblematica;
    }

    public void setIdProblematica(int idProblematica) {
        this.idProblematica = idProblematica;
    }

    public int getIdTutorado() {
        return idTutorado;
    }

    public void setIdTutorado(int idTutorado) {
        this.idTutorado = idTutorado;
    }

    public int getIdTutoria() {
        return idTutoria;
    }

    public void setIdTutoria(int idTutoria) {
        this.idTutoria = idTutoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
