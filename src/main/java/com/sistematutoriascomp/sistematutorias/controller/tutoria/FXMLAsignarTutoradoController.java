/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.AsignacionTutorImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutorado;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FXMLAsignarTutoradoController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLAsignarTutoradoController.class);
    
    @FXML
    private TableView<Tutorado> tblTutorados;
    @FXML
    private TableColumn<Tutorado, String> tcMatricula;
    @FXML
    private TableColumn<Tutorado, String> tcNombresTutorado;
    @FXML
    private TableColumn<Tutorado, String> tcApellidosTutorado;
    @FXML
    private TableView<Tutor> tblTutores;
    @FXML
    private TableColumn<Tutor, String> tcNombresTutor;
    @FXML
    private TableColumn<Tutor, String> tcApellidosTutor;
    @FXML
    private TableColumn<Tutor, Integer> tcCantidadTutorados;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnAsignar;

    private ObservableList<Tutorado> listaTutorados;
    private ObservableList<Tutor> listaTutores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatosTablas();
    }

    private void configurarColumnas() {
        tcMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        tcNombresTutorado.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tcApellidosTutorado.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        tcNombresTutor.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tcApellidosTutor.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        tcCantidadTutorados.setCellValueFactory(new PropertyValueFactory<>("cantidadTutorados"));
    }

    private void cargarDatosTablas() {
        try {
            HashMap<String, Object> respuesta = AsignacionTutorImp.obtenerListasParaAsignacion();

            if (!(boolean) respuesta.get("error")) {
                List<Tutorado> listaTutoradosTemporal = (List<Tutorado>) respuesta.get("tutorados");
                List<Tutor> listaTutoresTemporal = (List<Tutor>) respuesta.get("tutores");

                listaTutorados = FXCollections.observableArrayList(listaTutoradosTemporal);
                listaTutores = FXCollections.observableArrayList(listaTutoresTemporal);
                tblTutorados.setItems(listaTutorados);
                tblTutores.setItems(listaTutores);

                if (listaTutorados.isEmpty()) {
                    Utilidades.mostrarAlertaSimple("Sin pendientes", "Actualmente no hay Tutorados por asignar.", Alert.AlertType.INFORMATION);
                }
            } else {
                Utilidades.mostrarAlertaSimple("Error al cargar", (String) respuesta.get("mensaje"), Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            manejarError("Error al cargar tablas de asignación", e, "Ocurrió un error al cargar la información.");
        }
    }

    @FXML
    private void clicAsignar(ActionEvent event) {
        Tutorado tutoradoSeleccionado = tblTutorados.getSelectionModel().getSelectedItem();
        Tutor tutorSeleccionado = tblTutores.getSelectionModel().getSelectedItem();

        if (tutoradoSeleccionado == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", "Por favor seleccione un Estudiante de la lista izquierda.", Alert.AlertType.WARNING);
            return;
        }

        if (tutorSeleccionado == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", "Por favor seleccione un Tutor de la lista derecha.", Alert.AlertType.WARNING);
            return;
        }

        boolean confirmar = Utilidades.mostrarAlertaConfirmacion("Confirmar Asignación",
                "¿Desea asignar al estudiante " + tutoradoSeleccionado.getNombre()
                + " con el tutor " + tutorSeleccionado.getNombre() + "?");

        if (confirmar) {
            realizarAsignacion(tutoradoSeleccionado, tutorSeleccionado);
        }
    }

    private void realizarAsignacion(Tutorado tutorado, Tutor tutor) {
        HashMap<String, Object> respuesta = AsignacionTutorImp.asignarTutor(tutorado, tutor);

        if (!(boolean) respuesta.get("error")) {
            Utilidades.mostrarAlertaSimple("Éxito", (String) respuesta.get("mensaje"), Alert.AlertType.INFORMATION);
            cargarDatosTablas();
        } else {
            Utilidades.mostrarAlertaSimple("Error", (String) respuesta.get("mensaje"), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
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
