/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 6.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sistematutoriascomp.sistematutorias.model.ConexionBaseDatos;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteGeneral;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteTutoria;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;

public class ReporteTutoriaDAO {
    private static final String SQL_OBTENER_SESIONES_PENDIENTES = "SELECT t.idTutoria, t.fecha, t.hora_inicio "
            + "FROM tutoria t "
            + "LEFT JOIN reportetutoria r ON r.idTutoria = t.idTutoria "
            + "WHERE t.idTutor = ? AND t.idPeriodo = ? "
            + "AND r.idTutoria IS NULL "
            + "AND EXISTS (SELECT 1 FROM asistencia a WHERE a.idTutoria = t.idTutoria) "
            + "ORDER BY t.fecha, t.hora_inicio";
    private static final String SQL_OBTENER_TOTALES_ASISTENCIA = "SELECT COUNT(*) as total, "
            + "SUM(CASE WHEN asistio = 1 THEN 1 ELSE 0 END) as asistentes, "
            + "SUM(CASE WHEN asistio = 0 THEN 1 ELSE 0 END) as faltantes "
            + "FROM asistencia WHERE idTutoria = ?";
    private static final String SQL_OBTENER_TOTAL_PROBLEMATICAS = "SELECT COUNT(*) as total FROM problematica WHERE idTutoria = ?";
    private static final String SQL_REGISTRAR_REPORTE = "INSERT INTO reportetutoria (idTutoria, fechaGeneracion, observaciones, estatus) "
            + "VALUES (?, NOW(), ?, ?)";
    private static final String SQL_OBTENER_REPORTES_POR_TUTOR = "SELECT r.*, t.idPeriodo, t.fecha, p.nombre as nombrePeriodo "
            + "FROM reportetutoria r "
            + "INNER JOIN tutoria t ON r.idTutoria = t.idTutoria "
            + "INNER JOIN periodo p ON t.idPeriodo = p.idPeriodo "
            + "WHERE t.idTutor = ?";
    private static final String SQL_ENVIAR_REPORTE = "UPDATE reportetutoria SET estatus = 'ENVIADO' WHERE idReporteTutoria = ?";
    private static final String SQL_OBTENER_REPORTES_POR_PERIODO = "SELECT r.*, t.idPeriodo, t.fecha, tu.nombre, tu.apellidoPaterno, tu.apellidoMaterno "
            + "FROM reportetutoria r "
            + "INNER JOIN tutoria t ON r.idTutoria = t.idTutoria "
            + "INNER JOIN tutor tu ON t.idTutor = tu.idTutor "
            + "WHERE t.idPeriodo = ? "
            + "ORDER BY r.estatus ASC, t.fecha DESC";
    private static final String SQL_REGISTRAR_RESPUESTA = "UPDATE reportetutoria SET respuesta = ?, estatus = 'REVISADO' WHERE idReporteTutoria = ?";
    private static final String SQL_OBTENER_REPORTES_POR_TUTOR_Y_PERIODO = "SELECT r.*, t.fecha "
            + "FROM reportetutoria r "
            + "INNER JOIN tutoria t ON r.idTutoria = t.idTutoria "
            + "WHERE t.idTutor = ? AND t.idPeriodo = ? "
            + "ORDER BY t.fecha DESC";
    private static final String SQL_OBTENER_DATOS_REPORTE_GENERAL = "SELECT "
            + "COUNT(DISTINCT rt.idReporteTutoria) as totalTutores, "
            + "COUNT(a.idAsistencia) as totalTutorados, "
            + "SUM(CASE WHEN a.asistio = 1 THEN 1 ELSE 0 END) as totalAsistentes, "
            + "SUM(CASE WHEN a.asistio = 0 THEN 1 ELSE 0 END) as totalFaltantes "
            + "FROM reportetutoria rt "
            + "INNER JOIN tutoria t ON rt.idTutoria = t.idTutoria "
            + "INNER JOIN asistencia a ON rt.idTutoria = a.idTutoria "
            + "WHERE t.fecha = (SELECT fecha FROM fechatutoria WHERE idFechaTutoria = ?)";

