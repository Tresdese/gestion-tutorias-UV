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

import com.sistematutoriascomp.sistematutorias.model.dao.AsistenciaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.AsistenciaRow;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;

public class AsistenciaImpTest {
    @Test
    public void testObtenerSesionesTutor_SinSesiones() {
        System.out.println("Prueba: obtenerSesionesTutor sin sesiones registradas");

        int idTutor = 1;
        int idPeriodo = 3;

        try (MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class);
             MockedStatic<AsistenciaDAO> daoMock = Mockito.mockStatic(AsistenciaDAO.class)) {

            sesionMock.when(Sesion::getIdPeriodoActual).thenReturn(idPeriodo);
            daoMock.when(() -> AsistenciaDAO.obtenerSesionesPorTutor(idTutor, idPeriodo))
                   .thenReturn(new ArrayList<>());

            HashMap<String, Object> respuesta = AsistenciaImp.obtenerSesionesTutor(idTutor);

            assertTrue((boolean) respuesta.get("error"));
            assertEquals("No tienes sesiones registradas para el periodo actual.", respuesta.get("mensaje"));
            assertFalse(respuesta.containsKey("sesiones"));
        }
    }

    @Test
    public void testObtenerSesionesTutor_ConSesiones() {
        System.out.println("Prueba: obtenerSesionesTutor con sesiones registradas");

        int idTutor = 1;
        int idPeriodo = 3;

        ArrayList<Tutoria> sesionesSimuladas = new ArrayList<>();
        Tutoria t1 = new Tutoria();
        t1.setIdTutoria(10);
        t1.setIdTutor(idTutor);
        t1.setIdPeriodo(idPeriodo);
        sesionesSimuladas.add(t1);

        try (MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class);
             MockedStatic<AsistenciaDAO> daoMock = Mockito.mockStatic(AsistenciaDAO.class)) {

            sesionMock.when(Sesion::getIdPeriodoActual).thenReturn(idPeriodo);
            daoMock.when(() -> AsistenciaDAO.obtenerSesionesPorTutor(idTutor, idPeriodo))
                   .thenReturn(sesionesSimuladas);

            HashMap<String, Object> respuesta = AsistenciaImp.obtenerSesionesTutor(idTutor);

            assertFalse((boolean) respuesta.get("error"));
            assertTrue(respuesta.containsKey("sesiones"));

            @SuppressWarnings("unchecked")
            ArrayList<Tutoria> resultado = (ArrayList<Tutoria>) respuesta.get("sesiones");
            assertEquals(1, resultado.size());
            assertEquals(10, resultado.get(0).getIdTutoria());
        }
    }

    @Test
    public void testObtenerSesionesTutor_ErrorBD() {
        System.out.println("Prueba: obtenerSesionesTutor con SQLException");

        int idTutor = 1;
        int idPeriodo = 3;

        try (MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class);
             MockedStatic<AsistenciaDAO> daoMock = Mockito.mockStatic(AsistenciaDAO.class)) {

            sesionMock.when(Sesion::getIdPeriodoActual).thenReturn(idPeriodo);
            daoMock.when(() -> AsistenciaDAO.obtenerSesionesPorTutor(idTutor, idPeriodo))
                   .thenThrow(new SQLException("Fallo en BD"));

            HashMap<String, Object> respuesta = AsistenciaImp.obtenerSesionesTutor(idTutor);

            assertTrue((boolean) respuesta.get("error"));
            String msg = (String) respuesta.get("mensaje");
            assertTrue(msg.contains("Error BD:"));
            assertTrue(msg.contains("Fallo en BD"));
        }
    }

    @Test
    public void testObtenerListaAsistencia_Exitoso() {
        System.out.println("Prueba: obtenerListaAsistencia exitoso");

        int idTutor = 1;
        int idTutoria = 10;
        int idPeriodo = 3;

        ArrayList<AsistenciaRow> listaSimulada = new ArrayList<>();
        AsistenciaRow a1 = new AsistenciaRow(
                100,
                "20230001",
                "Alumno Prueba",
                3,
                true
        );
        listaSimulada.add(a1);

        try (MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class);
             MockedStatic<AsistenciaDAO> daoMock = Mockito.mockStatic(AsistenciaDAO.class)) {

            sesionMock.when(Sesion::getIdPeriodoActual).thenReturn(idPeriodo);
            daoMock.when(() -> AsistenciaDAO.obtenerTutoradosPorTutor(idTutor, idPeriodo, idTutoria))
                   .thenReturn(listaSimulada);

            HashMap<String, Object> respuesta = AsistenciaImp.obtenerListaAsistencia(idTutor, idTutoria);

            assertFalse((boolean) respuesta.get("error"));

            @SuppressWarnings("unchecked")
            ArrayList<AsistenciaRow> resultado = (ArrayList<AsistenciaRow>) respuesta.get("tutorados");
            assertEquals(1, resultado.size());
            assertEquals(100, resultado.get(0).getIdTutorado());
            assertTrue(resultado.get(0).isAsistio());
        }
    }

    @Test
    public void testObtenerListaAsistencia_ErrorBD() {
        System.out.println("Prueba: obtenerListaAsistencia con SQLException");

        int idTutor = 1;
        int idTutoria = 10;
        int idPeriodo = 3;

        try (MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class);
             MockedStatic<AsistenciaDAO> daoMock = Mockito.mockStatic(AsistenciaDAO.class)) {

            sesionMock.when(Sesion::getIdPeriodoActual).thenReturn(idPeriodo);
            daoMock.when(() -> AsistenciaDAO.obtenerTutoradosPorTutor(idTutor, idPeriodo, idTutoria))
                   .thenThrow(new SQLException("Fallo al obtener lista"));

            HashMap<String, Object> respuesta = AsistenciaImp.obtenerListaAsistencia(idTutor, idTutoria);

            assertTrue((boolean) respuesta.get("error"));
            String msg = (String) respuesta.get("mensaje");
            assertTrue(msg.contains("Error BD:"));
            assertTrue(msg.contains("Fallo al obtener lista"));
        }
    }

    @Test
    public void testGuardarListaAsistencia_Exitoso() {
        System.out.println("Prueba: guardarListaAsistencia exitoso");

        int idTutoria = 10;

        ArrayList<AsistenciaRow> lista = new ArrayList<>();
        AsistenciaRow a1 = new AsistenciaRow(
                100,
                "20230001",
                "Alumno Uno",
                3,
                true
        );
        AsistenciaRow a2 = new AsistenciaRow(
                101,
                "20230002",
                "Alumno Dos",
                3,
                false
        );
        lista.add(a1);
        lista.add(a2);

        try (MockedStatic<AsistenciaDAO> daoMock = Mockito.mockStatic(AsistenciaDAO.class)) {

            HashMap<String, Object> respuesta = AsistenciaImp.guardarListaAsistencia(idTutoria, lista);

            assertFalse((boolean) respuesta.get("error"));
            assertEquals("Asistencia registrada correctamente.", respuesta.get("mensaje"));

            daoMock.verify(() -> AsistenciaDAO.registrarAsistencia(idTutoria, 100, true));
            daoMock.verify(() -> AsistenciaDAO.registrarAsistencia(idTutoria, 101, false));
        }
    }

    @Test
    public void testGuardarListaAsistencia_ErrorBD() {
        System.out.println("Prueba: guardarListaAsistencia con SQLException");

        int idTutoria = 10;

        ArrayList<AsistenciaRow> lista = new ArrayList<>();
        AsistenciaRow a1 = new AsistenciaRow(
                100,
                "20230001",
                "Alumno Uno",
                3,
                true
        );
        lista.add(a1);

        try (MockedStatic<AsistenciaDAO> daoMock = Mockito.mockStatic(AsistenciaDAO.class)) {

            daoMock.when(() -> AsistenciaDAO.registrarAsistencia(idTutoria, 100, true))
                   .thenThrow(new SQLException("Error al insertar asistencia"));

            HashMap<String, Object> respuesta = AsistenciaImp.guardarListaAsistencia(idTutoria, lista);

            assertTrue((boolean) respuesta.get("error"));
            String msg = (String) respuesta.get("mensaje");
            assertTrue(msg.contains("Error al guardar:"));
            assertTrue(msg.contains("Error al insertar asistencia"));
        }
    }

    @Test
    public void testYaTieneAsistenciaRegistrada_True() {
        System.out.println("Prueba: yaTieneAsistenciaRegistrada devuelve true");

        int idTutoria = 10;

        try (MockedStatic<AsistenciaDAO> daoMock = Mockito.mockStatic(AsistenciaDAO.class)) {

            daoMock.when(() -> AsistenciaDAO.existeAsistenciaParaTutoria(idTutoria))
                   .thenReturn(true);

            boolean resultado = AsistenciaImp.yaTieneAsistenciaRegistrada(idTutoria);

            assertTrue(resultado);
        }
    }

    @Test
    public void testYaTieneAsistenciaRegistrada_False() {
        System.out.println("Prueba: yaTieneAsistenciaRegistrada devuelve false");

        int idTutoria = 10;

        try (MockedStatic<AsistenciaDAO> daoMock = Mockito.mockStatic(AsistenciaDAO.class)) {

            daoMock.when(() -> AsistenciaDAO.existeAsistenciaParaTutoria(idTutoria))
                   .thenReturn(false);

            boolean resultado = AsistenciaImp.yaTieneAsistenciaRegistrada(idTutoria);

            assertFalse(resultado);
        }
    }

    @Test
    public void testYaTieneAsistenciaRegistrada_ErrorBD() {
        System.out.println("Prueba: yaTieneAsistenciaRegistrada con SQLException (debe devolver false)");

        int idTutoria = 10;

        try (MockedStatic<AsistenciaDAO> daoMock = Mockito.mockStatic(AsistenciaDAO.class)) {

            daoMock.when(() -> AsistenciaDAO.existeAsistenciaParaTutoria(idTutoria))
                   .thenThrow(new SQLException("Fallo al consultar"));

            boolean resultado = AsistenciaImp.yaTieneAsistenciaRegistrada(idTutoria);

            assertFalse(resultado);
        }
    }
}
