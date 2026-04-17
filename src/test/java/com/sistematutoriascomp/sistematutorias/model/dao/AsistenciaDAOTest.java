/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sistematutoriascomp.sistematutorias.model.pojo.AsistenciaRow;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;

class AsistenciaDAOTest extends BaseDaoTest {
    @Test
    void obtenerSesionesPorTutor_devuelveSesionesOrdenadas() throws SQLException {
        List<Tutoria> sesiones = AsistenciaDAO.obtenerSesionesPorTutor(1, 1);
        assertEquals(2, sesiones.size());
        assertTrue(sesiones.get(0).getFecha().isAfter(sesiones.get(1).getFecha()));
    }

    @Test
    void obtenerTutoradosPorTutor_devuelveAsignados() throws SQLException {
        List<AsistenciaRow> lista = AsistenciaDAO.obtenerTutoradosPorTutor(1, 1, 1);
        assertEquals(2, lista.size());
    }

    @Test
    void registrarAsistencia_insertaYOActualiza() throws SQLException {
        assertTrue(AsistenciaDAO.registrarAsistencia(1, 1, true));
        assertTrue(AsistenciaDAO.existeAsistenciaParaTutoria(1));

        assertTrue(AsistenciaDAO.registrarAsistencia(1, 1, false));

        reopenConnection();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT asistio FROM asistencia WHERE idTutoria = ? AND idTutorado = ?")) {
            ps.setInt(1, 1);
            ps.setInt(2, 1);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                assertEquals(false, rs.getBoolean("asistio"));
            }
        }
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        execute("DROP TABLE IF EXISTS asistencia");
        execute("DROP TABLE IF EXISTS asignaciontutor");
        execute("DROP TABLE IF EXISTS tutorado");
        execute("DROP TABLE IF EXISTS tutoria");

        execute("CREATE TABLE tutoria (idTutoria INT AUTO_INCREMENT PRIMARY KEY, idTutor INT, idPeriodo INT, fecha DATE, hora_inicio TIME)");
        execute("CREATE TABLE tutorado (idTutorado INT AUTO_INCREMENT PRIMARY KEY, matricula VARCHAR(50), nombre VARCHAR(50), apellidoPaterno VARCHAR(50), apellidoMaterno VARCHAR(50), semestre INT)");
        execute("CREATE TABLE asignaciontutor (idTutorado INT, idTutor INT, idPeriodo INT)");
        execute("CREATE TABLE asistencia (idTutoria INT, idTutorado INT, asistio BOOLEAN, PRIMARY KEY (idTutoria, idTutorado))");

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO tutoria (idTutor, idPeriodo, fecha, hora_inicio) VALUES (?, ?, ?, ?), (?, ?, ?, ?)");) {
            ps.setInt(1, 1);
            ps.setInt(2, 1);
            ps.setDate(3, Date.valueOf("2024-02-02"));
            ps.setTime(4, Time.valueOf("09:00:00"));
            ps.setInt(5, 1);
            ps.setInt(6, 1);
            ps.setDate(7, Date.valueOf("2024-01-15"));
            ps.setTime(8, Time.valueOf("08:00:00"));
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO tutorado (matricula, nombre, apellidoPaterno, apellidoMaterno, semestre) VALUES (?, ?, ?, ?, ?), (?, ?, ?, ?, ?)");) {
            ps.setString(1, "A1");
            ps.setString(2, "Luis");
            ps.setString(3, "Garcia");
            ps.setString(4, "Diaz");
            ps.setInt(5, 3);
            ps.setString(6, "A2");
            ps.setString(7, "Maria");
            ps.setString(8, "Lopez");
            ps.setString(9, "Hernandez");
            ps.setInt(10, 2);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO asignaciontutor (idTutorado, idTutor, idPeriodo) VALUES (?, ?, ?), (?, ?, ?)");) {
            ps.setInt(1, 1);
            ps.setInt(2, 1);
            ps.setInt(3, 1);
            ps.setInt(4, 2);
            ps.setInt(5, 1);
            ps.setInt(6, 1);
            ps.executeUpdate();
        }
    }
}
