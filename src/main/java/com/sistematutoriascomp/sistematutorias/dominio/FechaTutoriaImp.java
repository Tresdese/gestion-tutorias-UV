/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 4.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import java.sql.SQLException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.FechaTutoriaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.FechaTutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;

public class FechaTutoriaImp {
    private static final Logger LOGGER = LogManager.getLogger(FechaTutoriaImp.class);

    public static HashMap<String, Object> registrarFechaTutoria(FechaTutoria fechaTutoria) {
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);

        try {
            int idPeriodo = Sesion.getIdPeriodoActual();
            if (idPeriodo <= 0) {
                idPeriodo = FechaTutoriaDAO.obtenerIdPeriodoActual();
            }
            if (idPeriodo <= 0) {
                respuesta.put("mensaje", "No se encontró un periodo escolar activo en el sistema. Contacte al administrador.");
                return respuesta;
            }
            fechaTutoria.setIdPeriodo(idPeriodo);

            boolean yaExiste = FechaTutoriaDAO.validarFechaRegistrada(idPeriodo, fechaTutoria.getNumeroSesion());
            if (yaExiste) {
                respuesta.put("mensaje", "La Sesión número " + fechaTutoria.getNumeroSesion() + " ya se encuentra registrada en este periodo.");
                return respuesta;
            }

            boolean resultado = FechaTutoriaDAO.registrarFechaTutoria(fechaTutoria);
            if (resultado) {
                respuesta.put("error", false);
                respuesta.put("mensaje", "La fecha de tutoría se registró correctamente.");
            } else {
                respuesta.put("mensaje", "No se pudo registrar la información.");
            }
        } catch (SQLException e) {
            respuesta.put("mensaje", "Error de conexión a la base de datos: " + e.getMessage());
            LOGGER.error("Error de base de datos al registrar fecha de tutoría", e);
        } catch (Exception e) {
            respuesta.put("mensaje", "Error inesperado al registrar: " + e.getMessage());
            LOGGER.error("Error inesperado al registrar fecha de tutoría", e);
        }

        return respuesta;
    }
}
