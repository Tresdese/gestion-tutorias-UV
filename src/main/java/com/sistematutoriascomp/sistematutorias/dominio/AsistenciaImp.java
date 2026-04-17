/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 6.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.AsistenciaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.AsistenciaRow;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

public class AsistenciaImp {
    private static final Logger LOGGER = LogManager.getLogger(AsistenciaImp.class);

    public static HashMap<String, Object> obtenerSesionesTutor(int idTutor) {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            int idPeriodo = Sesion.getIdPeriodoActual();
            List<Tutoria> lista = AsistenciaDAO.obtenerSesionesPorTutor(idTutor, idPeriodo);
            if (lista.isEmpty()) {
                LOGGER.info("No se encontraron sesiones para el tutor con ID: {}", idTutor);
                respuesta.put("error", true);
                respuesta.put("mensaje", "No tienes sesiones registradas para el periodo actual.");
            } else {
                LOGGER.info("Sesiones obtenidas para tutor ID " + idTutor + ": " + lista.size());
                respuesta.put("error", false);
                respuesta.put("sesiones", lista);
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener sesiones para tutor: " + ex.getMessage());
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error BD: " + ex.getMessage());
        }
        return respuesta;
    }

    public static HashMap<String, Object> obtenerListaAsistencia(int idTutor, int idTutoria) {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            int idPeriodo = Sesion.getIdPeriodoActual();
            List<AsistenciaRow> lista = AsistenciaDAO.obtenerTutoradosPorTutor(idTutor, idPeriodo, idTutoria);
            respuesta.put("error", false);
            respuesta.put("tutorados", lista);
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener lista de asistencia: " + ex.getMessage());
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error BD: " + ex.getMessage());
        }
        return respuesta;
    }

    public static HashMap<String, Object> guardarListaAsistencia(int idTutoria, List<AsistenciaRow> lista) {
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", false);
        try {
            for (AsistenciaRow alumno : lista) {
                AsistenciaDAO.registrarAsistencia(idTutoria, alumno.getIdTutorado(), alumno.isAsistio());
            }
            respuesta.put("mensaje", "Asistencia registrada correctamente.");
        } catch (SQLException ex) {
            LOGGER.error("Error al guardar lista de asistencia: " + ex.getMessage());
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error al guardar: " + ex.getMessage());
        }
        return respuesta;
    }

    public static boolean yaTieneAsistenciaRegistrada(int idTutoria) {
        boolean respuesta = false;
        try {
            respuesta = AsistenciaDAO.existeAsistenciaParaTutoria(idTutoria);
        } catch (SQLException ex) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error al verificar asistencia", ex, "Error",
                    "No se pudo verificar la asistencia.");
            respuesta = false;
        } catch (Exception ex) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error inesperado al verificar asistencia", ex, "Error",
                    "Ocurrió un error inesperado.");
            respuesta = false;
        }
        return respuesta;
    }
}
