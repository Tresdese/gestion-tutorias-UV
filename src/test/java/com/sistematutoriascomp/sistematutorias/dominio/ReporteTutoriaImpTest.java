/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 4.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.sistematutoriascomp.sistematutorias.model.dao.ReporteTutoriaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteTutoria;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;

public class ReporteTutoriaImpTest {
    @Test
    public void testObtenerSesionesPendientes_SinSesiones() {
        System.out.println("Prueba: obtenerSesionesPendientes sin sesiones");

        int idTutor = 1;
        int periodoActual = 3;
        ArrayList<Tutoria> listaVacia = new ArrayList<>();

        try (MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class);
             MockedStatic<ReporteTutoriaDAO> daoMock = Mockito.mockStatic(ReporteTutoriaDAO.class)) {

            sesionMock.when(Sesion::getIdPeriodoActual).thenReturn(periodoActual);
            daoMock.when(() -> ReporteTutoriaDAO.obtenerSesionesPendientes(idTutor, periodoActual))
                   .thenReturn(listaVacia);

            HashMap<String, Object> respuesta = ReporteTutoriaImp.obtenerSesionesPendientes(idTutor);

            assertTrue((boolean) respuesta.get("error"));
            assertEquals("No hay sesiones pendientes de reporte en este periodo.", respuesta.get("mensaje"));
            assertFalse(respuesta.containsKey("sesiones"));
        }
    }

    @Test
    public void testObtenerSesionesPendientes_ConSesiones() {
        System.out.println("Prueba: obtenerSesionesPendientes con sesiones");

        int idTutor = 1;
        int periodoActual = 3;
        ArrayList<Tutoria> lista = new ArrayList<>();
        lista.add(new Tutoria());

        try (MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class);
             MockedStatic<ReporteTutoriaDAO> daoMock = Mockito.mockStatic(ReporteTutoriaDAO.class)) {

            sesionMock.when(Sesion::getIdPeriodoActual).thenReturn(periodoActual);
            daoMock.when(() -> ReporteTutoriaDAO.obtenerSesionesPendientes(idTutor, periodoActual))
                   .thenReturn(lista);

            HashMap<String, Object> respuesta = ReporteTutoriaImp.obtenerSesionesPendientes(idTutor);

            assertFalse((boolean) respuesta.get("error"));
            assertTrue(respuesta.containsKey("sesiones"));

            @SuppressWarnings("unchecked")
            ArrayList<Tutoria> resultado = (ArrayList<Tutoria>) respuesta.get("sesiones");
            assertEquals(1, resultado.size());
        }
    }

    @Test
    public void testObtenerSesionesPendientes_ErrorBD() {
        System.out.println("Prueba: obtenerSesionesPendientes con SQLException");

        int idTutor = 1;
        int periodoActual = 3;

        try (MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class);
             MockedStatic<ReporteTutoriaDAO> daoMock = Mockito.mockStatic(ReporteTutoriaDAO.class)) {

            sesionMock.when(Sesion::getIdPeriodoActual).thenReturn(periodoActual);
            daoMock.when(() -> ReporteTutoriaDAO.obtenerSesionesPendientes(idTutor, periodoActual))
                   .thenThrow(new SQLException("Fallo en BD"));

            HashMap<String, Object> respuesta = ReporteTutoriaImp.obtenerSesionesPendientes(idTutor);

            assertTrue((boolean) respuesta.get("error"));
            String msg = (String) respuesta.get("mensaje");
            assertTrue(msg.contains("Error BD:"));
            assertTrue(msg.contains("Fallo en BD"));
        }
    }

    @Test
    public void testCargarTotales_Exito() {
        System.out.println("Prueba: cargarTotales éxito");

        int idTutoria = 10;
        HashMap<String, Integer> totalesSimulados = new HashMap<>();
        totalesSimulados.put("asistentes", 5);
        totalesSimulados.put("faltantes", 2);

        try (MockedStatic<ReporteTutoriaDAO> daoMock = Mockito.mockStatic(ReporteTutoriaDAO.class)) {

            daoMock.when(() -> ReporteTutoriaDAO.obtenerTotales(idTutoria))
                   .thenReturn(totalesSimulados);

            HashMap<String, Object> respuesta = ReporteTutoriaImp.cargarTotales(idTutoria);

            assertFalse((boolean) respuesta.get("error"));
            assertTrue(respuesta.containsKey("totales"));

            @SuppressWarnings("unchecked")
            HashMap<String, Integer> resultado = (HashMap<String, Integer>) respuesta.get("totales");
            assertEquals(Integer.valueOf(5), resultado.get("asistentes"));
            assertEquals(Integer.valueOf(2), resultado.get("faltantes"));
        }
    }

    @Test
    public void testCargarTotales_ErrorBD() {
        System.out.println("Prueba: cargarTotales con SQLException");

        int idTutoria = 10;

        try (MockedStatic<ReporteTutoriaDAO> daoMock = Mockito.mockStatic(ReporteTutoriaDAO.class)) {

            daoMock.when(() -> ReporteTutoriaDAO.obtenerTotales(idTutoria))
                   .thenThrow(new SQLException("Fallo en BD"));

            HashMap<String, Object> respuesta = ReporteTutoriaImp.cargarTotales(idTutoria);

            assertTrue((boolean) respuesta.get("error"));
            assertEquals("Error al calcular totales.", respuesta.get("mensaje"));
            assertFalse(respuesta.containsKey("totales"));
        }
    }

    @Test
    public void testGuardarReporte_Exito() {
        System.out.println("Prueba: guardarReporte éxito");

        ReporteTutoria reporte = new ReporteTutoria();

        try (MockedStatic<ReporteTutoriaDAO> daoMock = Mockito.mockStatic(ReporteTutoriaDAO.class)) {

            daoMock.when(() -> ReporteTutoriaDAO.registrarReporte(reporte))
                   .thenReturn(true);

            HashMap<String, Object> respuesta = ReporteTutoriaImp.guardarReporte(reporte);

            assertFalse((boolean) respuesta.get("error"));
            assertEquals("Reporte generado correctamente.", respuesta.get("mensaje"));
        }
    }

    @Test
    public void testGuardarReporte_SinExito() {
        System.out.println("Prueba: guardarReporte sin éxito");

        ReporteTutoria reporte = new ReporteTutoria();

        try (MockedStatic<ReporteTutoriaDAO> daoMock = Mockito.mockStatic(ReporteTutoriaDAO.class)) {

            daoMock.when(() -> ReporteTutoriaDAO.registrarReporte(reporte))
                   .thenReturn(false);

            HashMap<String, Object> respuesta = ReporteTutoriaImp.guardarReporte(reporte);

            assertTrue((boolean) respuesta.get("error"));
            assertEquals("No se pudo guardar el reporte.", respuesta.get("mensaje"));
        }
    }

    @Test
    public void testGuardarReporte_ErrorBD() {
        System.out.println("Prueba: guardarReporte con SQLException");

        ReporteTutoria reporte = new ReporteTutoria();

        try (MockedStatic<ReporteTutoriaDAO> daoMock = Mockito.mockStatic(ReporteTutoriaDAO.class)) {

            daoMock.when(() -> ReporteTutoriaDAO.registrarReporte(reporte))
                   .thenThrow(new SQLException("Fallo en BD"));

            HashMap<String, Object> respuesta = ReporteTutoriaImp.guardarReporte(reporte);

            assertTrue((boolean) respuesta.get("error"));
            String msg = (String) respuesta.get("mensaje");
            assertTrue(msg.contains("Error BD:"));
            assertTrue(msg.contains("Fallo en BD"));
        }
    }
}
