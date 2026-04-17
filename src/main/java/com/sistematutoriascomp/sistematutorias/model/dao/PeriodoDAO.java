/*
 * Autor: Henrnadez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 4.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sistematutoriascomp.sistematutorias.model.ConexionBaseDatos;
import com.sistematutoriascomp.sistematutorias.model.pojo.Periodo;

public class PeriodoDAO {
    private static final String SQL_SELECT_PERIODO_ACTUAL = "SELECT idPeriodo FROM periodo WHERE esActual = 1";
    private static final String SQL_SELECT_PERIODO_POR_ID = "SELECT idPeriodo, nombre, esActual FROM periodo WHERE idPeriodo = ?";
    private static final String SQL_SELECT_ID_POR_NOMBRE = "SELECT idPeriodo FROM periodo WHERE nombre = ?";
    private static final String SQL_SELECT_TODOS = "SELECT idPeriodo, nombre, esActual FROM periodo";

    public static int obtenerIdPeriodoActual() throws SQLException {
        int idPeriodo = -1;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_SELECT_PERIODO_ACTUAL);
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

    public static Periodo obtenerPeriodoPorId(int idPeriodo) throws SQLException {
        Periodo periodo = null;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_SELECT_PERIODO_POR_ID);
                sentencia.setInt(1, idPeriodo);
                ResultSet resultado = sentencia.executeQuery();

                if (resultado.next()) {
                    periodo = new Periodo();
                    periodo.setIdPeriodo(resultado.getInt("idPeriodo"));
                    periodo.setNombre(resultado.getString("nombre"));
                    periodo.setEsActual(resultado.getBoolean("esActual"));
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return periodo;
    }

    public static int obtenerIdPorNombre(String nombrePeriodo) throws SQLException {
        int idPeriodo = -1;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_SELECT_ID_POR_NOMBRE);
                sentencia.setString(1, nombrePeriodo);
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

    public static List<Periodo> obtenerTodosPeriodos() throws SQLException {
        List<Periodo> resultado = new ArrayList<>();
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_SELECT_TODOS);
                ResultSet rs = sentencia.executeQuery();

                while (rs.next()) {
                    Periodo periodo = new Periodo();
                    periodo.setIdPeriodo(rs.getInt("idPeriodo"));
                    periodo.setNombre(rs.getString("nombre"));
                    periodo.setEsActual(rs.getBoolean("esActual"));
                    resultado.add(periodo);
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return resultado;
    }

    private Periodo mapResultSetToPeriodo(ResultSet resultSet) throws SQLException {
        Periodo periodo = new Periodo();
        periodo.setIdPeriodo(resultSet.getInt("idPeriodo"));
        periodo.setNombre(resultSet.getString("nombre"));
        periodo.setEsActual(resultSet.getBoolean("esActual"));
        return periodo;
    }
}
