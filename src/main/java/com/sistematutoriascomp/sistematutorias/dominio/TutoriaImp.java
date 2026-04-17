/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 5.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.FechaTutoriaDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.TutoriaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.FechaTutoria;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;

public class TutoriaImp {
    private static final Logger LOGGER = LogManager.getLogger(TutoriaImp.class);

    public static HashMap<String, Object> obtenerFechasPeriodoActual() {
        HashMap<String, Object> respuesta = new HashMap<>();

        try {
            int idPeriodo = Sesion.getIdPeriodoActual();
            List<FechaTutoria> fechas = FechaTutoriaDAO.obtenerFechasPorPeriodo(idPeriodo);

            if (!fechas.isEmpty()) {
                respuesta.put("error", false);
                respuesta.put("fechas", fechas);
            } else {
                respuesta.put("error", true);
                respuesta.put("mensaje", "No hay fechas de tutoría definidas por el coordinador para este periodo.");
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al consultar fechas de tutoría", ex);
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error al consultar fechas: " + ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al consultar fechas de tutoría", ex);
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error inesperado: " + ex.getMessage());
        }

        return respuesta;
    }

    public static HashMap<String, Object> registrarHorarioTutoria(Tutoria tutoria) {
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);

        try {
            if (TutoriaDAO.comprobarTutoriaRegistrada(tutoria.getIdTutor(), tutoria.getFecha())) {
                respuesta.put("mensaje", "Ya has registrado un horario para esta fecha de tutoría.");
                return respuesta;
            }

            int filas = TutoriaDAO.registrarTutoria(tutoria);

            if (filas > 0) {
                respuesta.put("error", false);
                respuesta.put("mensaje", "Horario registrado correctamente.");
            } else {
                respuesta.put("mensaje", "No se pudo registrar el horario.");
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al registrar horario de tutoría", ex);
            respuesta.put("mensaje", "Error de base de datos: " + ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al registrar horario de tutoría", ex);
            respuesta.put("mensaje", "Error inesperado: " + ex.getMessage());
        }

        return respuesta;
    }

    public static HashMap<String, Object> subirEvidencia(int idTutoria, byte[] archivo) {
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);

        try {
            if (TutoriaDAO.subirEvidencia(idTutoria, archivo)) {
                respuesta.put("error", false);
                respuesta.put("mensaje", "Evidencia subida correctamente.");
            } else {
                respuesta.put("mensaje", "No se pudo guardar la evidencia.");
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al subir evidencia de tutoría", ex);
            respuesta.put("mensaje", "Error BD: " + ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al subir evidencia de tutoría", ex);
            respuesta.put("mensaje", "Error inesperado: " + ex.getMessage());
        }

        return respuesta;
    }

    public static boolean comprobarExistenciaEvidencia(int idTutoria) {
        boolean existe = false;
        try {
            existe = TutoriaDAO.comprobarExistenciaEvidencia(idTutoria);
        } catch (SQLException ex) {
            LOGGER.error("Error al comprobar existencia de evidencia para la tutoría " + idTutoria, ex);
            existe = false;
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al comprobar existencia de evidencia", ex);
            existe = false;
        }
        return existe;
    }
}
