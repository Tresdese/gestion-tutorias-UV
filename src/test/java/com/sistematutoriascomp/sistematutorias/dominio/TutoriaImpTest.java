/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.sistematutoriascomp.sistematutorias.model.dao.FechaTutoriaDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.TutoriaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.FechaTutoria;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;

public class TutoriaImpTest {
    @Test
    public void testObtenerFechasPeriodoActual_ConFechas() {
        System.out.println("Prueba: obtenerFechasPeriodoActual con fechas definidas");

        int idPeriodo = 3;
        ArrayList<FechaTutoria> fechasSimuladas = new ArrayList<>();
        fechasSimuladas.add(null);

        try (MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class);
             MockedStatic<FechaTutoriaDAO> daoMock = Mockito.mockStatic(FechaTutoriaDAO.class)) {

            sesionMock.when(Sesion::getIdPeriodoActual).thenReturn(idPeriodo);
            daoMock.when(() -> FechaTutoriaDAO.obtenerFechasPorPeriodo(idPeriodo))
                   .thenReturn(fechasSimuladas);

            HashMap<String, Object> respuesta = TutoriaImp.obtenerFechasPeriodoActual();

            assertFalse((boolean) respuesta.get("error"));
            assertTrue(respuesta.containsKey("fechas"));

            @SuppressWarnings("unchecked")
            ArrayList<FechaTutoria> resultado = (ArrayList<FechaTutoria>) respuesta.get("fechas");
            assertEquals(1, resultado.size());
        }
    }

    @Test
    public void testObtenerFechasPeriodoActual_SinFechas() {
        System.out.println("Prueba: obtenerFechasPeriodoActual sin fechas");

        int idPeriodo = 3;
        ArrayList<FechaTutoria> fechasSimuladas = new ArrayList<>();

        try (MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class);
             MockedStatic<FechaTutoriaDAO> daoMock = Mockito.mockStatic(FechaTutoriaDAO.class)) {

            sesionMock.when(Sesion::getIdPeriodoActual).thenReturn(idPeriodo);
            daoMock.when(() -> FechaTutoriaDAO.obtenerFechasPorPeriodo(idPeriodo))
                   .thenReturn(fechasSimuladas);

            HashMap<String, Object> respuesta = TutoriaImp.obtenerFechasPeriodoActual();

            assertTrue((boolean) respuesta.get("error"));
            assertEquals("No hay fechas de tutoría definidas por el coordinador para este periodo.", respuesta.get("mensaje"));
            assertFalse(respuesta.containsKey("fechas"));
        }
    }

    @Test
    public void testObtenerFechasPeriodoActual_ErrorBD() {
        System.out.println("Prueba: obtenerFechasPeriodoActual con SQLException");

        int idPeriodo = 3;

        try (MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class);
             MockedStatic<FechaTutoriaDAO> daoMock = Mockito.mockStatic(FechaTutoriaDAO.class)) {

            sesionMock.when(Sesion::getIdPeriodoActual).thenReturn(idPeriodo);
            daoMock.when(() -> FechaTutoriaDAO.obtenerFechasPorPeriodo(idPeriodo))
                   .thenThrow(new SQLException("Fallo en BD"));

            HashMap<String, Object> respuesta = TutoriaImp.obtenerFechasPeriodoActual();

            assertTrue((boolean) respuesta.get("error"));
            String msg = (String) respuesta.get("mensaje");
            assertTrue(msg.contains("Error al consultar fechas:"));
            assertTrue(msg.contains("Fallo en BD"));
        }
    }

    @Test
    public void testRegistrarHorarioTutoria_YaRegistrada() {
        System.out.println("Prueba: registrarHorarioTutoria cuando ya existe horario para esa fecha");

        Tutoria tutoria = new Tutoria();
        tutoria.setIdTutor(1);
        tutoria.setFecha(LocalDate.of(2025, 1, 1));

        try (MockedStatic<TutoriaDAO> daoMock = Mockito.mockStatic(TutoriaDAO.class)) {

            daoMock.when(() -> TutoriaDAO.comprobarTutoriaRegistrada(tutoria.getIdTutor(), tutoria.getFecha()))
                   .thenReturn(true);

            HashMap<String, Object> respuesta = TutoriaImp.registrarHorarioTutoria(tutoria);

            assertTrue((boolean) respuesta.get("error"));
            assertEquals("Ya has registrado un horario para esta fecha de tutoría.", respuesta.get("mensaje"));
        }
    }

    @Test
    public void testRegistrarHorarioTutoria_Exito() {
        System.out.println("Prueba: registrarHorarioTutoria éxito");

        Tutoria tutoria = new Tutoria();
        tutoria.setIdTutor(1);
        tutoria.setFecha(LocalDate.of(2025, 1, 1));

        try (MockedStatic<TutoriaDAO> daoMock = Mockito.mockStatic(TutoriaDAO.class)) {

            daoMock.when(() -> TutoriaDAO.comprobarTutoriaRegistrada(tutoria.getIdTutor(), tutoria.getFecha()))
                   .thenReturn(false);
            daoMock.when(() -> TutoriaDAO.registrarTutoria(tutoria))
                   .thenReturn(1);

            HashMap<String, Object> respuesta = TutoriaImp.registrarHorarioTutoria(tutoria);

            assertFalse((boolean) respuesta.get("error"));
            assertEquals("Horario registrado correctamente.", respuesta.get("mensaje"));
        }
    }

    @Test
    public void testRegistrarHorarioTutoria_SinFilas() {
        System.out.println("Prueba: registrarHorarioTutoria sin filas afectadas");

        Tutoria tutoria = new Tutoria();
        tutoria.setIdTutor(1);
        tutoria.setFecha(LocalDate.of(2025, 1, 1));

        try (MockedStatic<TutoriaDAO> daoMock = Mockito.mockStatic(TutoriaDAO.class)) {

            daoMock.when(() -> TutoriaDAO.comprobarTutoriaRegistrada(tutoria.getIdTutor(), tutoria.getFecha()))
                   .thenReturn(false);
            daoMock.when(() -> TutoriaDAO.registrarTutoria(tutoria))
                   .thenReturn(0);

            HashMap<String, Object> respuesta = TutoriaImp.registrarHorarioTutoria(tutoria);

            assertTrue((boolean) respuesta.get("error"));
            assertEquals("No se pudo registrar el horario.", respuesta.get("mensaje"));
        }
    }

    @Test
    public void testRegistrarHorarioTutoria_ErrorBD() {
        System.out.println("Prueba: registrarHorarioTutoria con SQLException");

        Tutoria tutoria = new Tutoria();
        tutoria.setIdTutor(1);
        tutoria.setFecha(LocalDate.of(2025, 1, 1));

        try (MockedStatic<TutoriaDAO> daoMock = Mockito.mockStatic(TutoriaDAO.class)) {

            daoMock.when(() -> TutoriaDAO.comprobarTutoriaRegistrada(tutoria.getIdTutor(), tutoria.getFecha()))
                   .thenThrow(new SQLException("Fallo en BD"));

            HashMap<String, Object> respuesta = TutoriaImp.registrarHorarioTutoria(tutoria);

            assertTrue((boolean) respuesta.get("error"));
            String msg = (String) respuesta.get("mensaje");
            assertTrue(msg.contains("Error de base de datos:"));
            assertTrue(msg.contains("Fallo en BD"));
        }
    }

    @Test
    public void testSubirEvidencia_Exito() {
        System.out.println("Prueba: subirEvidencia éxito");

        int idTutoria = 10;
        byte[] archivo = new byte[]{1, 2, 3};

        try (MockedStatic<TutoriaDAO> daoMock = Mockito.mockStatic(TutoriaDAO.class)) {

            daoMock.when(() -> TutoriaDAO.subirEvidencia(idTutoria, archivo))
                   .thenReturn(true);

            HashMap<String, Object> respuesta = TutoriaImp.subirEvidencia(idTutoria, archivo);

            assertFalse((boolean) respuesta.get("error"));
            assertEquals("Evidencia subida correctamente.", respuesta.get("mensaje"));
        }
    }

    @Test
    public void testSubirEvidencia_SinExito() {
        System.out.println("Prueba: subirEvidencia sin éxito");

        int idTutoria = 10;
        byte[] archivo = new byte[]{1, 2, 3};

        try (MockedStatic<TutoriaDAO> daoMock = Mockito.mockStatic(TutoriaDAO.class)) {

            daoMock.when(() -> TutoriaDAO.subirEvidencia(idTutoria, archivo))
                   .thenReturn(false);

            HashMap<String, Object> respuesta = TutoriaImp.subirEvidencia(idTutoria, archivo);

            assertTrue((boolean) respuesta.get("error"));
            assertEquals("No se pudo guardar la evidencia.", respuesta.get("mensaje"));
        }
    }

    @Test
    public void testSubirEvidencia_ErrorBD() {
        System.out.println("Prueba: subirEvidencia con SQLException");

        int idTutoria = 10;
        byte[] archivo = new byte[]{1, 2, 3};

        try (MockedStatic<TutoriaDAO> daoMock = Mockito.mockStatic(TutoriaDAO.class)) {

            daoMock.when(() -> TutoriaDAO.subirEvidencia(idTutoria, archivo))
                   .thenThrow(new SQLException("Fallo en BD"));

            HashMap<String, Object> respuesta = TutoriaImp.subirEvidencia(idTutoria, archivo);

            assertTrue((boolean) respuesta.get("error"));
            String msg = (String) respuesta.get("mensaje");
            assertTrue(msg.contains("Error BD:"));
            assertTrue(msg.contains("Fallo en BD"));
        }
    }
}
