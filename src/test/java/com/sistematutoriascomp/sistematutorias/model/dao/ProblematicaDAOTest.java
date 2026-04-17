/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Date;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sistematutoriascomp.sistematutorias.model.pojo.Problematica;

class ProblematicaDAOTest extends BaseDaoTest {
    @Test
    void registrarProblematica_insertaFila() throws SQLException {
        Problematica p = new Problematica();
        p.setIdTutorado(1);
        p.setIdTutoria(2);
        p.setTitulo("Titulo");
        p.setDescripcion("Descripcion");
        p.setFecha(Date.valueOf("2024-01-01").toLocalDate());

        assertTrue(ProblematicaDAO.registrarProblematica(p));
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        execute("DROP TABLE IF EXISTS problematica");
        execute("CREATE TABLE problematica ("
                + "idProblematica INT AUTO_INCREMENT PRIMARY KEY,"
                + "idTutorado INT,"
                + "idTutoria INT,"
                + "titulo VARCHAR(200),"
                + "descripcion VARCHAR(500),"
                + "fecha DATE,"
                + "estatus VARCHAR(50)"
                + ")");
    }
}
