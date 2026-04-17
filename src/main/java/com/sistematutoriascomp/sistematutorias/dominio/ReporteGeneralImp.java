/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 5.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.FechaTutoriaDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.ProblematicaDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.ReporteGeneralDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.ReporteTutoriaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.FechaTutoria;
import com.sistematutoriascomp.sistematutorias.model.pojo.Problematica;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteGeneral;

public class ReporteGeneralImp {
    private static final Logger LOGGER = LogManager.getLogger(ReporteGeneralImp.class);

    public static HashMap<String, Object> obtenerSesionesPorPeriodo(int idPeriodo) {
        HashMap<String, Object> respuesta = new HashMap<>();

        try {
            List<FechaTutoria> lista = FechaTutoriaDAO.obtenerFechasPorPeriodo(idPeriodo);
            respuesta.put("error", false);
            respuesta.put("sesiones", lista);
        } catch (SQLException ex) {
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error al cargar sesiones: " + ex.getMessage());
            LOGGER.error("Error al obtener sesiones por periodo {}", idPeriodo, ex);
        }

        return respuesta;
    }

    public static HashMap<String, Object> calcularDatosReporte(int idPeriodo, int idFechaTutoria) {
        HashMap<String, Object> respuesta = new HashMap<>();

        try {
            ReporteGeneral reporte = ReporteTutoriaDAO.obtenerDatosReporteGeneral(idPeriodo, idFechaTutoria);
            List<Problematica> listaProblematicas = ProblematicaDAO.obtenerProblematicasPorFecha(idFechaTutoria);

            if (reporte != null) {
                reporte.setTotalProblematicas(listaProblematicas.size());
                respuesta.put("error", false);
                respuesta.put("reporte", reporte);
                respuesta.put("listaProblematicas", listaProblematicas);
            } else {
                respuesta.put("error", true);
                respuesta.put("mensaje", "No se encontraron datos para generar el reporte.");
            }
        } catch (SQLException ex) {
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error al calcular datos: " + ex.getMessage());
            LOGGER.error("Error al calcular datos de reporte general (periodo {}, fecha {})", idPeriodo, idFechaTutoria, ex);
        }
        return respuesta;
    }

    public static HashMap<String, Object> guardarReporteGeneral(ReporteGeneral reporte) {
        HashMap<String, Object> respuesta = new HashMap<>();

        try {
            ReporteGeneralDAO reporteGeneralDAO = new ReporteGeneralDAO();

            if (reporteGeneralDAO.insertar(reporte)) {
                respuesta.put("error", false);
                respuesta.put("mensaje", "Reporte General guardado exitosamente.");
            } else {
                respuesta.put("error", true);
                respuesta.put("mensaje", "No se pudo registrar el reporte en la base de datos.");
            }
        } catch (SQLException ex) {
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error BD: " + ex.getMessage());
            LOGGER.error("Error al guardar reporte general", ex);
        } catch (Exception ex) {
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error: " + ex.getMessage());
            LOGGER.error("Error inesperado al guardar reporte general", ex);
        }
        return respuesta;
    }
}
