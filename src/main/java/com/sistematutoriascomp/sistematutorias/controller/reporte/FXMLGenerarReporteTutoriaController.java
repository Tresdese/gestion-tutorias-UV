/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 4.0
 */
package com.sistematutoriascomp.sistematutorias.controller.reporte;

import java.net.URL;
import java.util.List;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.ReporteTutoriaImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteTutoria;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class FXMLGenerarReporteTutoriaController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLGenerarReporteTutoriaController.class);

    @FXML
    private ComboBox<Tutoria> cbSesiones;
    @FXML
    private Label lbTotalTutorados;
    @FXML
    private Label lbTotalAsistentes;
    @FXML
    private Label lbTotalInasistentes;
    @FXML
    private Label lbTotalProblematicas;
    @FXML
    private TextArea txtaObservaciones;
    @FXML
    private Button btnGenerar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarAreaTexto();
        cargarSesionesPendientes();
        configurarListenerComboBox();
    }

    private void configurarAreaTexto() {
        txtaObservaciones.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 500) {
                txtaObservaciones.setText(oldValue);
            }
        });
    }

    private void cargarSesionesPendientes() {
        int idTutor = Sesion.getTutorSesion().getIdTutor();
        HashMap<String, Object> respuesta = ReporteTutoriaImp.obtenerSesionesPendientes(idTutor);
        if (!(boolean) respuesta.get("error")) {
            List<Tutoria> lista = (List<Tutoria>) respuesta.get("sesiones");
            ObservableList<Tutoria> sesionesObs = FXCollections.observableArrayList(lista);
            cbSesiones.setItems(sesionesObs);
        } else {
            Utilidades.mostrarAlertaSimple("Sin sesiones",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.INFORMATION);
            btnGenerar.setDisable(true);
            cbSesiones.setDisable(true);
            txtaObservaciones.setDisable(true);
        }
    }

    private void configurarListenerComboBox() {
        cbSesiones.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarTotales(newVal.getIdTutoria());
            } else {
                limpiarEtiquetas();
            }
        });
    }

    private void cargarTotales(int idTutoria) {
        HashMap<String, Object> respuesta = ReporteTutoriaImp.cargarTotales(idTutoria);
        if (!(boolean) respuesta.get("error")) {
            HashMap<String, Integer> totales = (HashMap<String, Integer>) respuesta.get("totales");
            lbTotalTutorados.setText(String.valueOf(totales.get("tutorados")));
            lbTotalAsistentes.setText(String.valueOf(totales.get("asistentes")));
            lbTotalInasistentes.setText(String.valueOf(totales.get("faltantes")));
            lbTotalProblematicas.setText(String.valueOf(totales.get("problematicas")));
        } else {
            LOGGER.error("No se pudieron calcular los totales para la tutoría con ID {}", idTutoria);
            Utilidades.mostrarAlertaSimple("Error", "No se pudieron calcular los totales.", Alert.AlertType.ERROR);
        }
    }

    private void limpiarEtiquetas() {
        lbTotalTutorados.setText("0");
        lbTotalAsistentes.setText("0");
        lbTotalInasistentes.setText("0");
        lbTotalProblematicas.setText("0");
    }

    @FXML
    private void clicGenerar(ActionEvent event) {
        Tutoria sesionSeleccionada = cbSesiones.getValue();
        String observaciones = txtaObservaciones.getText() != null ? txtaObservaciones.getText().trim() : "";
        if (sesionSeleccionada == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", "Por favor seleccione una sesión de tutoría.",
                    Alert.AlertType.WARNING);
            return;
        }
        if (observaciones.isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "Es necesario escribir las observaciones generales.",
                    Alert.AlertType.WARNING);
            return;
        }
        if (observaciones.length() > 500) {
            Utilidades.mostrarAlertaSimple("Texto muy largo",
                    "Las observaciones no pueden exceder los 500 caracteres. Actual: " + observaciones.length(),
                    Alert.AlertType.WARNING);
            return;
        }
        ReporteTutoria nuevoReporte = new ReporteTutoria();
        nuevoReporte.setIdTutoria(sesionSeleccionada.getIdTutoria());
        nuevoReporte.setObservaciones(observaciones);
        HashMap<String, Object> respuesta = ReporteTutoriaImp.guardarReporte(nuevoReporte);
        if (!(boolean) respuesta.get("error")) {
            Utilidades.mostrarAlertaSimple("Reporte Generado", (String) respuesta.get("mensaje"),
                    Alert.AlertType.INFORMATION);
            cerrarVentana();
        } else {
            LOGGER.error("Error al guardar reporte: {}", respuesta.get("mensaje"));
            Utilidades.mostrarAlertaSimple("Error", (String) respuesta.get("mensaje"), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        cerrarVentana();
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGenerar.getScene().getWindow();
        stage.close();
    }
}
