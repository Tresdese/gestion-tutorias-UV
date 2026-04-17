/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 4.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;

class AutenticacionDAOTest extends BaseDaoTest {
    @Test
    void verificarSesionTutor_devuelveTutorCuandoCoincide() throws SQLException {
        Tutor tutor = AutenticacionDAO.verificarSesionTutor("123", "secret");
        assertNotNull(tutor);
    }

    @Test
    void verificarSesionTutor_regresaNullConCredencialesIncorrectas() throws SQLException {
        Tutor tutor = AutenticacionDAO.verificarSesionTutor("123", "wrong");
        assertNull(tutor);
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        execute("DROP TABLE IF EXISTS tutor");
        execute("CREATE TABLE tutor ("
                + "idTutor INT AUTO_INCREMENT PRIMARY KEY,"
                + "numeroDePersonal VARCHAR(50),"
                + "nombre VARCHAR(50),"
                + "apellidoPaterno VARCHAR(50),"
                + "apellidoMaterno VARCHAR(50),"
                + "correo VARCHAR(100),"
                + "password VARCHAR(100),"
                + "idRol INT,"
                + "esActivo BOOLEAN,"
                + "idCarrera INT"
                + ")");

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO tutor (numeroDePersonal, nombre, apellidoPaterno, apellidoMaterno, correo, password, idRol, esActivo, idCarrera)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");) {
            ps.setString(1, "123");
            ps.setString(2, "Ana");
            ps.setString(3, "Lopez");
            ps.setString(4, "Perez");
            ps.setString(5, "ana@example.com");
            ps.setString(6, "secret");
            ps.setInt(7, 1);
            ps.setBoolean(8, true);
            ps.setInt(9, 1);
            ps.executeUpdate();
        }
    }
}
