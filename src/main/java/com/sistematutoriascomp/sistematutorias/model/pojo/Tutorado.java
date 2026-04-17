/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.pojo;

public class Tutorado {
    private int idTutorado;
    private String matricula;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private int idCarrera;
    private int semestre;
    private boolean esActivo;
    private int idTutor;

    public Tutorado() {
    }

    public Tutorado(int idTutorado, String matricula, String nombre, String apellidoPaterno, String apellidoMaterno, String correo, int idCarrera, int semestre, boolean esActivo, int idTutor) {
        this.idTutorado = idTutorado;
        this.matricula = matricula;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.correo = correo;
        this.idCarrera = idCarrera;
        this.semestre = semestre;
        this.esActivo = esActivo;
        this.idTutor = idTutor;
    }

    @Override
    public String toString() {
        return "Tutorado{" + "idTutorado=" + idTutorado + ", matricula=" + matricula + ", nombre=" + nombre + ", apellidoPaterno=" + apellidoPaterno + ", apellidoMaterno=" + apellidoMaterno + ", correo=" + correo + ", idCarrera=" + idCarrera + ", semestre=" + semestre + ", esActivo=" + esActivo + ", idTutor=" + idTutor + '}';
    }

    public int getIdTutorado() {
        return idTutorado;
    }

    public void setIdTutorado(int idTutorado) {
        this.idTutorado = idTutorado;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(int idCarrera) {
        this.idCarrera = idCarrera;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public boolean isActivo() {
        return esActivo;
    }

    public void setActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }

    public int getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(int idTutor) {
        this.idTutor = idTutor;
    }
}
