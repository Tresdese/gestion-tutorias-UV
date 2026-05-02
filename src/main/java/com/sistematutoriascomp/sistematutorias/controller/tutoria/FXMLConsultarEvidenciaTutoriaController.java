/*
 * Autor: Fidel Cruz Reyes
 * Ultima modificación hecha por: Fidel Cruz Reyes
 * Versión: 1.0
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.TutoriaImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class FXMLConsultarEvidenciaTutoriaController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLConsultarEvidenciaTutoriaController.class);

    @FXML
    private TableView<Tutoria> tvEvidencias;
    @FXML
    private TableColumn<Tutoria, String> colFecha;
    @FXML
    private TableColumn<Tutoria, String> colHora;
    @FXML
    private TableColumn<Tutoria, Void> colAcciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarEvidencias();
    }

    private void configurarTabla() {
        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFecha() != null ? cellData.getValue().getFecha().toString() : ""));
        colHora.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getHoraInicio() != null ? cellData.getValue().getHoraInicio().toString() : ""));
        configurarColumnaAcciones();
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<Tutoria, Void>() {
            private final Button btnDescargar = new Button("Descargar");
            private final Button btnEditar = new Button("Editar");
            private final HBox contenedor = new HBox(8, btnDescargar, btnEditar);

            {
                btnDescargar.setStyle("-fx-background-color: #18529d; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-size: 12;");
                btnEditar.setStyle("-fx-background-color: #FC8B12; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-size: 12;");

                btnDescargar.setOnAction(e -> {
                    Tutoria tutoria = getTableView().getItems().get(getIndex());
                    descargarEvidencia(tutoria);
                });
                btnEditar.setOnAction(e -> {
                    Tutoria tutoria = getTableView().getItems().get(getIndex());
                    editarEvidencia(tutoria);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }

    private void cargarEvidencias() {
        HashMap<String, Object> respuesta = TutoriaImp.obtenerTutoriasConEvidencia();
        if (!(boolean) respuesta.get("error")) {
            List<Tutoria> lista = (List<Tutoria>) respuesta.get("tutorias");
            tvEvidencias.setItems(FXCollections.observableArrayList(lista));
        } else {
            Utilidades.mostrarAlertaSimple("Sin evidencias",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.INFORMATION);
        }
    }

    private void descargarEvidencia(Tutoria tutoria) {
        HashMap<String, Object> respuesta = TutoriaImp.obtenerEvidenciaTutoria(tutoria.getIdTutoria());
        if ((boolean) respuesta.get("error")) {
            Utilidades.mostrarAlertaSimple("Error",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.ERROR);
            return;
        }

        byte[] evidencia = (byte[]) respuesta.get("evidencia");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar evidencia");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("evidencia_" + tutoria.getFecha() + ".pdf");

        File archivoDestino = fileChooser.showSaveDialog(tvEvidencias.getScene().getWindow());

        if (archivoDestino == null) {
            // FA1.1: Usuario canceló el explorador → regresa a la ventana anterior
            return;
        }

        try {
            Files.write(archivoDestino.toPath(), evidencia);
            Utilidades.mostrarAlertaSimple("Descarga exitosa",
                    "El archivo se ha guardado correctamente.",
                    Alert.AlertType.INFORMATION);
        } catch (IOException ex) {
            manejarError("Error al guardar archivo de evidencia para tutoría " + tutoria.getIdTutoria(), ex,
                    "No se pudo guardar el archivo en la ubicación seleccionada.");
        }
    }

    private void editarEvidencia(Tutoria tutoria) {
        // Punto de integración para CU12 - Editar Evidencia de Tutoría
        Utilidades.mostrarAlertaSimple("En desarrollo",
                "La función de editar evidencia (CU12) será implementada próximamente.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void clicVolver(ActionEvent event) {
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
