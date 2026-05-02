/*
 * Autor: Fidel Cruz Reyes
 * Ultima modificación hecha por: Fidel Cruz Reyes
 * Versión: 1.0
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.TutoriaImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class FXMLEditarHoraTutoriaController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLEditarHoraTutoriaController.class);

    @FXML
    private ComboBox<Tutoria> cbTutorias;
    @FXML
    private Label lbFecha;
    @FXML
    private Spinner<Integer> spHora;
    @FXML
    private Spinner<Integer> spMinuto;
    @FXML
    private Label lbErrorTutoria;
    @FXML
    private Label lbErrorHora;

    private Tutoria tutoriaSeleccionada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarSpinners();
        cargarTutorias();
        cbTutorias.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nueva) -> {
            if (nueva != null) {
                tutoriaSeleccionada = nueva;
                lbFecha.setText(nueva.getFecha().toString());
                spHora.getValueFactory().setValue(nueva.getHoraInicio().getHour());
                spMinuto.getValueFactory().setValue(nueva.getHoraInicio().getMinute());
            }
        });
    }

    private void configurarSpinners() {
        spHora.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(7, 20, 7));
        spMinuto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    private void cargarTutorias() {
        HashMap<String, Object> respuesta = TutoriaImp.obtenerTutoriasRegistradasTutor();
        if (!(boolean) respuesta.get("error")) {
            List<Tutoria> lista = (List<Tutoria>) respuesta.get("tutorias");
            cbTutorias.setItems(FXCollections.observableArrayList(lista));
        } else {
            Utilidades.mostrarAlertaSimple("Error", (String) respuesta.get("mensaje"), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        lbErrorTutoria.setText("");
        lbErrorHora.setText("");
        boolean valido = true;

        if (tutoriaSeleccionada == null) {
            lbErrorTutoria.setText("Selecciona una tutoría.");
            lbErrorTutoria.setVisible(true);
            lbErrorTutoria.setManaged(true);
            valido = false;
        }

        Integer hora = spHora.getValue();
        if (hora == null || hora < 7 || hora > 20) {
            lbErrorHora.setText("Hora inválida (7-20).");
            lbErrorHora.setVisible(true);
            lbErrorHora.setManaged(true);
            valido = false;
        }

        if (!valido) {
            return;
        }

        LocalTime nuevaHora = LocalTime.of(hora, spMinuto.getValue());
        HashMap<String, Object> respuesta = TutoriaImp.editarHorarioTutoria(tutoriaSeleccionada.getIdTutoria(), nuevaHora);

        if (!(boolean) respuesta.get("error")) {
            Utilidades.mostrarAlertaSimple("Éxito", (String) respuesta.get("mensaje"), Alert.AlertType.INFORMATION);
            irAtras(event);
        } else {
            Utilidades.mostrarAlertaSimple("Error", (String) respuesta.get("mensaje"), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        irAtras(event);
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        irAtras(event);
    }

    private void irAtras(ActionEvent event) {
        try {
            Utilidades.volverMenuGestionarTutorias(event);
        } catch (IOException ex) {
            manejarError("Error al volver al menú de tutorías", ex, "No se pudo volver al menú de tutorías.");
        } catch (Exception e) {
            manejarError("Error inesperado al volver al menú de tutorías", e,
                    "Ocurrió un error inesperado al volver al menú de tutorías.");
        }
    }

    private void manejarError(String mensajeLog, Exception excepcion, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, "Error", mensajeUsuario);
    }
}
