/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 8.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.ReporteTutoriaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteTutoria;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;

public class ReporteTutoriaImp {
    private static final Logger LOGGER = LogManager.getLogger(ReporteTutoriaImp.class);

    public static HashMap<String, Object> obtenerSesionesPendientes(int idTutor) {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            int periodoActual = Sesion.getIdPeriodoActual();
            List<Tutoria> lista = ReporteTutoriaDAO.obtenerSesionesPendientes(idTutor, periodoActual);
            if (lista.isEmpty()) {
                respuesta.put("error", true);
                respuesta.put("mensaje", "No hay sesiones pendientes de reporte en este periodo.");
            } else {
                respuesta.put("error", false);
                respuesta.put("sesiones", lista);
            }
        } catch (SQLException ex) {
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error BD: " + ex.getMessage());
            LOGGER.error("Error al obtener sesiones pendientes para el tutor {}", idTutor, ex);
        }
        return respuesta;
    }

    public static HashMap<String, Object> cargarTotales(int idTutoria) {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            HashMap<String, Integer> totales = ReporteTutoriaDAO.obtenerTotales(idTutoria);
            respuesta.put("error", false);
            respuesta.put("totales", totales);
        } catch (SQLException ex) {
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error al calcular totales.");
            LOGGER.error("Error al cargar totales para la tutoría {}", idTutoria, ex);
        }
        return respuesta;
    }

    public static HashMap<String, Object> guardarReporte(ReporteTutoria reporte) {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            if (ReporteTutoriaDAO.registrarReporte(reporte)) {
                respuesta.put("error", false);
                respuesta.put("mensaje", "Reporte generado correctamente.");
            } else {
                respuesta.put("error", true);
                respuesta.put("mensaje", "No se pudo guardar el reporte.");
            }
        } catch (SQLException ex) {
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error BD: " + ex.getMessage());
            LOGGER.error("Error al guardar reporte de tutoría", ex);
        }

        return respuesta;
    }

    public static HashMap<String, Object> obtenerReportesPorTutor(int idTutor) {
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);

        try {
            List<ReporteTutoria> listaReportes = ReporteTutoriaDAO.obtenerReportesPorTutor(idTutor);
            respuesta.put("error", false);
            respuesta.put("reportes", listaReportes);
        } catch (SQLException e) {
            respuesta.put("mensaje", "Error en base de datos al cargar lista: " + e.getMessage());
            LOGGER.error("Error al obtener reportes por tutor {}", idTutor, e);
        } catch (Exception e) {
            respuesta.put("mensaje", "Error inesperado: " + e.getMessage());
            LOGGER.error("Error inesperado al obtener reportes por tutor {}", idTutor, e);
        }

        return respuesta;
    }

    public static HashMap<String, Object> enviarReporte(int idReporte) {
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);

        try {
            boolean exito = ReporteTutoriaDAO.enviarReporte(idReporte);

            if (exito) {
                respuesta.put("error", false);
                respuesta.put("mensaje", "Reporte enviado correctamente.");
            } else {
                respuesta.put("mensaje", "No se pudo actualizar el estatus del reporte.");
            }
        } catch (SQLException e) {
            respuesta.put("mensaje", "Error de conexión: " + e.getMessage());
            LOGGER.error("Error al enviar reporte {}", idReporte, e);
        }

        return respuesta;
    }

    public static HashMap<String, Object> obtenerReportesPorPeriodo(int idPeriodo) {
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);

        try {
            List<ReporteTutoria> listaReportes = ReporteTutoriaDAO.obtenerReportesPorPeriodo(idPeriodo);

            respuesta.put("error", false);
            respuesta.put("reportes", listaReportes);

        } catch (SQLException e) {
            respuesta.put("mensaje", "Error de conexión: " + e.getMessage());
            LOGGER.error("Error al obtener reportes por periodo {}", idPeriodo, e);
        }
        return respuesta;
    }

    public static HashMap<String, Object> responderReporte(int idReporte, String textoRespuesta) {
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);

        try {
            boolean exito = ReporteTutoriaDAO.registrarRespuesta(idReporte, textoRespuesta);

            if (exito) {
                respuesta.put("error", false);
                respuesta.put("mensaje", "Respuesta enviada correctamente.");
            } else {
                respuesta.put("mensaje", "No se pudo guardar la respuesta.");
            }

        } catch (SQLException e) {
            respuesta.put("mensaje", "Error de conexión: " + e.getMessage());
            LOGGER.error("Error al responder reporte {}", idReporte, e);
        }
        return respuesta;
    }

    public static HashMap<String, Object> obtenerReportesPorTutorYPeriodo(int idTutor, int idPeriodo) {
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);

        try {
            List<ReporteTutoria> listaReportes = ReporteTutoriaDAO.obtenerReportesPorTutorYPeriodo(idTutor, idPeriodo);
            respuesta.put("error", false);
            respuesta.put("reportes", listaReportes);
        } catch (SQLException e) {
            respuesta.put("mensaje", "Error en base de datos al cargar lista filtrada: " + e.getMessage());
            LOGGER.error("Error al obtener reportes por tutor {} y periodo {}", idTutor, idPeriodo, e);
        } catch (Exception e) {
            respuesta.put("mensaje", "Error inesperado: " + e.getMessage());
            LOGGER.error("Error inesperado al obtener reportes por tutor {} y periodo {}", idTutor, idPeriodo, e);
        }

        return respuesta;
    }
}
