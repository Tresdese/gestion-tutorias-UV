/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.sistematutoriascomp.sistematutorias.model.dao.AutenticacionDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;

public class AutenticacionImpTest {
    @Test
    public void testIniciarSesionExitoso() {
        System.out.println("Prueba: Iniciar Sesión con credenciales correctas (Sin BD)");

        
        String usuario = "12345";
        String password = "password123";
        Tutor tutorSimulado = new Tutor();
        tutorSimulado.setNombre("Profesor de Prueba");

        try (MockedStatic<AutenticacionDAO> daoMock = Mockito.mockStatic(AutenticacionDAO.class);
             MockedStatic<Sesion> sesionMock = Mockito.mockStatic(Sesion.class)) {

            
            
            daoMock.when(() -> AutenticacionDAO.verificarSesionTutor(usuario, password))
                   .thenReturn(tutorSimulado);

            
            
            boolean resultado = AutenticacionImp.iniciarSesionTutor(usuario, password);

            
            
            assertTrue(resultado, "El inicio de sesión debería ser exitoso");

            
            sesionMock.verify(() -> Sesion.setTutorSesion(tutorSimulado));
        }
    }

    @Test
    public void testIniciarSesionFallido() {
        System.out.println("Prueba: Iniciar Sesión con credenciales incorrectas");

        try (MockedStatic<AutenticacionDAO> daoMock = Mockito.mockStatic(AutenticacionDAO.class)) {
            
            
            daoMock.when(() -> AutenticacionDAO.verificarSesionTutor("usuarioMal", "passMal"))
                   .thenReturn(null);

            
            boolean resultado = AutenticacionImp.iniciarSesionTutor("usuarioMal", "passMal");

            
            assertFalse(resultado, "El inicio de sesión debería fallar");
        }
    }
}
