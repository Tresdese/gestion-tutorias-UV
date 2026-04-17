/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 4.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteTutoria;

class ReporteTutoriaDAOTest extends BaseDaoTest {
    @Test
    void obtenerSesionesPendientes_devuelveSinReporteConAsistencia() throws SQLException {
        var sesiones = ReporteTutoriaDAO.obtenerSesionesPendientes(1, 1);
        assertEquals(1, sesiones.size());
        assertEquals(LocalDate.of(2024, 2, 2), sesiones.get(0).getFecha());
    }

    @Test
    void obtenerTotales_calculaAsistentesFaltantesYProblematicas() throws SQLException {
        HashMap<String, Integer> totales = ReporteTutoriaDAO.obtenerTotales(1);
        assertEquals(1, totales.get("tutorados").intValue());
        assertEquals(1, totales.get("asistentes").intValue());
        assertEquals(0, totales.get("faltantes").intValue());
        assertEquals(1, totales.get("problematicas").intValue());
    }

    @Test
    void registrarReporte_insertaNuevo() throws SQLException {
        ReporteTutoria reporte = new ReporteTutoria();
        reporte.setIdTutoria(1);
        reporte.setObservaciones("Observaciones");
        assertTrue(ReporteTutoriaDAO.registrarReporte(reporte));

        reopenConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM reportetutoria WHERE idTutoria = ?")) {
            ps.setInt(1, 1);
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertTrue(rs.getInt(1) >= 1);
        }
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        execute("DROP TABLE IF EXISTS reportetutoria");
        execute("DROP TABLE IF EXISTS problematica");
        execute("DROP TABLE IF EXISTS asistencia");
        execute("DROP TABLE IF EXISTS tutoria");

        execute("CREATE TABLE tutoria (idTutoria INT AUTO_INCREMENT PRIMARY KEY, idTutor INT, idPeriodo INT, fecha DATE, hora_inicio TIME)");
        execute("CREATE TABLE asistencia (idTutoria INT, idTutorado INT, asistio BOOLEAN)");
        execute("CREATE TABLE problematica (idProblematica INT AUTO_INCREMENT PRIMARY KEY, idTutorado INT, idTutoria INT, titulo VARCHAR(100))");
        execute("CREATE TABLE reportetutoria (idReporteTutoria INT AUTO_INCREMENT PRIMARY KEY, idTutoria INT, fechaGeneracion TIMESTAMP, observaciones VARCHAR(500), estatus VARCHAR(50))");

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO tutoria (idTutor, idPeriodo, fecha, hora_inicio) VALUES (?, ?, ?, ?), (?, ?, ?, ?)");) {
            ps.setInt(1, 1);
            ps.setInt(2, 1);
            ps.setDate(3, Date.valueOf("2024-02-02"));
            ps.setTime(4, Time.valueOf("09:00:00"));
            ps.setInt(5, 1);
            ps.setInt(6, 1);
            ps.setDate(7, Date.valueOf("2024-01-10"));
            ps.setTime(8, Time.valueOf("08:00:00"));
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO asistencia (idTutoria, idTutorado, asistio) VALUES (?, ?, ?), (?, ?, ?)");) {
            ps.setInt(1, 1);
            ps.setInt(2, 1);
            ps.setBoolean(3, true);
            ps.setInt(4, 2);
            ps.setInt(5, 1);
            ps.setBoolean(6, false);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO problematica (idTutorado, idTutoria, titulo) VALUES (?, ?, ?)");) {
            ps.setInt(1, 1);
            ps.setInt(2, 1);
            ps.setString(3, "Falta interes");
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO reportetutoria (idTutoria, fechaGeneracion, observaciones, estatus) VALUES (?, NOW(), ?, ?)");) {
            ps.setInt(1, 2);
            ps.setString(2, "Ya generado");
            ps.setString(3, "BORRADOR");
            ps.executeUpdate();
        }
    }
}
