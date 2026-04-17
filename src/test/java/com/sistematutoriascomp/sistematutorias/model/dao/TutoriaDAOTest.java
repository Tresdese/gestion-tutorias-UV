/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;

class TutoriaDAOTest extends BaseDaoTest {
    @Test
    void registrarTutoria_guardaRegistro() throws SQLException {
        Tutoria tutoria = new Tutoria();
        tutoria.setIdTutor(1);
        tutoria.setIdPeriodo(2);
        tutoria.setFecha(LocalDate.of(2024, 1, 10));
        tutoria.setHoraInicio(LocalTime.of(10, 0));

        int filas = TutoriaDAO.registrarTutoria(tutoria);
        assertTrue(filas > 0);
    }

    @Test
    void comprobarTutoriaRegistrada_detectaExistencia() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO tutoria (idTutor, idPeriodo, fecha, hora_inicio) VALUES (?, ?, ?, ?)");) {
            ps.setInt(1, 1);
            ps.setInt(2, 1);
            ps.setDate(3, Date.valueOf("2024-02-02"));
            ps.setTime(4, Time.valueOf("09:00:00"));
            ps.executeUpdate();
        }
        assertTrue(TutoriaDAO.comprobarTutoriaRegistrada(1, LocalDate.of(2024, 2, 2)));
        assertFalse(TutoriaDAO.comprobarTutoriaRegistrada(1, LocalDate.of(2024, 3, 2)));
    }

    @Test
    void subirEvidencia_yComprobarExistenciaEvidencia() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO tutoria (idTutor, idPeriodo, fecha, hora_inicio) VALUES (?, ?, ?, ?)");) {
            ps.setInt(1, 1);
            ps.setInt(2, 1);
            ps.setDate(3, Date.valueOf("2024-02-02"));
            ps.setTime(4, Time.valueOf("09:00:00"));
            ps.executeUpdate();
        }

        int idTutoria = 0;
        try (PreparedStatement ps = connection.prepareStatement("SELECT idTutoria FROM tutoria LIMIT 1")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idTutoria = rs.getInt(1);
            }
        }

        byte[] evidencia = {1, 2, 3};
        assertTrue(TutoriaDAO.subirEvidencia(idTutoria, evidencia));
        assertTrue(TutoriaDAO.comprobarExistenciaEvidencia(idTutoria));
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        execute("DROP TABLE IF EXISTS tutoria");
        execute("CREATE TABLE tutoria ("
                + "idTutoria INT AUTO_INCREMENT PRIMARY KEY,"
                + "idTutor INT,"
                + "idPeriodo INT,"
                + "fecha DATE,"
                + "hora_inicio TIME,"
                + "evidencia BLOB"
                + ")");
    }
}
