/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 5.0
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.FechaTutoriaImp;
import com.sistematutoriascomp.sistematutorias.model.dao.FechaTutoriaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.FechaTutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class FXMLRegistrarFechaTutoriaController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLRegistrarFechaTutoriaController.class);

    @FXML
    private TextField txtNumeroSesion;
    @FXML
    private DatePicker dpFechaTutoria;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextArea txtaDescripcion;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnRegistrar;

    private int numeroSesionAuto = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarVentana();
    }

    private void configurarVentana() {
        configurarFechasDisponibles();
        cargarSiguienteSesion();
    }

    private void configurarFechasDisponibles() {
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
        dpFechaTutoria.setDayCellFactory(dayCellFactory);
        dpFechaTutoria.setEditable(false);

        final StringConverter<LocalDate> defaultConverter = dpFechaTutoria.getConverter();
        dpFechaTutoria.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate object) {
                return defaultConverter.toString(object);
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }
                try {
                    return defaultConverter.fromString(string.trim());
                } catch (DateTimeParseException ex) {
                    LOGGER.warn("Formato de fecha inv\u00E1lido ingresado: {}", string, ex);
                    Utilidades.mostrarAlertaSimple("Fecha inv\u00E1lida", "Seleccione la fecha usando el calendario.", Alert.AlertType.WARNING);
                    dpFechaTutoria.getEditor().clear();
                    return null;
                }
            }
        });
    }

    private void cargarSiguienteSesion() {
        try {
            int idPeriodo = Sesion.getIdPeriodoActual();
            if (idPeriodo <= 0) {
                idPeriodo = FechaTutoriaDAO.obtenerIdPeriodoActual();
            }

            numeroSesionAuto = FechaTutoriaDAO.comprobarSiguienteSesion(idPeriodo);

            if (numeroSesionAuto > 3) {
                txtNumeroSesion.setText("COMPLETO");
                btnRegistrar.setDisable(true);
                Utilidades.mostrarAlertaSimple("Periodo Completo", "Ya se han registrado las 3 sesiones para este periodo.", Alert.AlertType.INFORMATION);
            } else {
                txtNumeroSesion.setText(String.valueOf(numeroSesionAuto));
                btnRegistrar.setDisable(false); 
            }

        } catch (SQLException ex) {
            txtNumeroSesion.setText("Error");
            Utilidades.manejarErrorTecnico(LOGGER, "Error al calcular la siguiente sesión", ex, "Error de conexión",
                    "No se pudo calcular el número de sesión.");
        } catch (Exception e) {
            txtNumeroSesion.setText("Error");
            Utilidades.manejarErrorTecnico(LOGGER, "Error inesperado al calcular la siguiente sesión", e, "Error inesperado",
                    "Ocurrió un error inesperado al calcular el número de sesión.");
        }
    }

    @FXML
    private void clicRegistrar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        FechaTutoria nuevaFecha = new FechaTutoria();
        nuevaFecha.setNumeroSesion(numeroSesionAuto);
        nuevaFecha.setFecha(dpFechaTutoria.getValue());
        nuevaFecha.setTitulo(txtTitulo.getText().trim());
        nuevaFecha.setDescripcion(txtaDescripcion.getText().trim());

        registrarInformacion(nuevaFecha);
    }

    private boolean validarCampos() {
        boolean respuesta = true;
        LocalDate fechaSeleccionada = dpFechaTutoria.getValue();
        if (fechaSeleccionada == null) {
            Utilidades.mostrarAlertaSimple("Campos requeridos", "Por favor seleccione la fecha de la tutoría.", Alert.AlertType.WARNING);
            respuesta = false;
        } else if (fechaSeleccionada.isBefore(LocalDate.now())) {
            Utilidades.mostrarAlertaSimple("Fecha inválida", "No puede seleccionar una fecha pasada.", Alert.AlertType.WARNING);
            respuesta = false;
        }

        if (txtTitulo.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos requeridos", "El título de la sesión es obligatorio.", Alert.AlertType.WARNING);
            respuesta = false;
        }

        if (txtaDescripcion.getText() == null) {
            txtaDescripcion.setText("");
        }

        return respuesta;
    }

    private void registrarInformacion(FechaTutoria fecha) {
        try {
            HashMap<String, Object> respuesta = FechaTutoriaImp.registrarFechaTutoria(fecha);
    
            if (!(boolean) respuesta.get("error")) {
                Utilidades.mostrarAlertaSimple("Registro exitoso", (String) respuesta.get("mensaje"), Alert.AlertType.INFORMATION);
                limpiarCampos();
            } else {
                LOGGER.error("Error al registrar fecha de tutoría: {}", respuesta.get("mensaje"));
                Utilidades.mostrarAlertaSimple("Error al registrar", (String) respuesta.get("mensaje"), Alert.AlertType.ERROR);
            }
        } catch (Exception ex) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error inesperado al intentar registrar fecha de tutoría", ex,
                    "Error inesperado", "Ocurrió un error al procesar el registro.");
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        limpiarCampos();
        irAtras(event);
    }

    private void limpiarCampos() {
        dpFechaTutoria.setValue(null);
        txtTitulo.clear();
        txtaDescripcion.clear();
        cargarSiguienteSesion();
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

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
        Sesion.cerrarSesion();
        try {
            Utilidades.clicCerrarSesion(event);
        } catch (IOException ex) {
            manejarError("Error al cerrar sesión", ex, "No se pudo cerrar la sesión.");
        } catch (Exception e) {
            manejarError("Error inesperado al cerrar sesión", e, "Ocurrió un error inesperado al cerrar la sesión.");
        }
    }

    private void manejarError(String mensajeLog, Exception excepcion, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, "Error", mensajeUsuario);
    }
}
