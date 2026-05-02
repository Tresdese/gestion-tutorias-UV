/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.ProblematicaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Problematica;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;

public class ProblematicaImp {
    private static final Logger LOGGER = LogManager.getLogger(ProblematicaImp.class);

    
    public static HashMap<String, Object> obtenerProblematicasTutor() {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            int idTutor = Sesion.getTutorSesion().getIdTutor();
            int idPeriodo = Sesion.getIdPeriodoActual();
            List<Problematica> lista = ProblematicaDAO.obtenerProblematicasPorTutor(idTutor, idPeriodo);
            if (!lista.isEmpty()) {
                respuesta.put("error", false);
                respuesta.put("problematicas", lista);
            } else {
                respuesta.put("error", true);
                respuesta.put("mensaje", "No tienes problemáticas registradas en el periodo actual.");
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener problemáticas del tutor", ex);
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error de conexión con base de datos, inténtalo más tarde.");
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al obtener problemáticas del tutor", ex);
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error inesperado: " + ex.getMessage());
        }
        return respuesta;
    }

    public static HashMap<String, Object> registrarProblematica(Problematica problematica) {
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);

        try {
            boolean exito = ProblematicaDAO.registrarProblematica(problematica);
            if (exito) {
                respuesta.put("error", false);
                respuesta.put("mensaje", "Problemática registrada con éxito");
            } else {
                respuesta.put("mensaje", "No se pudo registrar la problemática.");
            }
        } catch (SQLException ex) {
            respuesta.put("mensaje", "Error de conexión con base de datos, inténtalo más tarde");
            LOGGER.error("Error al registrar problemática", ex);
        } catch (Exception ex) {
            respuesta.put("mensaje", "Error inesperado: " + ex.getMessage());
            LOGGER.error("Error inesperado al registrar problemática", ex);
        }

        return respuesta;
    }
}
