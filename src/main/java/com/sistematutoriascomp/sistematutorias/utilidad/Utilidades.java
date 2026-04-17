/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 4.0
 */
package com.sistematutoriascomp.sistematutorias.utilidad;

import java.io.IOException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Utilidades {
    private static final Logger LOGGER = LogManager.getLogger(Utilidades.class);
    private final static String RUTA_VISTAS = "/com/sistematutoriascomp/sistematutorias/views";

    public static void mostrarAlertaSimple(String titulo, String contenido, Alert.AlertType tipo) {
        Runnable mostrar = () -> {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(contenido);
            alerta.showAndWait();
        };
        if (Platform.isFxApplicationThread()) {
            mostrar.run();
        } else {
            try {
                Platform.runLater(mostrar);
            } catch (IllegalStateException ex) {
                LOGGER.warn("No se pudo mostrar la alerta {}", titulo, ex);
            }
        }
    }

    public static void manejarErrorTecnico(Logger logger, String mensajeLog, Exception excepcion, String tituloAlerta,
            String mensajeUsuario) {
        if (logger != null) {
            logger.error(mensajeLog, excepcion);
        }
        mostrarAlertaSimple(tituloAlerta, mensajeUsuario, Alert.AlertType.ERROR);
    }

    public static Parent loadFXML(String fxmlPath) throws IOException, NullPointerException {
        return FXMLLoader.load(Utilidades.class.getResource(RUTA_VISTAS + fxmlPath));
    }

    public static void goToWindow(String fxmlPath, ActionEvent event, String title)
            throws IOException, NullPointerException {
        Parent vista = loadFXML(fxmlPath);
        Scene escena = new Scene(vista);
        Node source = (Node) event.getSource();
        Stage escenario = (Stage) source.getScene().getWindow();
        escenario.setScene(escena);
        escenario.setTitle(title);
        escenario.show();
    }

    public static void openModal(String fxmlPath, String title) throws IOException, NullPointerException {
        Parent vista = loadFXML(fxmlPath);
        Scene escena = new Scene(vista);
        Stage escenario = new Stage();
        escenario.setScene(escena);
        escenario.setTitle(title);
        escenario.initModality(Modality.APPLICATION_MODAL);
        escenario.showAndWait();
    }

    public static void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public static void volverMenuPrincipal(ActionEvent event) throws IOException, NullPointerException {
        goToWindow("/FXMLMenuPrincipal.fxml", event, "Menú Principal");
    }

    public static void volverMenuGestionarReportes(ActionEvent event) throws IOException, NullPointerException {
        goToWindow("/FXMLMenuGestionarReportes.fxml", event, "Menú Reporte");
    }

    public static void volverMenuGestionarTutorias(ActionEvent event) throws IOException, NullPointerException {
        goToWindow("/FXMLMenuGestionarTutorias.fxml", event, "Menú Gestión de Tutorías");
    }

    public static void clicCerrarSesion(ActionEvent event) throws IOException, NullPointerException {
        goToWindow("/FXMLInicioSesion.fxml", event, "Iniciar Sesión");
    }

    public static boolean mostrarAlertaConfirmacion(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Optional<ButtonType> resultado = alerta.showAndWait();

        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}
