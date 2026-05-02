/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 5.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import java.sql.SQLException;
import java.time.LocalTime;
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
                respuesta.put("mensaje", "Se ha subido la evidencia correctamente.");
            } else {
                respuesta.put("mensaje", "No se pudo guardar la evidencia.");
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al subir evidencia de tutoría", ex);
            respuesta.put("mensaje", "Error de conexión con base de datos, inténtalo más tarde");
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al subir evidencia de tutoría", ex);
            respuesta.put("mensaje", "Error inesperado: " + ex.getMessage());
        }

        return respuesta;
    }

    public static HashMap<String, Object> obtenerTutoriasRegistradasTutor() {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            int idTutor = Sesion.getTutorSesion().getIdTutor();
            int idPeriodo = Sesion.getIdPeriodoActual();
            List<Tutoria> tutorias = TutoriaDAO.obtenerTutoriasPorTutorPeriodo(idTutor, idPeriodo);
            if (!tutorias.isEmpty()) {
                respuesta.put("error", false);
                respuesta.put("tutorias", tutorias);
            } else {
                respuesta.put("error", true);
                respuesta.put("mensaje", "No tienes horarios de tutoría registrados.");
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener tutorías registradas del tutor", ex);
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error de conexión con base de datos, inténtalo más tarde.");
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al obtener tutorías registradas del tutor", ex);
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error inesperado: " + ex.getMessage());
        }
        return respuesta;
    }

    public static HashMap<String, Object> editarHorarioTutoria(int idTutoria, LocalTime nuevaHora) {
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);
        try {
            boolean editado = TutoriaDAO.editarHoraTutoria(idTutoria, nuevaHora);
            if (editado) {
                respuesta.put("error", false);
                respuesta.put("mensaje", "Éxito al editar el horario");
            } else {
                respuesta.put("mensaje", "No se pudo editar el horario.");
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al editar horario de tutoría {}", idTutoria, ex);
            respuesta.put("mensaje", "Error de conexión con base de datos, inténtalo más tarde.");
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al editar horario de tutoría {}", idTutoria, ex);
            respuesta.put("mensaje", "Error inesperado: " + ex.getMessage());
        }
        return respuesta;
    }

    public static HashMap<String, Object> obtenerTutoriasConEvidencia() {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            int idTutor = Sesion.getTutorSesion().getIdTutor();
            int idPeriodo = Sesion.getIdPeriodoActual();
            List<Tutoria> tutorias = TutoriaDAO.obtenerTutoriasConEvidencia(idTutor, idPeriodo);
            if (!tutorias.isEmpty()) {
                respuesta.put("error", false);
                respuesta.put("tutorias", tutorias);
            } else {
                respuesta.put("error", true);
                respuesta.put("mensaje", "No hay evidencias registradas en el periodo actual.");
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener tutorías con evidencia", ex);
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error de conexión con base de datos, inténtalo más tarde.");
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al obtener tutorías con evidencia", ex);
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error inesperado: " + ex.getMessage());
        }
        return respuesta;
    }

    public static HashMap<String, Object> obtenerEvidenciaTutoria(int idTutoria) {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            byte[] evidencia = TutoriaDAO.obtenerEvidencia(idTutoria);
            if (evidencia != null && evidencia.length > 0) {
                respuesta.put("error", false);
                respuesta.put("evidencia", evidencia);
            } else {
                respuesta.put("error", true);
                respuesta.put("mensaje", "No se encontró evidencia para esta sesión.");
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener evidencia de tutoría {}", idTutoria, ex);
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error de conexión con base de datos, inténtalo más tarde.");
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al obtener evidencia de tutoría {}", idTutoria, ex);
            respuesta.put("error", true);
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
