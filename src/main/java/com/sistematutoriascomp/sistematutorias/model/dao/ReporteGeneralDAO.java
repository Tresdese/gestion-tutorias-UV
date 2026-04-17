/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 5.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.sistematutoriascomp.sistematutorias.model.ConexionBaseDatos;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteGeneral;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReporteGeneralDAO {
    private static final String SQL_INSERT = "INSERT INTO reportegeneral (idPeriodo, idCoordinador, fechaGeneracion, estado, "
            + "totalTutorados, totalEstudiantesRiesgo, totalTutores, porcentajeAsistencia, "
            + "totalProblematicas, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE reportegeneral SET idPeriodo = ?, idCoordinador = ?, fechaGeneracion = ?, "
            + "estado = ?, totalTutorados = ?, totalEstudiantesRiesgo = ?, totalTutores = ?, "
            + "porcentajeAsistencia = ?, totalProblematicas = ?, observaciones = ? "
            + "WHERE idReporteGeneral = ?";
    private static final String SQL_DELETE = "DELETE FROM reportegeneral WHERE idReporteGeneral = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT rg.*, p.nombre as nombrePeriodo "
            + "FROM reportegeneral rg "
            + "INNER JOIN periodo p ON rg.idPeriodo = p.idPeriodo "
            + "WHERE rg.idReporteGeneral = ?";
    private static final String SQL_SELECT_ALL = "SELECT rg.*, p.nombre as nombrePeriodo "
            + "FROM reportegeneral rg "
            + "INNER JOIN periodo p ON rg.idPeriodo = p.idPeriodo "
            + "ORDER BY rg.fechaGeneracion DESC";
    private static final String SQL_SELECT_BY_PERIODO = "SELECT rg.*, p.nombre as nombrePeriodo "
            + "FROM reportegeneral rg "
            + "INNER JOIN periodo p ON rg.idPeriodo = p.idPeriodo "
            + "WHERE rg.idPeriodo = ? ORDER BY rg.fechaGeneracion DESC";
    private static final String SQL_SELECT_BY_COORDINADOR = "SELECT rg.*, p.nombre as nombrePeriodo "
            + "FROM reportegeneral rg "
            + "INNER JOIN periodo p ON rg.idPeriodo = p.idPeriodo "
            + "WHERE rg.idCoordinador = ? ORDER BY rg.fechaGeneracion DESC";
    private static final String SQL_INSERT_ANSWERS = "UPDATE reportegeneral SET observaciones = ?, estado = ? WHERE idReporteGeneral = ?";

    public boolean insertar(ReporteGeneral reporteGeneral) throws SQLException {
        boolean resultado = false;

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, reporteGeneral.getIdPeriodo());
                statement.setInt(2, reporteGeneral.getIdCoordinador());
                statement.setTimestamp(3, reporteGeneral.getFechaGeneracion() != null
                        ? Timestamp.valueOf(reporteGeneral.getFechaGeneracion()) : null);
                statement.setString(4, reporteGeneral.getEstado());
                statement.setInt(5, reporteGeneral.getTotalTutorados());
                statement.setInt(6, reporteGeneral.getTotalEstudiantesRiesgo());
                statement.setInt(7, reporteGeneral.getTotalTutores());
                statement.setBigDecimal(8, reporteGeneral.getPorcentajeAsistencia());
                statement.setInt(9, reporteGeneral.getTotalProblematicas());
                statement.setString(10, reporteGeneral.getObservaciones());

                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            reporteGeneral.setIdReporteGeneral(generatedKeys.getInt(1));
                        }
                    }
                    resultado = true;
                }
            }
        }

        return resultado;
    }

    public boolean actualizar(ReporteGeneral reporteGeneral) throws SQLException {
        boolean resultado = false;

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(SQL_UPDATE);
                statement.setInt(1, reporteGeneral.getIdPeriodo());
                statement.setInt(2, reporteGeneral.getIdCoordinador());
                statement.setTimestamp(3, reporteGeneral.getFechaGeneracion() != null
                        ? Timestamp.valueOf(reporteGeneral.getFechaGeneracion()) : null);
                statement.setString(4, reporteGeneral.getEstado());
                statement.setInt(5, reporteGeneral.getTotalTutorados());
                statement.setInt(6, reporteGeneral.getTotalEstudiantesRiesgo());
                statement.setInt(7, reporteGeneral.getTotalTutores());
                statement.setBigDecimal(8, reporteGeneral.getPorcentajeAsistencia());
                statement.setInt(9, reporteGeneral.getTotalProblematicas());
                statement.setString(10, reporteGeneral.getObservaciones());
                statement.setInt(11, reporteGeneral.getIdReporteGeneral());

                resultado = statement.executeUpdate() > 0;
            }
        }

        return resultado;
    }

    public boolean actualizarRespuestas(ReporteGeneral reporteGeneral) throws SQLException {
        boolean resultado = false;

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(SQL_INSERT_ANSWERS);
                statement.setString(1, reporteGeneral.getObservaciones());
                statement.setString(2, reporteGeneral.getEstado());
                statement.setInt(3, reporteGeneral.getIdReporteGeneral());

                resultado = statement.executeUpdate() > 0;
            }
        }

        return resultado;
    }

    public boolean eliminar(int idReporteGeneral) throws SQLException {
        boolean resultado = false;

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(SQL_DELETE);
                statement.setInt(1, idReporteGeneral);
                resultado = statement.executeUpdate() > 0;
            }
        }

        return resultado;
    }

    public ReporteGeneral obtenerPorId(int idReporteGeneral) throws SQLException {
        ReporteGeneral reporteGeneral = null;
        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID);
                statement.setInt(1, idReporteGeneral);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        reporteGeneral = mapResultSetToReporteGeneral(resultSet);
                    }
                }
            }
        }

        return reporteGeneral;
    }

    public ObservableList<ReporteGeneral> obtenerTodos() throws SQLException {
        ObservableList<ReporteGeneral> reportes = FXCollections.observableArrayList();

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        reportes.add(mapResultSetToReporteGeneral(resultSet));
                    }
                }
            }
        }

        return reportes;
    }

    public List<ReporteGeneral> obtenerPorPeriodo(int idPeriodo) throws SQLException {
        List<ReporteGeneral> reportes = new ArrayList<>();

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_PERIODO);
                statement.setInt(1, idPeriodo);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        reportes.add(mapResultSetToReporteGeneral(resultSet));
                    }
                }
            }
        }

        return reportes;
    }

    public List<ReporteGeneral> obtenerPorCoordinador(int idCoordinador) throws SQLException {
        List<ReporteGeneral> reportes = new ArrayList<>();

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_COORDINADOR);
                statement.setInt(1, idCoordinador);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        reportes.add(mapResultSetToReporteGeneral(resultSet));
                    }
                }
            }
        }

        return reportes;
    }

    private ReporteGeneral mapResultSetToReporteGeneral(ResultSet resultSet) throws SQLException {
        ReporteGeneral reporte = new ReporteGeneral();
        reporte.setIdReporteGeneral(resultSet.getInt("idReporteGeneral"));
        reporte.setIdPeriodo(resultSet.getInt("idPeriodo"));
        reporte.setIdCoordinador(resultSet.getInt("idCoordinador"));
        Timestamp timestamp = resultSet.getTimestamp("fechaGeneracion");

        if (timestamp != null) {
            reporte.setFechaGeneracion(timestamp.toLocalDateTime());
        }
        reporte.setEstado(resultSet.getString("estado"));
        reporte.setTotalTutorados(resultSet.getInt("totalTutorados"));
        reporte.setTotalEstudiantesRiesgo(resultSet.getInt("totalEstudiantesRiesgo"));
        reporte.setTotalTutores(resultSet.getInt("totalTutores"));
        reporte.setPorcentajeAsistencia(resultSet.getBigDecimal("porcentajeAsistencia"));
        reporte.setTotalProblematicas(resultSet.getInt("totalProblematicas"));
        reporte.setObservaciones(resultSet.getString("observaciones"));
        reporte.setNombrePeriodo(resultSet.getString("nombrePeriodo"));

        return reporte;
    }
}
