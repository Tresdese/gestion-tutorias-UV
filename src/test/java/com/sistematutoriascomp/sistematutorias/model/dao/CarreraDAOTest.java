/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarreraDAOTest extends BaseDaoTest {
    private CarreraDAO dao = new CarreraDAO();

    @Test
    void obtenerTodasCarreras_devuelveTodas() throws SQLException {
        assertEquals(2, dao.obtenerTodasCarreras().size());
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        execute("DROP TABLE IF EXISTS carrera");
        execute("CREATE TABLE carrera ("
                + "idCarrera INT AUTO_INCREMENT PRIMARY KEY,"
            + "nombre VARCHAR(100)"
            + ")");

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO carrera (nombre) VALUES (?), (?)");) {
            ps.setString(1, "Sistemas");
            ps.setString(2, "Industrial");
            ps.executeUpdate();
        }
    }
}
