/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 3.0
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
import com.sistematutoriascomp.sistematutorias.model.pojo.Problematica;

public class ProblematicaDAO {
    private static final String SQL_INSERT_PROBLEMATICA = "INSERT INTO problematica (idTutorado, idTutoria, titulo, descripcion, fecha, estatus) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_OBTENER_PROBLEMATICAS_POR_TUTOR =
            "SELECT p.idProblematica, p.idTutorado, p.idTutoria, p.titulo, p.descripcion, p.fecha, p.estatus "
            + "FROM problematica p "
            + "INNER JOIN tutoria tu ON p.idTutoria = tu.idTutoria "
            + "WHERE tu.idTutor = ? AND tu.idPeriodo = ? "
            + "ORDER BY p.fecha DESC";
    private static final String SQL_SELECT_PROBLEMATICAS_POR_FECHA = "SELECT p.titulo, p.descripcion "
            + "FROM problematica p "
            + "INNER JOIN tutoria t ON p.idTutoria = t.idTutoria "
            + "WHERE t.fecha = (SELECT fecha FROM fechatutoria WHERE idFechaTutoria = ?)";
    private static final String SQL_SELECT_PROBLEMATICAS_POR_TUTORIA = "SELECT titulo, descripcion FROM problematica WHERE idTutoria = ?";

    public static boolean registrarProblematica(Problematica problematica) throws SQLException {
        boolean resultado = false;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERT_PROBLEMATICA);
                sentencia.setInt(1, problematica.getIdTutorado());
                sentencia.setInt(2, problematica.getIdTutoria());
                sentencia.setString(3, problematica.getTitulo());
                sentencia.setString(4, problematica.getDescripcion());
                sentencia.setDate(5, Date.valueOf(problematica.getFecha()));
                sentencia.setString(6, "ABIERTA");
                int filasAfectadas = sentencia.executeUpdate();
                resultado = (filasAfectadas > 0);
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return resultado;
    }

    public static List<Problematica> obtenerProblematicasPorTutor(int idTutor, int idPeriodo) throws SQLException {
        List<Problematica> lista = new ArrayList<>();
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_PROBLEMATICAS_POR_TUTOR);
                ps.setInt(1, idTutor);
                ps.setInt(2, idPeriodo);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Problematica p = new Problematica();
                    p.setIdProblematica(rs.getInt("idProblematica"));
                    p.setIdTutorado(rs.getInt("idTutorado"));
                    p.setIdTutoria(rs.getInt("idTutoria"));
                    p.setTitulo(rs.getString("titulo"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setFecha(rs.getDate("fecha").toLocalDate());
                    p.setEstatus(rs.getString("estatus"));
                    lista.add(p);
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return lista;
    }

    public static List<Problematica> obtenerProblematicasPorFecha(int idFechaTutoria) throws SQLException {
        List<Problematica> lista = new ArrayList<>();
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_SELECT_PROBLEMATICAS_POR_FECHA);
                sentencia.setInt(1, idFechaTutoria);
                ResultSet resultado = sentencia.executeQuery();

                while (resultado.next()) {
                    Problematica prob = new Problematica();
                    prob.setTitulo(resultado.getString("titulo"));
                    prob.setDescripcion(resultado.getString("descripcion"));
                    lista.add(prob);
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return lista;
    }

    public static List<Problematica> obtenerProblematicasPorTutoria(int idTutoria) throws SQLException {
        List<Problematica> lista = new ArrayList<>();
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_SELECT_PROBLEMATICAS_POR_TUTORIA);
                ps.setInt(1, idTutoria);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Problematica p = new Problematica();
                    p.setTitulo(rs.getString("titulo"));
                    p.setDescripcion(rs.getString("descripcion"));
                    lista.add(p);
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return lista;
    }
}
