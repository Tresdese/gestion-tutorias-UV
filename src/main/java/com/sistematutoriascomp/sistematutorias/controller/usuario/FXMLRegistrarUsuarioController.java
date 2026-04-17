/*
 * Autor: Delgado Santiago Darlington Diego
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.controller.usuario;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.CarreraDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.RolDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.TutorDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Carrera;
import com.sistematutoriascomp.sistematutorias.model.pojo.Rol;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class FXMLRegistrarUsuarioController implements Initializable {
    private final Logger LOGGER = LogManager.getLogger(FXMLRegistrarUsuarioController.class);
    
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;
    @FXML
    private TextField txtNumeroPersonal;
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidoPaterno;
    @FXML
    private TextField txtApellidoMaterno;
    @FXML
    private TextField txtCorreoInstitucional;
    @FXML
    private PasswordField pwdPassword;
    @FXML
    private ComboBox<String> cbRol;
    @FXML

    private ComboBox<String> cbCarrera;
    TutorDAO tutorDAO = new TutorDAO();
    RolDAO rolDAO = new RolDAO();
    CarreraDAO carreraDAO = new CarreraDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        llenarCombos();
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if (validarCampos()) {
            registrarTutor();
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

    private void limpiarCampos() {
        txtNumeroPersonal.clear();
        txtNombres.clear();
        txtApellidoPaterno.clear();
        txtApellidoMaterno.clear();
        txtCorreoInstitucional.clear();
        if (pwdPassword != null) {
            pwdPassword.clear();
        }
        cbRol.getSelectionModel().clearSelection();
        cbCarrera.getSelectionModel().clearSelection();
    }

    private void llenarCombos() {
        llenarComboBoxRoles();
        llenarComboBoxCarreras();
    }

    private void llenarComboBoxRoles() {
        List<Rol> roles;
        try {
            roles = rolDAO.obtenerTodosRoles();
            for (Rol rol : roles) {
                cbRol.getItems().add(rol.getNombreRol());
            }
        } catch (SQLException ex) {
            manejarError("Error al obtener roles de la base de datos", ex,
                    "Error de base de datos", "No se pudieron cargar los roles. Intenta más tarde.");
        } catch (Exception e) {
            manejarError("Error inesperado al obtener roles de la base de datos", e,
                    "Error inesperado", "Ocurrió un error inesperado al cargar los roles.");
        }
    }

    private void llenarComboBoxCarreras() {
        List<Carrera> carreras;
        try {
            carreras = carreraDAO.obtenerTodasCarreras();
            for (Carrera carrera : carreras) {
                cbCarrera.getItems().add(carrera.getNombre());
            }
        } catch (SQLException ex) {
            manejarError("Error al obtener carreras de la base de datos", ex,
                    "Error de base de datos", "No se pudieron cargar las carreras. Intenta más tarde.");
        } catch (Exception e) {
            manejarError("Error inesperado al obtener carreras de la base de datos", e,
                    "Error inesperado", "Ocurrió un error inesperado al cargar las carreras.");
        }
    }

    private boolean validarCampos() {
        boolean respuesta = true;
        if (txtNumeroPersonal.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El número de personal es obligatorio", Alert.AlertType.WARNING);
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
        if (txtCorreoInstitucional.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El correo institucional es obligatorio", Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (pwdPassword != null && pwdPassword.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "La contraseña es obligatoria", Alert.AlertType.WARNING);
            respuesta = false;
        }
        return respuesta;
    }

    private void registrarTutor() {
        try {
            Tutor tutor = new Tutor();
            tutor.setNumeroDePersonal(txtNumeroPersonal.getText().trim());
            tutor.setNombre(txtNombres.getText().trim());
            tutor.setApellidoPaterno(txtApellidoPaterno.getText().trim());
            tutor.setApellidoMaterno(txtApellidoMaterno.getText().trim());
            tutor.setIdRol(cbRol.getSelectionModel().getSelectedIndex() + 1);
            tutor.setCorreo(txtCorreoInstitucional.getText().trim());
            tutor.setIdCarrera(cbCarrera.getSelectionModel().getSelectedIndex() + 1);
            if (pwdPassword != null) {
                tutor.setPassword(pwdPassword.getText());
            }

            boolean registrado = tutorDAO.insertarTutor(tutor);

            if (registrado) {
                LOGGER.info("Tutor registrado exitosamente: {}", tutor.getNumeroDePersonal());
                Utilidades.mostrarAlertaSimple("Registro exitoso",
                        "El tutor ha sido registrado correctamente",
                        Alert.AlertType.INFORMATION);
                limpiarCampos();
            } else {
                Utilidades.mostrarAlertaSimple("Error de registro",
                        "No se pudo registrar al tutor",
                        Alert.AlertType.ERROR);
            }
        } catch (SQLException ex) {
            manejarError("Error al registrar tutor en la base de datos", ex,
                    "Error de base de datos", "No se pudo registrar al tutor. Intenta nuevamente.");
        } catch (Exception e) {
            manejarError("Error inesperado al registrar tutor", e,
                    "Error inesperado", "Ocurrió un error inesperado al registrar al tutor.");
        }
    }

    private void manejarError(String mensajeLog, Exception excepcion, String titulo, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, titulo, mensajeUsuario);
    }
}
