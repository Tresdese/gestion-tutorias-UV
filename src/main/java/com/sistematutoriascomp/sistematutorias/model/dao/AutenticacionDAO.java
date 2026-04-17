/*
 * Autor: Henrnadez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sistematutoriascomp.sistematutorias.model.ConexionBaseDatos;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;

public class AutenticacionDAO {
    private static final String SQL_VERIFICAR_SESION_TUTOR = "SELECT * FROM tutor WHERE numeroDePersonal = ? AND password = ?";

    public static Tutor verificarSesionTutor(String numeroPersonal, String password) throws SQLException {
        Tutor tutor = null;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            PreparedStatement prepararSentencia = conexion.prepareStatement(SQL_VERIFICAR_SESION_TUTOR);
            prepararSentencia.setString(1, numeroPersonal);
            prepararSentencia.setString(2, password);

            ResultSet resultado = prepararSentencia.executeQuery();

            if (resultado.next()) {
                tutor = new Tutor();
                tutor.setIdTutor(resultado.getInt("idTutor"));
                tutor.setNumeroDePersonal(resultado.getString("numeroDePersonal"));
                tutor.setNombre(resultado.getString("nombre"));
                tutor.setApellidoPaterno(resultado.getString("apellidoPaterno"));
                tutor.setApellidoMaterno(resultado.getString("apellidoMaterno"));
                tutor.setCorreo(resultado.getString("correo"));
                tutor.setPassword(resultado.getString("password"));
                tutor.setIdRol(resultado.getInt("idRol"));
                tutor.setEsActivo(resultado.getBoolean("esActivo"));
                tutor.setIdCarrera(resultado.getInt("idCarrera"));
            }
        }

        return tutor;
    }
}
