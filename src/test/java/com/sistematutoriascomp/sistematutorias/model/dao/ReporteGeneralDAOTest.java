/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteGeneral;

class ReporteGeneralDAOTest extends BaseDaoTest {
    private ReporteGeneralDAO dao = new ReporteGeneralDAO();

    @Test
    void insertarYObtenerPorId_funciona() throws SQLException {
        ReporteGeneral rg = construirReporte();
        assertTrue(dao.insertar(rg));
        assertTrue(rg.getIdReporteGeneral() > 0);

        ReporteGeneral obtenido = dao.obtenerPorId(rg.getIdReporteGeneral());
        assertNotNull(obtenido);
        assertEquals("2024-1", obtenido.getNombrePeriodo());
    }

    @Test
    void obtenerTodos_devuelveLista() throws SQLException {
        dao.insertar(construirReporte());
        assertEquals(1, dao.obtenerTodos().size());
    }

    private ReporteGeneral construirReporte() {
        ReporteGeneral rg = new ReporteGeneral();
        rg.setIdPeriodo(1);
        rg.setIdCoordinador(10);
        rg.setFechaGeneracion(LocalDateTime.of(2024, 1, 10, 10, 0));
        rg.setEstado("BORRADOR");
        rg.setTotalTutorados(100);
        rg.setTotalEstudiantesRiesgo(5);
        rg.setTotalTutores(8);
        rg.setPorcentajeAsistencia(new BigDecimal("0.80"));
        rg.setTotalProblematicas(3);
        rg.setObservaciones("Observaciones");
        return rg;
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        execute("DROP TABLE IF EXISTS reportegeneral");
        execute("DROP TABLE IF EXISTS periodo");

        execute("CREATE TABLE periodo (idPeriodo INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(100), esActual BOOLEAN)");
        execute("CREATE TABLE reportegeneral ("
                + "idReporteGeneral INT AUTO_INCREMENT PRIMARY KEY,"
                + "idPeriodo INT,"
                + "idCoordinador INT,"
                + "fechaGeneracion TIMESTAMP,"
                + "estado VARCHAR(50),"
                + "totalTutorados INT,"
                + "totalEstudiantesRiesgo INT,"
                + "totalTutores INT,"
                + "porcentajeAsistencia DECIMAL(10,2),"
                + "totalProblematicas INT,"
                + "observaciones VARCHAR(500)"
                + ")");

        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO periodo (nombre, esActual) VALUES (?, ?)");) {
            ps.setString(1, "2024-1");
            ps.setBoolean(2, true);
            ps.executeUpdate();
        }
    }
}
