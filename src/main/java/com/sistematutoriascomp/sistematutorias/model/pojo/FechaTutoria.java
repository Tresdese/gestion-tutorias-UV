/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.pojo;

import java.time.LocalDate;

public class FechaTutoria {
    private int idFechaTutoria;
    private int idPeriodo;
    private int numeroSesion;
    private String titulo;
    private String descripcion;
    private LocalDate fecha;

    public FechaTutoria() {
    }

    public FechaTutoria(int idFechaTutoria, int idPeriodo, int numeroSesion, String titulo, String descripcion, LocalDate fecha) {
        this.idFechaTutoria = idFechaTutoria;
        this.idPeriodo = idPeriodo;
        this.numeroSesion = numeroSesion;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Sesión " + numeroSesion + " (" + fecha + ")";
    }

    public int getIdFechaTutoria() {
        return idFechaTutoria;
    }

    public void setIdFechaTutoria(int idFechaTutoria) {
        this.idFechaTutoria = idFechaTutoria;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public int getNumeroSesion() {
        return numeroSesion;
    }

    public void setNumeroSesion(int numeroSesion) {
        this.numeroSesion = numeroSesion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
