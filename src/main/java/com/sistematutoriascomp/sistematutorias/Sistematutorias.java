/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 3.0
 */

package com.sistematutoriascomp.sistematutorias;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Sistematutorias extends Application {
    private static final Logger LOGGER = LogManager.getLogger(Sistematutorias.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent vistaRaiz = Utilidades.loadFXML("/FXMLInicioSesion.fxml");
            Scene escena = new Scene(vistaRaiz);
            primaryStage.setTitle("Iniciar sesión");
            primaryStage.setScene(escena);
            primaryStage.show();
        } catch (IOException e) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error al iniciar la aplicación", e, "Error",
                    "No se pudo iniciar la aplicación.");
        } catch (NullPointerException ex) {
            Utilidades.manejarErrorTecnico(LOGGER, "Recurso no encontrado al iniciar la aplicación", ex,
                    "Recurso no encontrado", "No se encontró el recurso para iniciar la aplicación.");
        } catch (Exception ex) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error inesperado al iniciar la aplicación", ex, "Error inesperado",
                    "Ocurrió un error inesperado al iniciar la aplicación.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
