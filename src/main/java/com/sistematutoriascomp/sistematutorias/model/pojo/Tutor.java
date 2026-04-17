/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.pojo;

public class Tutor {
    private int idTutor;
    private String numeroDePersonal;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String password;
    private int idRol;
    private boolean esActivo;
    private int idCarrera;
    private int cantidadTutorados;

    public Tutor() {
    }

    public Tutor(int idTutor, String numeroDePersonal, String nombre, String apellidoPaterno, String apellidoMaterno, String correo, String password, int idRol, boolean esActivo, int idCarrera, int cantidadTutorados) {
        this.idTutor = idTutor;
        this.numeroDePersonal = numeroDePersonal;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.correo = correo;
        this.password = password;
        this.idRol = idRol;
        this.esActivo = esActivo;
        this.idCarrera = idCarrera;
        this.cantidadTutorados = cantidadTutorados;
    }

    @Override
    public String toString() {
        return "Tutor{" + "idTutor=" + idTutor + ", numeroDePersonal=" + numeroDePersonal + ", nombre=" + nombre + ", apellidoPaterno=" + apellidoPaterno + ", apellidoMaterno=" + apellidoMaterno + ", correo=" + correo + ", password=" + password + ", idRol=" + idRol + ", esActivo=" + esActivo + ", idCarrera=" + idCarrera + ", cantidadTutorados=" + cantidadTutorados + '}';
    }

    public int getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(int idTutor) {
        this.idTutor = idTutor;
    }

    public String getNumeroDePersonal() {
        return numeroDePersonal;
    }

    public void setNumeroDePersonal(String numeroDePersonal) {
        this.numeroDePersonal = numeroDePersonal;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public boolean isEsActivo() {
        return esActivo;
    }

    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }

    public int getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(int idCarrera) {
        this.idCarrera = idCarrera;
    }

    public int getCantidadTutorados() {
        return cantidadTutorados;
    }

    public void setCantidadTutorados(int cantidadTutorados) {
        this.cantidadTutorados = cantidadTutorados;
    }
}
