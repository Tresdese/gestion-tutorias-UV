/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 4.0
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.TutoriaImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.FechaTutoria;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class FXMLRegistrarHoraTutoriaController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLRegistrarHoraTutoriaController.class);

    @FXML
    private ComboBox<FechaTutoria> cbFechas;
    @FXML
    private Spinner<Integer> spHora;
    @FXML
    private Spinner<Integer> spMinuto;
    @FXML
    private Label lbErrorFecha;
    @FXML
    private Label lbErrorHora;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarSpinners();
        cargarFechas();
    }

    private void configurarSpinners() {
        SpinnerValueFactory<Integer> horasFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(7, 20, 7);
        spHora.setValueFactory(horasFactory);
        SpinnerValueFactory<Integer> minutosFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        spMinuto.setValueFactory(minutosFactory);
    }

    private void cargarFechas() {
        HashMap<String, Object> respuesta = TutoriaImp.obtenerFechasPeriodoActual();
        if (!(boolean) respuesta.get("error")) {
            List<FechaTutoria> lista = (List<FechaTutoria>) respuesta.get("fechas");
            ObservableList<FechaTutoria> fechasObs = FXCollections.observableArrayList(lista);
            cbFechas.setItems(fechasObs);
        }
    }

    @FXML
    private void clicRegistrar(ActionEvent event) {
        lbErrorFecha.setText("");
        lbErrorHora.setText("");
        FechaTutoria fechaSeleccionada = cbFechas.getValue();
        Integer hora = spHora.getValue();
        Integer minuto = spMinuto.getValue();
        boolean valido = true;
        if (fechaSeleccionada == null) {
            lbErrorFecha.setText("Selecciona una fecha.");
            lbErrorFecha.setVisible(true);
            valido = false;
        }
        if (hora == null || hora < 7 || hora > 20) {
            lbErrorHora.setText("Hora inválida (7-20).");
            lbErrorHora.setVisible(true);
            valido = false;
        }
        if (minuto == null || minuto < 0 || minuto > 59) {
            valido = false;
        }
        if (!valido) {
            return;
        }
        Tutoria nuevaTutoria = new Tutoria();
        nuevaTutoria.setIdTutor(Sesion.getTutorSesion().getIdTutor());
        nuevaTutoria.setIdPeriodo(Sesion.getIdPeriodoActual());
        nuevaTutoria.setFecha(fechaSeleccionada.getFecha());
        nuevaTutoria.setHoraInicio(LocalTime.of(hora, minuto));
        boolean seGuardo = guardarTutoria(nuevaTutoria);
        if (seGuardo) {
            irAtras(event);
        }
    }

    private boolean guardarTutoria(Tutoria tutoria) {
        HashMap<String, Object> respuesta = TutoriaImp.registrarHorarioTutoria(tutoria);
        boolean exito = !(boolean) respuesta.get("error");

        if (exito) {
            Utilidades.mostrarAlertaSimple("Éxito", (String) respuesta.get("mensaje"), Alert.AlertType.INFORMATION);
        } else {
            Utilidades.mostrarAlertaSimple("Error", (String) respuesta.get("mensaje"), Alert.AlertType.ERROR);
        }

        return exito;
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cbFechas.getSelectionModel().clearSelection();
        spHora.getValueFactory().setValue(10);
        spMinuto.getValueFactory().setValue(0);
        lbErrorFecha.setText("");
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
