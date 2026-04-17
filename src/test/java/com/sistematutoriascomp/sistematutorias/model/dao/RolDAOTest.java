/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RolDAOTest extends BaseDaoTest {
    private RolDAO dao = new RolDAO();

    @Test
    void obtenerRolPorId_devuelveNombre() throws SQLException {
        assertEquals("Coordinador", dao.obtenerRolPorId(1));
        assertNull(dao.obtenerRolPorId(99));
    }

    @Test
    void obtenerTodosRoles_devuelveLista() throws SQLException {
        assertEquals(2, dao.obtenerTodosRoles().size());
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        execute("DROP TABLE IF EXISTS rol");
        execute("CREATE TABLE rol (idRol INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(50))");

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO rol (nombre) VALUES (?), (?)");) {
            ps.setString(1, "Coordinador");
            ps.setString(2, "Tutor");
            ps.executeUpdate();
        }
    }
}
