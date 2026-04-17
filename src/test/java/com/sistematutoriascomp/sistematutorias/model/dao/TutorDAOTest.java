/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 2.0
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

import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;

class TutorDAOTest extends BaseDaoTest {
    private TutorDAO dao = new TutorDAO();

    @Test
    void insertarYBuscarTutor_funciona() throws SQLException {
        Tutor tutor = crearTutor();
        assertTrue(dao.insertarTutor(tutor));

        Tutor encontrado = dao.searchTutorByStaffNumber("T001");
        assertNotNull(encontrado);
        assertEquals("Juan", encontrado.getNombre());
    }

    @Test
    void updateTutor_actualizaDatos() throws SQLException {
        Tutor tutor = crearTutor();
        dao.insertarTutor(tutor);

        tutor.setNombre("Carlos");
        assertTrue(dao.updateTutor(tutor));
        Tutor actualizado = dao.searchTutorByStaffNumber("T001");
        assertEquals("Carlos", actualizado.getNombre());
    }

    @Test
    void deleteTutor_eliminaRegistro() throws SQLException {
        Tutor tutor = crearTutor();
        dao.insertarTutor(tutor);

        reopenConnection();
        int idGenerado = 0;
        try (PreparedStatement ps = connection.prepareStatement("SELECT idTutor FROM tutor WHERE numeroDePersonal = ?")) {
            ps.setString(1, "T001");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idGenerado = rs.getInt("idTutor");
            }
        }
        assertTrue(dao.deleteTutor(idGenerado));
        assertEquals(0, dao.getAllTutors().size());
    }

    @Test
    void obtenerIdPorNombre_devuelveId() throws SQLException {
        Tutor tutor = crearTutor();
        dao.insertarTutor(tutor);
        assertEquals(1, dao.obtenerIdPorNombre("Juan"));
    }

    private Tutor crearTutor() {
        Tutor tutor = new Tutor();
        tutor.setNumeroDePersonal("T001");
        tutor.setNombre("Juan");
        tutor.setApellidoPaterno("Perez");
        tutor.setApellidoMaterno("Lopez");
        tutor.setCorreo("juan@example.com");
        tutor.setPassword("pwd");
        tutor.setIdRol(1);
        tutor.setEsActivo(true);
        tutor.setIdCarrera(2);
        return tutor;
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
    }
}
