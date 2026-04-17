/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
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
import com.sistematutoriascomp.sistematutorias.model.pojo.AsistenciaRow;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;

public class AsistenciaDAO {
    private static final String SQL_OBTENER_SESIONES_POR_TUTOR = "SELECT idTutoria, fecha, hora_inicio FROM tutoria WHERE idTutor = ? AND idPeriodo = ? ORDER BY fecha DESC";
    private static final String SQL_OBTENER_TUTORADOS_POR_TUTOR = "SELECT DISTINCT t.idTutorado, t.matricula, "
            + "CONCAT(t.nombre, ' ', t.apellidoPaterno, ' ', t.apellidoMaterno) as nombreC, "
            + "t.semestre, "
            + "asi.asistio "
            + "FROM tutorado t "
            + "INNER JOIN asignaciontutor a ON t.idTutorado = a.idTutorado "
            + "LEFT JOIN asistencia asi ON (asi.idTutorado = t.idTutorado AND asi.idTutoria = ?) "
            + "WHERE a.idTutor = ? AND a.idPeriodo = ?";
    private static final String SQL_REGISTRAR_ASISTENCIA = "INSERT INTO asistencia (idTutoria, idTutorado, asistio) VALUES (?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE asistio = VALUES(asistio)";
    private static final String SQL_EXISTE_ASISTENCIA_POR_TUTORIA = "SELECT COUNT(*) AS total FROM asistencia WHERE idTutoria = ?";

    public static List<Tutoria> obtenerSesionesPorTutor(int idTutor, int idPeriodo) throws SQLException {
        List<Tutoria> sesiones = new ArrayList<>();
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_SESIONES_POR_TUTOR);
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

    public static List<AsistenciaRow> obtenerTutoradosPorTutor(int idTutor, int idPeriodo, int idTutoria) throws SQLException {
        List<AsistenciaRow> lista = new ArrayList<>();
        Connection conexion = ConexionBaseDatos.abrirConexionBD();

        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_OBTENER_TUTORADOS_POR_TUTOR);
                ps.setInt(1, idTutoria);
                ps.setInt(2, idTutor);
                ps.setInt(3, idPeriodo);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    boolean estadoAsistencia = rs.getBoolean("asistio");

                    lista.add(new AsistenciaRow(
                            rs.getInt("idTutorado"),
                            rs.getString("matricula"),
                            rs.getString("nombreC"),
                            rs.getInt("semestre"),
                            estadoAsistencia
                    ));
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return lista;
    }

    public static boolean registrarAsistencia(int idTutoria, int idTutorado, boolean asistio) throws SQLException {
        boolean registrado = false;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_REGISTRAR_ASISTENCIA);
                ps.setInt(1, idTutoria);
                ps.setInt(2, idTutorado);
                ps.setBoolean(3, asistio);
                registrado = ps.executeUpdate() > 0;
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return registrado;
    }

    public static boolean existeAsistenciaParaTutoria(int idTutoria) throws SQLException {
        boolean existe = false;
        Connection conexion = ConexionBaseDatos.abrirConexionBD();
        if (conexion != null) {
            try {
                PreparedStatement ps = conexion.prepareStatement(SQL_EXISTE_ASISTENCIA_POR_TUTORIA);
                ps.setInt(1, idTutoria);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    existe = rs.getInt("total") > 0;
                }
            } finally {
                ConexionBaseDatos.cerrarConexionBD();
            }
        }
        return existe;
    }
}
