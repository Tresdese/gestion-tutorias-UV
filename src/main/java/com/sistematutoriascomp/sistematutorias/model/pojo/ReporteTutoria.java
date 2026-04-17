/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.pojo;

import java.time.LocalDateTime;

public class ReporteTutoria {
    private int idReporteTutoria;
    private int idTutoria;
    private LocalDateTime fechaGeneracion;
    private String observaciones;
    private String estatus;
    private String respuesta;
    private String nombreTutor;

    public ReporteTutoria() {
    }

    public ReporteTutoria(int idReporteTutoria, int idTutoria, LocalDateTime fechaGeneracion, String observaciones) {
        this.idReporteTutoria = idReporteTutoria;
        this.idTutoria = idTutoria;
        this.fechaGeneracion = fechaGeneracion;
        this.observaciones = observaciones;
    }

    public int getIdReporteTutoria() {
        return idReporteTutoria;
    }

    public void setIdReporteTutoria(int idReporteTutoria) {
        this.idReporteTutoria = idReporteTutoria;
    }

    public int getIdTutoria() {
        return idTutoria;
    }

    public void setIdTutoria(int idTutoria) {
        this.idTutoria = idTutoria;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getFechaFormato() {
        return fechaGeneracion != null ? fechaGeneracion.toString().replace("T", " ") : "";
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getNombreTutor() {
        return nombreTutor;
    }

    public void setNombreTutor(String nombreTutor) {
        this.nombreTutor = nombreTutor;
    }
}
