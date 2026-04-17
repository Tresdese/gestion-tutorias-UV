/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FechaTutoriaDAOTest extends BaseDaoTest {
    @Test
    void obtenerFechasPorPeriodo_regresaOrdenAscendente() throws SQLException {
        assertEquals(2, FechaTutoriaDAO.obtenerFechasPorPeriodo(1).size());
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        execute("DROP TABLE IF EXISTS fechatutoria");
        execute("CREATE TABLE fechatutoria ("
                + "idFechaTutoria INT AUTO_INCREMENT PRIMARY KEY,"
                + "idPeriodo INT,"
                + "numeroSesion INT,"
                + "fecha DATE"
                + ")");

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO fechatutoria (idPeriodo, numeroSesion, fecha) VALUES (?, ?, ?), (?, ?, ?)");) {
            ps.setInt(1, 1);
            ps.setInt(2, 1);
            ps.setDate(3, Date.valueOf("2024-01-10"));
            ps.setInt(4, 1);
            ps.setInt(5, 2);
            ps.setDate(6, Date.valueOf("2024-02-10"));
            ps.executeUpdate();
        }
    }
}
