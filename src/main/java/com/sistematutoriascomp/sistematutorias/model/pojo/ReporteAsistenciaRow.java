package com.sistematutoriascomp.sistematutorias.model.pojo;

public class ReporteAsistenciaRow {
    private String matricula;
    private String nombreCompleto;
    private int semestre;
    private String fechaSesion;
    private String asistencia;

    public ReporteAsistenciaRow(String matricula, String nombreCompleto, int semestre,
            String fechaSesion, boolean asistio) {
        this.matricula = matricula;
        this.nombreCompleto = nombreCompleto;
        this.semestre = semestre;
        this.fechaSesion = fechaSesion;
        this.asistencia = asistio ? "Asistió" : "No asistió";
    }

    public String getMatricula() { return matricula; }
    public String getNombreCompleto() { return nombreCompleto; }
    public int getSemestre() { return semestre; }
    public String getFechaSesion() { return fechaSesion; }
    public String getAsistencia() { return asistencia; }
}
