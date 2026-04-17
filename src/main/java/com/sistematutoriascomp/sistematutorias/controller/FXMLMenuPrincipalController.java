/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class FXMLMenuPrincipalController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLMenuPrincipalController.class);

    @FXML
    private Label lbNombreUsuario;
    @FXML
    private Label lbRol;
    @FXML
    private Button btnGestionarTutorias;
    @FXML
    private Button btnGestionarReportes;
    @FXML
    private Button btnGestionarUsuarios;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarPermisos();
        cargarDatosUsuario();
    }

    @FXML
    private void clicGestionarTutorias(ActionEvent event) {
        irPantalla("/FXMLMenuGestionarTutorias.fxml", "Menú Gestión de Tutorías", event);
    }

    @FXML
    private void clicGestionarReportes(ActionEvent event) {
        irPantalla("/FXMLMenuGestionarReportes.fxml", "Menú Gestión de Reportes", event);
    }

    @FXML
    private void clicGestionarUsuarios(ActionEvent event) {
        irPantalla("/FXMLMenuGestionarUsuarios.fxml", "Menú Gestión de Usuarios", event);
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

    private void configurarPermisos() {
        String rol = Sesion.getRolActual();

        btnGestionarTutorias.setVisible(false);
        btnGestionarReportes.setVisible(false);
        btnGestionarUsuarios.setVisible(false);

        if ("ACADEMICO".equals(rol)) {
            btnGestionarTutorias.setVisible(true);
            btnGestionarReportes.setVisible(true);
            btnGestionarUsuarios.setVisible(false);
        } else if ("COORDINADOR".equals(rol)) {
            btnGestionarTutorias.setVisible(true);
            btnGestionarReportes.setVisible(true);
            btnGestionarUsuarios.setVisible(false);
        } else if ("ADMINISTRADOR".equals(rol)) {
            btnGestionarTutorias.setVisible(true);
            btnGestionarReportes.setVisible(true);
            btnGestionarUsuarios.setVisible(true);
        }
    }

    private void cargarDatosUsuario() {
        String rol = Sesion.getRolActual();
        String nombreCompleto = "";

        if ("ACADEMICO".equals(rol)) {
            Tutor tutor = Sesion.getTutorSesion();
            if (tutor != null) {
                nombreCompleto = tutor.getNombre() + " "
                        + tutor.getApellidoPaterno() + " "
                        + tutor.getApellidoMaterno();
            }
        } else if ("COORDINADOR".equals(rol)) {
            Tutor tutor = Sesion.getTutorSesion();
            if (tutor != null) {
                nombreCompleto = tutor.getNombre() + " "
                        + tutor.getApellidoPaterno() + " "
                        + tutor.getApellidoMaterno();
            }
        } else if ("ADMINISTRADOR".equals(rol)) {
            Tutor tutor = Sesion.getTutorSesion();
            if (tutor != null) {
                nombreCompleto = tutor.getNombre() + " "
                        + tutor.getApellidoPaterno() + " "
                        + tutor.getApellidoMaterno();
            }
        }

        lbNombreUsuario.setText(nombreCompleto);
        lbRol.setText(rol);
    }

    private void irPantalla(String ruta, String titulo, ActionEvent event) {
        try {
            Utilidades.goToWindow(ruta, event, titulo);
        } catch (IOException ex) {
            manejarError("Error al cambiar de ventana hacia " + ruta, ex, "No se pudo cambiar de ventana.");
        } catch (NullPointerException ex) {
            manejarError("Error de referencia nula al cambiar de ventana hacia " + ruta, ex,
                    "Ocurrió un error inesperado al cambiar de ventana.");
        } catch (Exception e) {
            manejarError("Error inesperado al cambiar de ventana hacia " + ruta, e,
                    "Ocurrió un error inesperado al cambiar de ventana.");
        }
    }

    private void manejarError(String mensajeLog, Exception excepcion, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, "Error", mensajeUsuario);
    }
}
