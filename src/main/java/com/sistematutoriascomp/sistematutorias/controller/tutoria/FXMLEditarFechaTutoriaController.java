/*
 * Autor: Gerardo Abraham Barrón Gómez
 * Ultima modificación hecha por: Gerardo Abraham Barrón Gómez
 * Versión: 1.0
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.FechaTutoriaImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.FechaTutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class FXMLEditarFechaTutoriaController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLEditarFechaTutoriaController.class);

    @FXML
    private ComboBox<FechaTutoria> cbFechasTutoria;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextArea txtaDescripcion;
    @FXML
    private DatePicker dpFecha;

    private FechaTutoria fechaSeleccionada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarDatePicker();
        cargarFechasTutoria();
        cbFechasTutoria.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nueva) -> {
            if (nueva != null) {
                fechaSeleccionada = nueva;
                txtTitulo.setText(nueva.getTitulo() != null ? nueva.getTitulo() : "");
                txtaDescripcion.setText(nueva.getDescripcion() != null ? nueva.getDescripcion() : "");
                dpFecha.setValue(nueva.getFecha());
            }
        });
    }

    private void configurarDatePicker() {
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        };
        dpFecha.setDayCellFactory(dayCellFactory);
        dpFecha.setEditable(false);
    }

    private void cargarFechasTutoria() {
        HashMap<String, Object> respuesta = FechaTutoriaImp.obtenerFechasTutoria();
        if (!(boolean) respuesta.get("error")) {
            List<FechaTutoria> lista = (List<FechaTutoria>) respuesta.get("fechas");
            cbFechasTutoria.setItems(FXCollections.observableArrayList(lista));
        } else {
            Utilidades.mostrarAlertaSimple("Sin fechas registradas",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void clicGuardarCambios(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        FechaTutoria fechaActualizada = new FechaTutoria();
        fechaActualizada.setIdFechaTutoria(fechaSeleccionada.getIdFechaTutoria());
        fechaActualizada.setIdPeriodo(fechaSeleccionada.getIdPeriodo());
        fechaActualizada.setNumeroSesion(fechaSeleccionada.getNumeroSesion());
        fechaActualizada.setTitulo(txtTitulo.getText().trim());
        fechaActualizada.setDescripcion(txtaDescripcion.getText().trim());
        fechaActualizada.setFecha(dpFecha.getValue());

        HashMap<String, Object> respuesta = FechaTutoriaImp.editarFechaTutoria(fechaActualizada);

        if (!(boolean) respuesta.get("error")) {
            Utilidades.mostrarAlertaSimple("Actualización exitosa",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarFechasTutoria();
        } else {
            Utilidades.mostrarAlertaSimple("Datos inválidos",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.WARNING);
        }
    }

    private boolean validarCampos() {
        if (fechaSeleccionada == null) {
            Utilidades.mostrarAlertaSimple("Campos incompletos",
                    "Por favor, completa todos los campos obligatorios.",
                    Alert.AlertType.WARNING);
            return false;
        }
        if (txtTitulo.getText().trim().isEmpty() || dpFecha.getValue() == null) {
            Utilidades.mostrarAlertaSimple("Campos incompletos",
                    "Por favor, completa todos los campos obligatorios.",
                    Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        cbFechasTutoria.getSelectionModel().clearSelection();
        txtTitulo.clear();
        txtaDescripcion.clear();
        dpFecha.setValue(null);
        fechaSeleccionada = null;
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
