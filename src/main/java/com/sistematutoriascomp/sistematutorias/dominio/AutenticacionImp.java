/*
 * Autor: Henrnadez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.dominio;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.AutenticacionDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.PeriodoDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.scene.control.Alert;

public class AutenticacionImp {
    private static final Logger LOGGER = LogManager.getLogger(AutenticacionImp.class);

    public static boolean iniciarSesionTutor(String numeroPersonal, String password) {
        boolean respuesta = false;
        try {
            Tutor tutor = AutenticacionDAO.verificarSesionTutor(numeroPersonal, password);
            if (tutor != null) {
                Sesion.setTutorSesion(tutor);
                try {
                    int idPeriodo = PeriodoDAO.obtenerIdPeriodoActual();
                    if (idPeriodo > 0) {
                        Sesion.setIdPeriodoActual(idPeriodo);
                    } else {
                        LOGGER.warn("No se encontró un periodo activo en la BD.");
                        Utilidades.mostrarAlertaSimple("Sin periodo activo",
                                "No existe un periodo escolar activo. Comunícate con tu coordinador.",
                                Alert.AlertType.WARNING);
                    }
                } catch (SQLException exPeriodo) {
                    Utilidades.manejarErrorTecnico(LOGGER, "Error al obtener el periodo actual", exPeriodo, "Error",
                            "No se pudo obtener el periodo actual.");
                }
                respuesta = true;
            }
        } catch (SQLException ex) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error al iniciar sesión del tutor", ex, "Error",
                    "No se pudo completar el inicio de sesión. Intenta más tarde.");
        }
        return respuesta;
    }
}
