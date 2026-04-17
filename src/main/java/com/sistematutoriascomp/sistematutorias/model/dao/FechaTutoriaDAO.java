/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 5.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sistematutoriascomp.sistematutorias.model.ConexionBaseDatos;
import com.sistematutoriascomp.sistematutorias.model.pojo.FechaTutoria;

public class FechaTutoriaDAO {
    private static final String SQL_OBTENER_FECHAS_POR_PERIODO = "SELECT * FROM fechatutoria WHERE idPeriodo = ? ORDER BY numeroSesion ASC";
    private static final String SQL_VALIDAR_FECHA_REGISTRADA = "SELECT count(*) FROM fechatutoria WHERE idPeriodo = ? AND numeroSesion = ?";
    private static final String SQL_INSERT_FECHA_TUTORIA = "INSERT INTO fechatutoria (idPeriodo, numeroSesion, fecha, titulo, descripcion) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_OBTENER_PERIODO_ACTUAL = "SELECT idPeriodo FROM periodo WHERE esActual = 1 LIMIT 1";
    private static final String SQL_MAX_NUMERO_SESION = "SELECT MAX(numeroSesion) FROM fechatutoria WHERE idPeriodo = ?";

    public static List<FechaTutoria> obtenerFechasPorPeriodo(int idPeriodo) throws SQLException {
        List<FechaTutoria> fechas = new ArrayList<>();
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_OBTENER_FECHAS_POR_PERIODO);
                sentencia.setInt(1, idPeriodo);
                ResultSet resultado = sentencia.executeQuery();

                while (resultado.next()) {
                    FechaTutoria fechaT = new FechaTutoria();
                    fechaT.setIdFechaTutoria(resultado.getInt("idFechaTutoria"));
                    fechaT.setIdPeriodo(resultado.getInt("idPeriodo"));
                    fechaT.setNumeroSesion(resultado.getInt("numeroSesion"));
                    fechaT.setFecha(resultado.getDate("fecha").toLocalDate());
                    if (hasColumn(resultado, "titulo")) {
                        fechaT.setTitulo(resultado.getString("titulo"));
                    }
                    if (hasColumn(resultado, "descripcion")) {
                        fechaT.setDescripcion(resultado.getString("descripcion"));
                    }
                    fechas.add(fechaT);
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return fechas;
    }

    public static boolean validarFechaRegistrada(int idPeriodo, int numeroSesion) throws SQLException {
        boolean existe = false;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_VALIDAR_FECHA_REGISTRADA);
                sentencia.setInt(1, idPeriodo);
                sentencia.setInt(2, numeroSesion);
                ResultSet resultado = sentencia.executeQuery();

                if (resultado.next() && resultado.getInt(1) > 0) {
                    existe = true;
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return existe;
    }

    public static boolean registrarFechaTutoria(FechaTutoria fechaTutoria) throws SQLException {
        boolean resultado = false;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERT_FECHA_TUTORIA);
                sentencia.setInt(1, fechaTutoria.getIdPeriodo());
                sentencia.setInt(2, fechaTutoria.getNumeroSesion());
                sentencia.setDate(3, Date.valueOf(fechaTutoria.getFecha()));
                sentencia.setString(4, fechaTutoria.getTitulo());
                sentencia.setString(5, fechaTutoria.getDescripcion());

                resultado = (sentencia.executeUpdate() > 0);
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return resultado;
    }

    public static int obtenerIdPeriodoActual() throws SQLException {
        int idPeriodo = 0;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_OBTENER_PERIODO_ACTUAL);
                ResultSet resultado = sentencia.executeQuery();
                if (resultado.next()) {
                    idPeriodo = resultado.getInt("idPeriodo");
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return idPeriodo;
    }

    public static int comprobarSiguienteSesion(int idPeriodo) throws SQLException {
        int siguiente = 1;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_MAX_NUMERO_SESION);
                sentencia.setInt(1, idPeriodo);
                ResultSet resultado = sentencia.executeQuery();

                if (resultado.next()) {
                    siguiente = resultado.getInt(1) + 1;
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return siguiente;
    }

    private static boolean hasColumn(ResultSet rs, String columnLabel) {
        boolean existe = false;
        try {
            rs.findColumn(columnLabel);
            existe = true;
        } catch (SQLException ex) {
            existe = false;
        } catch (Exception e) {
            existe = false;
        }
        return existe;
    }
}
