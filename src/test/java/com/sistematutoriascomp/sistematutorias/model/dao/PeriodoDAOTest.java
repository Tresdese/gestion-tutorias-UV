/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sistematutoriascomp.sistematutorias.model.pojo.Periodo;

class PeriodoDAOTest extends BaseDaoTest {
    @Test
    void obtenerIdPeriodoActual_devuelveElIdActual() throws SQLException {
        int id = PeriodoDAO.obtenerIdPeriodoActual();
        assertEquals(1, id);
    }

    @Test
    void obtenerPeriodoPorId_devuelvePeriodo() throws SQLException {
        Periodo periodo = PeriodoDAO.obtenerPeriodoPorId(2);
        assertNotNull(periodo);
        assertEquals("2023-2", periodo.getNombre());
    }

    @Test
    void obtenerIdPorNombre_devuelveIdCorrecto() throws SQLException {
        int id = PeriodoDAO.obtenerIdPorNombre("2024-1");
        assertEquals(1, id);
    }

    @Test
    void obtenerTodosPeriodos_devuelveListaCompleta() throws SQLException {
        assertEquals(2, PeriodoDAO.obtenerTodosPeriodos().size());
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        execute("DROP TABLE IF EXISTS periodo");
        execute("CREATE TABLE periodo ("
                + "idPeriodo INT AUTO_INCREMENT PRIMARY KEY,"
                + "nombre VARCHAR(100),"
                + "esActual BOOLEAN"
                + ")");

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO periodo (nombre, esActual) VALUES (?, ?), (?, ?)");) {
            ps.setString(1, "2024-1");
            ps.setBoolean(2, true);
            ps.setString(3, "2023-2");
            ps.setBoolean(4, false);
            ps.executeUpdate();
        }
    }
}
