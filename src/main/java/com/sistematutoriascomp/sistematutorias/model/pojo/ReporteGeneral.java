/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.model.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReporteGeneral {
    private int idReporteGeneral;
    private int idPeriodo;
    private String nombrePeriodo;
    private int numeroSesion;
    private int idCoordinador;
    private LocalDateTime fechaGeneracion;
    private String estado;
    private int totalTutorados;
    private int totalEstudiantesRiesgo;
    private int totalTutores;
    private int totalProblematicas;
    private int totalAsistentes;
    private int totalFaltantes;
    private BigDecimal porcentajeAsistencia;
    private String observaciones;
    private List<ReporteTutoria> detallesReportes;

    public ReporteGeneral() {
    }

    public ReporteGeneral(int idReporteGeneral, int idPeriodo, String nombrePeriodo, int numeroSesion, int idCoordinador, LocalDateTime fechaGeneracion, String estado, int totalTutorados, int totalEstudiantesRiesgo, int totalTutores, int totalProblematicas, int totalAsistentes, int totalFaltantes, BigDecimal porcentajeAsistencia, String observaciones, List<ReporteTutoria> detallesReportes) {
        this.idReporteGeneral = idReporteGeneral;
        this.idPeriodo = idPeriodo;
        this.nombrePeriodo = nombrePeriodo;
        this.numeroSesion = numeroSesion;
        this.idCoordinador = idCoordinador;
        this.fechaGeneracion = fechaGeneracion;
        this.estado = estado;
        this.totalTutorados = totalTutorados;
        this.totalEstudiantesRiesgo = totalEstudiantesRiesgo;
        this.totalTutores = totalTutores;
        this.totalProblematicas = totalProblematicas;
        this.totalAsistentes = totalAsistentes;
        this.totalFaltantes = totalFaltantes;
        this.porcentajeAsistencia = porcentajeAsistencia;
        this.observaciones = observaciones;
        this.detallesReportes = detallesReportes;
    }

    public int getIdReporteGeneral() {
        return idReporteGeneral;
    }

    public void setIdReporteGeneral(int idReporteGeneral) {
        this.idReporteGeneral = idReporteGeneral;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public String getNombrePeriodo() {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo) {
        this.nombrePeriodo = nombrePeriodo;
    }

    public int getNumeroSesion() {
        return numeroSesion;
    }

    public void setNumeroSesion(int numeroSesion) {
        this.numeroSesion = numeroSesion;
    }

    public int getIdCoordinador() {
        return idCoordinador;
    }

    public void setIdCoordinador(int idCoordinador) {
        this.idCoordinador = idCoordinador;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getTotalTutorados() {
        return totalTutorados;
    }

    public void setTotalTutorados(int totalTutorados) {
        this.totalTutorados = totalTutorados;
    }

    public int getTotalEstudiantesRiesgo() {
        return totalEstudiantesRiesgo;
    }

    public void setTotalEstudiantesRiesgo(int totalEstudiantesRiesgo) {
        this.totalEstudiantesRiesgo = totalEstudiantesRiesgo;
    }

    public int getTotalTutores() {
        return totalTutores;
    }

    public void setTotalTutores(int totalTutores) {
        this.totalTutores = totalTutores;
    }

    public int getTotalProblematicas() {
        return totalProblematicas;
    }

    public void setTotalProblematicas(int totalProblematicas) {
        this.totalProblematicas = totalProblematicas;
    }

    public int getTotalAsistentes() {
        return totalAsistentes;
    }

    public void setTotalAsistentes(int totalAsistentes) {
        this.totalAsistentes = totalAsistentes;
    }

    public int getTotalFaltantes() {
        return totalFaltantes;
    }

    public void setTotalFaltantes(int totalFaltantes) {
        this.totalFaltantes = totalFaltantes;
    }

    public BigDecimal getPorcentajeAsistencia() {
        return porcentajeAsistencia;
    }

    public void setPorcentajeAsistencia(BigDecimal porcentajeAsistencia) {
        this.porcentajeAsistencia = porcentajeAsistencia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<ReporteTutoria> getDetallesReportes() {
        return detallesReportes;
    }

    public void setDetallesReportes(List<ReporteTutoria> detallesReportes) {
        this.detallesReportes = detallesReportes;
    }
}
