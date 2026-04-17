/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 5.0
 */
package com.sistematutoriascomp.sistematutorias.controller.reporte;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.ReporteTutoriaImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteTutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class FXMLResponderReporteTutoriaController implements Initializable {
    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(FXMLResponderReporteTutoriaController.class);

    @FXML
    private TextArea txtaRespuesta;
    
    private ReporteTutoria reporte;
    private boolean respuestaGuardada = false;
    private String respuestaTexto = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void inicializarReporte(ReporteTutoria reporte) {
        this.reporte = reporte;
    }

    @FXML
    private void clicEnviar(ActionEvent event) {
        String texto = txtaRespuesta.getText().trim();

        if (texto.isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campo vacío", "No se puede enviar una respuesta vacía.",
                    Alert.AlertType.WARNING);
            return;
        }

        HashMap<String, Object> resultado = ReporteTutoriaImp.responderReporte(reporte.getIdReporteTutoria(), texto);

        if (!(boolean) resultado.get("error")) {
            Utilidades.mostrarAlertaSimple("Éxito", "Respuesta enviada correctamente.", Alert.AlertType.INFORMATION);
            respuestaGuardada = true;
            respuestaTexto = texto;
            cerrarVentana();
        } else {
            LOGGER.error("Error al enviar respuesta: {}", resultado.get("mensaje"));
            Utilidades.mostrarAlertaSimple("Error", (String) resultado.get("mensaje"), Alert.AlertType.ERROR);
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

    public String getRespuestaTexto() {
        return respuestaTexto;
    }
}
