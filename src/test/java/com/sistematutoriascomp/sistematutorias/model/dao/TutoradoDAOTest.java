/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sistematutoriascomp.sistematutorias.model.pojo.Tutorado;

class TutoradoDAOTest extends BaseDaoTest {
    private TutoradoDAO dao = new TutoradoDAO();

    @Test
    void insertarYBuscarTutorado_funciona() throws SQLException {
        Tutorado t = crearTutorado();
        assertTrue(dao.insertarTutorado(t));

        Tutorado encontrado = dao.searchTutoradoByMatricula("A001");
        assertNotNull(encontrado);
        assertEquals("Laura", encontrado.getNombre());
    }

    @Test
    void updateTutorado_actualizaDatos() throws SQLException {
        Tutorado t = crearTutorado();
        dao.insertarTutorado(t);
        t.setNombre("Lucia");
        assertTrue(dao.updateTutorado(t));
        assertEquals("Lucia", dao.searchTutoradoByMatricula("A001").getNombre());
    }

    @Test
    void deleteTutorado_eliminaRegistro() throws SQLException {
        Tutorado t = crearTutorado();
        dao.insertarTutorado(t);

        reopenConnection();
        int id = 0;
        try (PreparedStatement ps = connection.prepareStatement("SELECT idTutorado FROM tutorado WHERE matricula = ?")) {
            ps.setString(1, "A001");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        }

        assertTrue(dao.deleteTutorado(id));
        assertEquals(0, dao.getAllTutorados().size());
    }

    private Tutorado crearTutorado() {
        Tutorado t = new Tutorado();
        t.setMatricula("A001");
        t.setNombre("Laura");
        t.setApellidoPaterno("Gomez");
        t.setApellidoMaterno("Diaz");
        t.setCorreo("laura@example.com");
        t.setIdCarrera(1);
        t.setSemestre(2);
        t.setActivo(true);
        t.setIdTutor(3);
        return t;
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        execute("DROP TABLE IF EXISTS tutorado");
        execute("CREATE TABLE tutorado ("
                + "idTutorado INT AUTO_INCREMENT PRIMARY KEY,"
                + "matricula VARCHAR(50),"
                + "nombre VARCHAR(50),"
                + "apellidoPaterno VARCHAR(50),"
                + "apellidoMaterno VARCHAR(50),"
                + "correo VARCHAR(100),"
                + "idCarrera INT,"
                + "semestre INT,"
                + "esActivo BOOLEAN,"
                + "idTutor INT"
                + ")");
    }
}
