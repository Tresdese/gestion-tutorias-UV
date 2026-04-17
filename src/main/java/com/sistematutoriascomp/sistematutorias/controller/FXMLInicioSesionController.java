/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.AutenticacionImp;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLInicioSesionController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLInicioSesionController.class);

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField pwdContrasenia;
    @FXML
    private Label lbErrorUsuario;
    @FXML
    private Label lbErrorContrasenia;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limpiarMensajesError();
    }

    @FXML
    private void clicIniciarSesion(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String password = pwdContrasenia.getText();

        if (sonDatosValidos(usuario, password)) {
            validarSesion(usuario, password);
        }
    }

    private boolean sonDatosValidos(String usuario, String password) {
        boolean correcto = true;
        limpiarMensajesError();

        if (usuario == null || usuario.trim().isEmpty()) {
            correcto = false;
            lbErrorUsuario.setText("Número de personal obligatorio");
            lbErrorUsuario.setVisible(true);
            lbErrorUsuario.setManaged(true);
        } else if (usuario.length() > 20) {
            correcto = false;
            lbErrorUsuario.setText("Máximo 20 caracteres");
            lbErrorUsuario.setVisible(true);
            lbErrorUsuario.setManaged(true);
        }

        if (password == null || password.trim().isEmpty()) {
            correcto = false;
            lbErrorContrasenia.setText("Contraseña obligatoria");
            lbErrorContrasenia.setVisible(true);
            lbErrorContrasenia.setManaged(true);
        } else if (password.length() > 255) {
            correcto = false;
            lbErrorContrasenia.setText("Máximo 255 caracteres");
            lbErrorContrasenia.setVisible(true);
            lbErrorContrasenia.setManaged(true);
        }

        return correcto;
    }

    private void limpiarMensajesError() {
        if (lbErrorUsuario != null) {
            lbErrorUsuario.setText("");
            lbErrorUsuario.setVisible(false);
            lbErrorUsuario.setManaged(false);
        }
        if (lbErrorContrasenia != null) {
            lbErrorContrasenia.setText("");
            lbErrorContrasenia.setVisible(false);
            lbErrorContrasenia.setManaged(false);
        }
    }

    private void validarSesion(String usuario, String password) {
        boolean esTutor = AutenticacionImp.iniciarSesionTutor(usuario, password);

        if (esTutor) {
            Utilidades.mostrarAlertaSimple(
                    "Bienvenido",
                    "Credenciales correctas",
                    Alert.AlertType.INFORMATION);
            irMenuPrincipal();
        } else {
            LOGGER.warn("Intento de inicio de sesión fallido para el usuario: {}", usuario);
            Utilidades.mostrarAlertaSimple(
                    "Credenciales incorrectas",
                    "No. de personal y/o contraseña incorrectos, por favor verifica la información",
                    Alert.AlertType.ERROR);
        }
    }

    private void irMenuPrincipal() {
        try {
            Stage escenario = (Stage) txtUsuario.getScene().getWindow();
            Parent root = Utilidades.loadFXML("/FXMLMenuPrincipal.fxml");
            Scene escena = new Scene(root);
            escenario.setScene(escena);
            escenario.setTitle("Menú Principal");
            escenario.show();
        } catch (IOException ex) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error al abrir el menú principal", ex, "Error",
                    "No se pudo abrir el menú principal.");
        }
    }
}
