/*
 * Autor:
 * Ultima modificación hecha por:
 * Versión:
 */
package com.sistematutoriascomp.sistematutorias.controller.usuario;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class FXMLEditarUsuarioController implements Initializable {
    private final Logger LOGGER = LogManager.getLogger(FXMLEditarUsuarioController.class);

    @FXML private TextField txtNumeroPersonal;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidoPaterno;
    @FXML private TextField txtApellidoMaterno;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField pwdPassword;
    @FXML private ComboBox<String> cbRol;
    @FXML private ComboBox<String> cbCarrera;
    @FXML private Button btnVolver;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private TutorDAO tutorDAO = new TutorDAO();
    private RolDAO rolDAO = new RolDAO();
    private CarreraDAO carreraDAO = new CarreraDAO();
    private List<Rol> roles = new ArrayList<>();
    private List<Carrera> carreras = new ArrayList<>();
    private Tutor tutorOriginal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        llenarCombos();
    }

    public void initData(Tutor tutor) {
        this.tutorOriginal = tutor;
        txtNumeroPersonal.setText(tutor.getNumeroDePersonal());
        txtNumeroPersonal.setEditable(false);
        txtNombres.setText(tutor.getNombre());
        txtApellidoPaterno.setText(tutor.getApellidoPaterno());
        txtApellidoMaterno.setText(tutor.getApellidoMaterno());
        txtCorreo.setText(tutor.getCorreo());

        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getIdRol() == tutor.getIdRol()) {
                cbRol.getSelectionModel().select(i);
                break;
            }
        }
        for (int i = 0; i < carreras.size(); i++) {
            if (carreras.get(i).getIdCarrera() == tutor.getIdCarrera()) {
                cbCarrera.getSelectionModel().select(i);
                break;
            }
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if (validarCampos()) {
            actualizarUsuario(event);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        Utilidades.cerrarVentana(event);
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        Utilidades.cerrarVentana(event);
    }

    private void llenarCombos() {
        try {
            roles = rolDAO.obtenerTodosRoles();
            for (Rol rol : roles) {
                cbRol.getItems().add(rol.getNombreRol());
            }
        } catch (SQLException ex) {
            manejarError("Error al obtener roles", ex, "Error de base de datos",
                    "No se pudieron cargar los roles.");
        } catch (Exception e) {
            manejarError("Error inesperado al obtener roles", e, "Error inesperado",
                    "Ocurrió un error inesperado al cargar los roles.");
        }
        try {
            carreras = carreraDAO.obtenerTodasCarreras();
            for (Carrera carrera : carreras) {
                cbCarrera.getItems().add(carrera.getNombre());
            }
        } catch (SQLException ex) {
            manejarError("Error al obtener carreras", ex, "Error de base de datos",
                    "No se pudieron cargar las carreras.");
        } catch (Exception e) {
            manejarError("Error inesperado al obtener carreras", e, "Error inesperado",
                    "Ocurrió un error inesperado al cargar las carreras.");
        }
    }

    private void actualizarUsuario(ActionEvent event) {
        try {
            Tutor tutor = new Tutor();
            tutor.setNumeroDePersonal(tutorOriginal.getNumeroDePersonal());
            tutor.setNombre(txtNombres.getText().trim());
            tutor.setApellidoPaterno(txtApellidoPaterno.getText().trim());
            tutor.setApellidoMaterno(txtApellidoMaterno.getText().trim());
            tutor.setCorreo(txtCorreo.getText().trim());
            tutor.setIdRol(roles.get(cbRol.getSelectionModel().getSelectedIndex()).getIdRol());
            tutor.setIdCarrera(carreras.get(cbCarrera.getSelectionModel().getSelectedIndex()).getIdCarrera());
            tutor.setEsActivo(tutorOriginal.isEsActivo());

            String nuevaPassword = pwdPassword.getText();
            tutor.setPassword(nuevaPassword.isEmpty() ? tutorOriginal.getPassword() : nuevaPassword);

            boolean actualizado = tutorDAO.updateTutor(tutor);
            if (actualizado) {
                LOGGER.info("Usuario actualizado exitosamente: {}", tutor.getNumeroDePersonal());
                Utilidades.mostrarAlertaSimple("Actualización exitosa",
                        "El usuario ha sido actualizado correctamente.",
                        Alert.AlertType.INFORMATION);
                Utilidades.cerrarVentana(event);
            } else {
                LOGGER.error("No se pudo actualizar usuario: {}", tutor.getNumeroDePersonal());
                Utilidades.mostrarAlertaSimple("Error al actualizar",
                        "No se pudo actualizar al usuario.",
                        Alert.AlertType.ERROR);
            }
        } catch (SQLException ex) {
            manejarError("Error al actualizar usuario en BD", ex, "Error de base de datos",
                    "No se pudo actualizar al usuario. Intenta nuevamente.");
        } catch (Exception e) {
            manejarError("Error inesperado al actualizar usuario", e, "Error inesperado",
                    "Ocurrió un error inesperado al actualizar al usuario.");
        }
    }

    private boolean validarCampos() {
        if (txtNombres.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El nombre es obligatorio.", Alert.AlertType.WARNING);
            return false;
        }
        if (txtApellidoPaterno.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El apellido paterno es obligatorio.", Alert.AlertType.WARNING);
            return false;
        }
        if (txtApellidoMaterno.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El apellido materno es obligatorio.", Alert.AlertType.WARNING);
            return false;
        }
        if (txtCorreo.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El correo es obligatorio.", Alert.AlertType.WARNING);
            return false;
        }
        if (cbRol.getSelectionModel().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El rol es obligatorio.", Alert.AlertType.WARNING);
            return false;
        }
        if (cbCarrera.getSelectionModel().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "La carrera es obligatoria.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void manejarError(String mensajeLog, Exception excepcion, String titulo, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, titulo, mensajeUsuario);
    }
}
