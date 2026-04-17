/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.pojo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Tutoria {
    private int idTutoria;
    private int idTutor;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private int idPeriodo;
    private byte[] evidencia;

    public Tutoria() {
    }

    public Tutoria(int idTutoria, int idTutor, LocalDate fecha, LocalTime horaInicio, int idPeriodo, byte[] evidencia) {
        this.idTutoria = idTutoria;
        this.idTutor = idTutor;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.idPeriodo = idPeriodo;
        this.evidencia = evidencia;
    }

    @Override
    public String toString() {
        return fecha.toString() + " - " + horaInicio.toString();
    }

    public int getIdTutoria() {
        return idTutoria;
    }

    public void setIdTutoria(int idTutoria) {
        this.idTutoria = idTutoria;
    }

    public int getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(int idTutor) {
        this.idTutor = idTutor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public byte[] getEvidencia() {
        return evidencia;
    }

    public void setEvidencia(byte[] evidencia) {
        this.evidencia = evidencia;
    }
}
