/*
 * Autor: Delgado Santiago Darlington Diego
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.CarreraDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.TutorDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.TutoradoDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Carrera;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutorado;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FXMLRegistrarTutoradoController implements Initializable {
    private final Logger LOGGER = LogManager.getLogger(FXMLRegistrarTutoradoController.class);
    
    @FXML
    private TextField txtMatricula;
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidoPaterno;
    @FXML
    private TextField txtApellidoMaterno;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtSemestre;
    @FXML
    private ComboBox<String> cbProgramaEducativo;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;

    private TutorDAO tutorDAO = new TutorDAO();
    private CarreraDAO carreraDAO = new CarreraDAO();
    private TutoradoDAO tutoradoDAO = new TutoradoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        llenarCombos();
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if (validarCampos()) {
            registrarTutorado();
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        limpiarCampos();
        irAtras(event);
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        limpiarCampos();
        irAtras(event);
    }

    private void irAtras(ActionEvent event) {
        Utilidades.cerrarVentana(event);
    }

    private void llenarCombos() {
        llenarComboBoxCarreras();
    }

    private void llenarComboBoxCarreras() {
        List<Carrera> carreras;
        try {
            carreras = carreraDAO.obtenerTodasCarreras();
            for (Carrera carrera : carreras) {
                cbProgramaEducativo.getItems().add(carrera.getNombre());
            }
        } catch (SQLException ex) {
            manejarError("Error al obtener carreras de la base de datos", ex, "Error de base de datos",
                    "No se pudieron cargar las carreras. Intenta más tarde.");
        } catch (Exception e) {
            manejarError("Error inesperado al obtener carreras de la base de datos", e, "Error inesperado",
                    "Ocurrió un error inesperado al cargar las carreras.");
        }
    }

    private void registrarTutorado() {
        try {
            Tutorado tutorado = new Tutorado();
            tutorado.setMatricula(txtMatricula.getText());
            tutorado.setNombre(txtNombres.getText());
            tutorado.setApellidoPaterno(txtApellidoPaterno.getText());
            tutorado.setApellidoMaterno(txtApellidoMaterno.getText());
            tutorado.setCorreo(txtCorreo.getText());
            tutorado.setSemestre(Integer.parseInt(txtSemestre.getText()));
            tutorado.setIdCarrera(cbProgramaEducativo.getSelectionModel().getSelectedIndex() + 1);
            tutorado.setActivo(true);

            boolean registrado = tutoradoDAO.insertarTutorado(tutorado);
            if (registrado) {
                LOGGER.info("Tutorado registrado exitosamente: {}", tutorado.getMatricula());
                Utilidades.mostrarAlertaSimple("Registro exitoso",
                        "El tutorado ha sido registrado correctamente",
                        Alert.AlertType.INFORMATION);
                limpiarCampos();
            } else {
                LOGGER.error("Error al registrar el tutorado: {}", tutorado.getMatricula());
                Utilidades.mostrarAlertaSimple("Error de registro",
                        "No se pudo registrar al tutorado",
                        Alert.AlertType.ERROR);
            }
        } catch (SQLException ex) {
            manejarError("Error al registrar tutorado en la base de datos", ex, "Error de base de datos",
                    "No se pudo registrar al tutorado. Intenta nuevamente.");
        } catch (Exception e) {
            manejarError("Error inesperado al registrar tutorado", e, "Error inesperado",
                    "Ocurrió un error inesperado al registrar al tutorado.");
        }

    }

    private boolean validarCampos() {
        boolean respuesta = true;
        if (txtMatricula.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "La matrícula es obligatoria", Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (txtNombres.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El nombre es obligatorio", Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (txtApellidoPaterno.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El apellido paterno es obligatorio", Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (txtApellidoMaterno.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El apellido materno es obligatorio", Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (txtCorreo.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El correo es obligatorio", Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (txtSemestre.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El semestre es obligatorio", Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (cbProgramaEducativo.getSelectionModel().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El programa educativo es obligatorio", Alert.AlertType.WARNING);
            respuesta = false;
        }
        return respuesta;
    }

    private void limpiarCampos() {
        txtMatricula.clear();
        txtNombres.clear();
        txtApellidoPaterno.clear();
        txtApellidoMaterno.clear();
        txtCorreo.clear();
        txtSemestre.clear();
        cbProgramaEducativo.getSelectionModel().clearSelection();
    }

    private void manejarError(String mensajeLog, Exception excepcion, String titulo, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, titulo, mensajeUsuario);
    }
}
