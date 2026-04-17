/*
 * Autor: Delgado Santiago Darlington Diego
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 5.0
 */
package com.sistematutoriascomp.sistematutorias.controller.reporte;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.ReporteGeneralDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteGeneral;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class FXMLResponderReporteGeneralController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLResponderReporteGeneralController.class);

    @FXML
    private TextArea txtaRespuesta;

    private final ReporteGeneralDAO reporteGeneralDAO = new ReporteGeneralDAO();
    private ReporteGeneral reporte;
    private boolean respuestaGuardada = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void inicializarReporte(ReporteGeneral reporte) {
        this.reporte = reporte;
        if (reporte != null && reporte.getObservaciones() != null) {
            txtaRespuesta.setText(reporte.getObservaciones());
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if (reporte == null) {
            Utilidades.mostrarAlertaSimple("Sin reporte",
                    "No se encontró un reporte general para responder.",
                    Alert.AlertType.WARNING);
            return;
        }

        String respuesta = txtaRespuesta.getText() != null ? txtaRespuesta.getText().trim() : "";
        if (respuesta.isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campo vacío",
                    "Escribe una respuesta antes de guardar.",
                    Alert.AlertType.WARNING);
            return;
        }

        reporte.setObservaciones(respuesta);
        if (reporte.getEstado() == null || reporte.getEstado().isBlank()) {
            reporte.setEstado("Revisado");
        } else {
            reporte.setEstado("Revisado");
        }

        try {
            boolean actualizado = reporteGeneralDAO.actualizarRespuestas(reporte);
            if (actualizado) {
                respuestaGuardada = true;
                Utilidades.mostrarAlertaSimple("Éxito",
                        "Respuesta guardada correctamente.",
                        Alert.AlertType.INFORMATION);
                cerrarVentana();
            } else {
                Utilidades.mostrarAlertaSimple("Sin cambios",
                        "No se pudo guardar la respuesta del reporte.",
                        Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            manejarError("Error al guardar la respuesta del reporte general en la base de datos", e,
                    "Error de base de datos", "No se pudo guardar la respuesta del reporte. Intenta más tarde.");
        } catch (Exception e) {
            manejarError("Error inesperado al responder reporte general", e,
                    "Error inesperado", "Ocurrió un error inesperado al responder el reporte.");
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtaRespuesta.getScene().getWindow();
        stage.close();
    }

    public boolean isRespuestaGuardada() {
        return respuestaGuardada;
    }

    private void manejarError(String mensajeLog, Exception excepcion, String titulo, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, titulo, mensajeUsuario);
    }
}
