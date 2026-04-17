/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import java.sql.SQLException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.sistematutoriascomp.sistematutorias.model.dao.ProblematicaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Problematica;

public class ProblematicaImpTest {
    @Test
    public void testRegistrarProblematica_Exito() {
        System.out.println("Prueba: registrarProblematica éxito");

        Problematica problematica = new Problematica();

        try (MockedStatic<ProblematicaDAO> daoMock = Mockito.mockStatic(ProblematicaDAO.class)) {
            daoMock.when(() -> ProblematicaDAO.registrarProblematica(problematica))
                   .thenReturn(true);

            HashMap<String, Object> respuesta = ProblematicaImp.registrarProblematica(problematica);

            assertFalse((boolean) respuesta.get("error"));
            assertEquals("Problemática registrada correctamente.", respuesta.get("mensaje"));
        }
    }

    @Test
    public void testRegistrarProblematica_SinExito() {
        System.out.println("Prueba: registrarProblematica sin éxito (DAO devuelve false)");

        Problematica problematica = new Problematica();

        try (MockedStatic<ProblematicaDAO> daoMock = Mockito.mockStatic(ProblematicaDAO.class)) {
            daoMock.when(() -> ProblematicaDAO.registrarProblematica(problematica))
                   .thenReturn(false);

            HashMap<String, Object> respuesta = ProblematicaImp.registrarProblematica(problematica);

            assertTrue((boolean) respuesta.get("error"));
            assertEquals("No se pudo registrar la problemática.", respuesta.get("mensaje"));
        }
    }

    @Test
    public void testRegistrarProblematica_ErrorBD() {
        System.out.println("Prueba: registrarProblematica con SQLException");

        Problematica problematica = new Problematica();

        try (MockedStatic<ProblematicaDAO> daoMock = Mockito.mockStatic(ProblematicaDAO.class)) {
            daoMock.when(() -> ProblematicaDAO.registrarProblematica(problematica))
                   .thenThrow(new SQLException("Fallo en BD"));

            HashMap<String, Object> respuesta = ProblematicaImp.registrarProblematica(problematica);

            assertTrue((boolean) respuesta.get("error"));
            String mensaje = (String) respuesta.get("mensaje");
            assertTrue(mensaje.contains("Error de base de datos:"));
            assertTrue(mensaje.contains("Fallo en BD"));
        }
    }
}