    public static List<Tutoria> obtenerSesionesPendientes(int idTutor, int idPeriodo) throws SQLException {
        List<Tutoria> sesiones = new ArrayList<>();
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_SESIONES_PENDIENTES);
                ps.setInt(1, idTutor);
                ps.setInt(2, idPeriodo);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Tutoria t = new Tutoria();
                    t.setIdTutoria(rs.getInt("idTutoria"));
                    t.setFecha(rs.getDate("fecha").toLocalDate());
                    t.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                    sesiones.add(t);
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return sesiones;
    }

    public static HashMap<String, Integer> obtenerTotales(int idTutoria) throws SQLException {
        HashMap<String, Integer> totales = new HashMap<>();
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_TOTALES_ASISTENCIA);
                ps.setInt(1, idTutoria);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    totales.put("tutorados", rs.getInt("total"));
                    totales.put("asistentes", rs.getInt("asistentes"));
                    totales.put("faltantes", rs.getInt("faltantes"));
                }

                PreparedStatement ps2 = conexion.prepareStatement(SQL_OBTENER_TOTAL_PROBLEMATICAS);
                ps2.setInt(1, idTutoria);
                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) {
                    totales.put("problematicas", rs2.getInt("total"));
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return totales;
    }

    public static boolean registrarReporte(ReporteTutoria reporte) throws SQLException {
        boolean resultado = false;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_REGISTRAR_REPORTE);
                ps.setInt(1, reporte.getIdTutoria());
                ps.setString(2, reporte.getObservaciones());
                ps.setString(3, "BORRADOR");

                resultado = (ps.executeUpdate() > 0);
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return resultado;
    }

    public static List<ReporteTutoria> obtenerReportesPorTutor(int idTutor) throws SQLException {
        List<ReporteTutoria> reportes = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.abrirConexionBD()) {
            if (conexion != null) {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_REPORTES_POR_TUTOR);
                ps.setInt(1, idTutor);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    ReporteTutoria r = new ReporteTutoria();
                    r.setIdReporteTutoria(rs.getInt("idReporteTutoria"));
                    r.setIdTutoria(rs.getInt("idTutoria"));
                    java.sql.Timestamp fechaSQL = rs.getTimestamp("fechaGeneracion");

                    if (fechaSQL != null) {
                        r.setFechaGeneracion(fechaSQL.toLocalDateTime());
                    }

                    r.setObservaciones(rs.getString("observaciones"));
                    r.setEstatus(rs.getString("estatus"));
                    r.setRespuesta(rs.getString("respuesta"));
                    reportes.add(r);
                }
            }
        }
        return reportes;
    }

    public static boolean enviarReporte(int idReporte) throws SQLException {
        boolean resultado = false;

        try (Connection conexion = ConexionBaseDatos.abrirConexionBD()) {
            if (conexion != null) {
                PreparedStatement ps = conexion.prepareStatement(SQL_ENVIAR_REPORTE);
                ps.setInt(1, idReporte);
                resultado = ps.executeUpdate() > 0;
            }
        }
        return resultado;
    }

    public static List<ReporteTutoria> obtenerReportesPorPeriodo(int idPeriodo) throws SQLException {
        List<ReporteTutoria> reportes = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.abrirConexionBD()) {
            if (conexion != null) {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_REPORTES_POR_PERIODO);
                ps.setInt(1, idPeriodo);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    ReporteTutoria r = new ReporteTutoria();
                    r.setIdReporteTutoria(rs.getInt("idReporteTutoria"));
                    r.setIdTutoria(rs.getInt("idTutoria"));

                    java.sql.Timestamp fechaSQL = rs.getTimestamp("fechaGeneracion");
                    if (fechaSQL != null) {
                        r.setFechaGeneracion(fechaSQL.toLocalDateTime());
                    }

                    r.setObservaciones(rs.getString("observaciones"));
                    r.setEstatus(rs.getString("estatus"));
                    r.setRespuesta(rs.getString("respuesta"));

                    String nombreCompleto = rs.getString("nombre") + " "
                            + rs.getString("apellidoPaterno") + " "
                            + rs.getString("apellidoMaterno");
                    r.setNombreTutor(nombreCompleto);

                    reportes.add(r);
                }
            }
        }
        return reportes;
    }

    public static boolean registrarRespuesta(int idReporte, String respuesta) throws SQLException {
        boolean resultado = false;

        try (Connection conexion = ConexionBaseDatos.abrirConexionBD()) {
            if (conexion != null) {
                PreparedStatement ps = conexion.prepareStatement(SQL_REGISTRAR_RESPUESTA);
                ps.setString(1, respuesta);
                ps.setInt(2, idReporte);
                resultado = ps.executeUpdate() > 0;
            }
        }
        return resultado;
    }

    public static List<ReporteTutoria> obtenerReportesPorTutorYPeriodo(int idTutor, int idPeriodo) throws SQLException {
        List<ReporteTutoria> reportes = new ArrayList<>();

        try (Connection conexion = ConexionBaseDatos.abrirConexionBD()) {
            if (conexion != null) {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_REPORTES_POR_TUTOR_Y_PERIODO);
                ps.setInt(1, idTutor);
                ps.setInt(2, idPeriodo);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    ReporteTutoria r = new ReporteTutoria();
                    r.setIdReporteTutoria(rs.getInt("idReporteTutoria"));
                    r.setIdTutoria(rs.getInt("idTutoria"));

                    java.sql.Timestamp fechaSQL = rs.getTimestamp("fechaGeneracion");
                    if (fechaSQL != null) {
                        r.setFechaGeneracion(fechaSQL.toLocalDateTime());
                    }

                    r.setObservaciones(rs.getString("observaciones"));
                    r.setEstatus(rs.getString("estatus"));
                    r.setRespuesta(rs.getString("respuesta"));
                    reportes.add(r);
                }
            }
        }

        return reportes;
    }

    public static ReporteGeneral obtenerDatosReporteGeneral(int idPeriodo, int idFechaTutoria) throws SQLException {
        ReporteGeneral reporte = null;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement sentencia = conexion.prepareStatement(SQL_OBTENER_DATOS_REPORTE_GENERAL);
                sentencia.setInt(1, idFechaTutoria);

                ResultSet resultado = sentencia.executeQuery();

                if (resultado.next()) {
                    reporte = new ReporteGeneral();
                    reporte.setIdPeriodo(idPeriodo);
                    reporte.setTotalTutores(resultado.getInt("totalTutores"));
                    reporte.setTotalTutorados(resultado.getInt("totalTutorados"));
                    reporte.setTotalAsistentes(resultado.getInt("totalAsistentes"));
                    reporte.setTotalFaltantes(resultado.getInt("totalFaltantes"));
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return reporte;
    }
}
