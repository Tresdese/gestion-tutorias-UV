/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.pojo;

public class Periodo {
    private int idPeriodo;
    private String nombre;
    private boolean esActual;

    public Periodo() {
    }

    public Periodo(int idPeriodo, String nombre, boolean esActual) {
        this.idPeriodo = idPeriodo;
        this.nombre = nombre;
        this.esActual = esActual;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEsActual() {
        return esActual;
    }

    public void setEsActual(boolean esActual) {
        this.esActual = esActual;
    }
}
