/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 4.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.ConexionBaseDatos;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;

public class TutoriaDAO {
    private static final String SQL_INSERT_TUTORIA = "INSERT INTO tutoria (idTutor, idPeriodo, fecha, hora_inicio) VALUES (?, ?, ?, ?)";
    private static final String SQL_COMPROBAR_TUTORIA_REGISTRADA = "SELECT idTutoria FROM tutoria WHERE idTutor = ? AND fecha = ?";
    private static final String SQL_SUBIR_EVIDENCIA = "UPDATE tutoria SET evidencia = ? WHERE idTutoria = ?";
    private static final String SQL_COMPROBAR_EXISTENCIA_EVIDENCIA = "SELECT idTutoria FROM tutoria WHERE idTutoria = ? AND evidencia IS NOT NULL";
    private static final String SQL_OBTENER_ID_TUTOR = "SELECT idTutor FROM tutoria WHERE idTutoria = ?";
    private static final String SQL_OBTENER_EVIDENCIA_POR_TUTORIA = "SELECT evidencia FROM tutoria WHERE idTutoria = ?";
    private static final String SQL_OBTENER_TUTORIAS_POR_TUTOR_PERIODO = "SELECT idTutoria, idTutor, fecha, hora_inicio, idPeriodo FROM tutoria WHERE idTutor = ? AND idPeriodo = ?";
    private static final String SQL_EDITAR_HORA_TUTORIA = "UPDATE tutoria SET hora_inicio = ? WHERE idTutoria = ?";
    private static final String SQL_OBTENER_TUTORIAS_CON_EVIDENCIA = "SELECT idTutoria, idTutor, fecha, hora_inicio, idPeriodo FROM tutoria WHERE idTutor = ? AND idPeriodo = ? AND evidencia IS NOT NULL ORDER BY fecha DESC";
    private static final Logger LOGGER = LogManager.getLogger(TutoriaDAO.class);

    public static int registrarTutoria(Tutoria tutoria) throws SQLException {
        int resultado = 0;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERT_TUTORIA);
                sentencia.setInt(1, tutoria.getIdTutor());
                sentencia.setInt(2, tutoria.getIdPeriodo());
                sentencia.setDate(3, Date.valueOf(tutoria.getFecha()));
                sentencia.setTime(4, Time.valueOf(tutoria.getHoraInicio()));
                resultado = sentencia.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error("Error al registrar tutoría", e);
                throw e;
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return resultado;
    }

    public static boolean comprobarTutoriaRegistrada(int idTutor, LocalDate fecha) throws SQLException {
        boolean registrada = false;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_COMPROBAR_TUTORIA_REGISTRADA);
                sentencia.setInt(1, idTutor);
                sentencia.setDate(2, Date.valueOf(fecha));
                ResultSet resultado = sentencia.executeQuery();
                registrada = resultado.next();
            } catch (SQLException e) {
                LOGGER.error("Error al comprobar tutoría registrada para tutor {} en fecha {}", idTutor, fecha, e);
                throw e;
            } catch (Exception e) {
                LOGGER.error("Error inesperado al comprobar tutoría registrada para tutor {} en fecha {}", idTutor, fecha, e);
                throw new SQLException("Error inesperado al comprobar tutoría registrada", e);
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return registrada;
    }

    public static boolean subirEvidencia(int idTutoria, byte[] evidencia) throws SQLException {
        boolean resultado = false;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_SUBIR_EVIDENCIA);
                sentencia.setBytes(1, evidencia);
                sentencia.setInt(2, idTutoria);

                int filasAfectadas = sentencia.executeUpdate();
                resultado = (filasAfectadas > 0);
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return resultado;
    }

    public static boolean comprobarExistenciaEvidencia(int idTutoria) throws SQLException {
        boolean existe = false;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_COMPROBAR_EXISTENCIA_EVIDENCIA);
                sentencia.setInt(1, idTutoria);
                ResultSet resultado = sentencia.executeQuery();
                existe = resultado.next();
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return existe;
    }

    public static byte[] obtenerEvidencia(int idTutoria) throws SQLException {
        byte[] evidencia = null;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_EVIDENCIA_POR_TUTORIA);
                ps.setInt(1, idTutoria);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    evidencia = rs.getBytes("evidencia");
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return evidencia;
    }

    public static Integer obtenerIdTutorPorTutoria(int idTutoria) throws SQLException {
        Integer idTutor = null;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_ID_TUTOR);
                ps.setInt(1, idTutoria);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idTutor = rs.getInt("idTutor");
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return idTutor;
    }

    public static List<Tutoria> obtenerTutoriasPorTutorPeriodo(int idTutor, int idPeriodo) throws SQLException {
        List<Tutoria> tutorias = new ArrayList<>();
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_TUTORIAS_POR_TUTOR_PERIODO);
                ps.setInt(1, idTutor);
                ps.setInt(2, idPeriodo);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Tutoria t = new Tutoria();
                    t.setIdTutoria(rs.getInt("idTutoria"));
                    t.setIdTutor(rs.getInt("idTutor"));
                    t.setFecha(rs.getDate("fecha").toLocalDate());
                    t.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                    t.setIdPeriodo(rs.getInt("idPeriodo"));
                    tutorias.add(t);
                }
            } catch (SQLException e) {
                LOGGER.error("Error al obtener tutorías por tutor y periodo", e);
                throw e;
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return tutorias;
    }

    public static List<Tutoria> obtenerTutoriasConEvidencia(int idTutor, int idPeriodo) throws SQLException {
        List<Tutoria> tutorias = new ArrayList<>();
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_TUTORIAS_CON_EVIDENCIA);
                ps.setInt(1, idTutor);
                ps.setInt(2, idPeriodo);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Tutoria t = new Tutoria();
                    t.setIdTutoria(rs.getInt("idTutoria"));
                    t.setIdTutor(rs.getInt("idTutor"));
                    t.setFecha(rs.getDate("fecha").toLocalDate());
                    t.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                    t.setIdPeriodo(rs.getInt("idPeriodo"));
                    tutorias.add(t);
                }
            } catch (SQLException e) {
                LOGGER.error("Error al obtener tutorías con evidencia", e);
                throw e;
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return tutorias;
    }

    public static boolean editarHoraTutoria(int idTutoria, LocalTime nuevaHora) throws SQLException {
        boolean resultado = false;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_EDITAR_HORA_TUTORIA);
                ps.setTime(1, Time.valueOf(nuevaHora));
                ps.setInt(2, idTutoria);
                resultado = ps.executeUpdate() > 0;
            } catch (SQLException e) {
                LOGGER.error("Error al editar hora de tutoría {}", idTutoria, e);
                throw e;
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return resultado;
    }
}
