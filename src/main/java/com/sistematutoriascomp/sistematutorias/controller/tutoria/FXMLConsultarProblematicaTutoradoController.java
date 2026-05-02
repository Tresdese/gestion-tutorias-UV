/*
 * Autor: Fidel Cruz Reyes
 * Ultima modificación hecha por: Fidel Cruz Reyes
 * Versión: 1.0
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.ProblematicaImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.Problematica;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLConsultarProblematicaTutoradoController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLConsultarProblematicaTutoradoController.class);

    @FXML
    private TableView<Problematica> tvProblematicas;
    @FXML
    private TableColumn<Problematica, String> colTitulo;
    @FXML
    private TableColumn<Problematica, String> colFecha;
    @FXML
    private TableColumn<Problematica, String> colEstatus;
    @FXML
    private TableColumn<Problematica, Void> colAcciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarProblematicas();
    }

    private void configurarTabla() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFecha() != null ? cellData.getValue().getFecha().toString() : ""));
        colEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));
        configurarColumnaAcciones();
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<Problematica, Void>() {
            private final Button btnDetalles = new Button("Detalles");
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox contenedor = new HBox(6, btnDetalles, btnEditar, btnEliminar);

            {
                btnDetalles.setStyle("-fx-background-color: #18529d; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-size: 11;");
                btnEditar.setStyle("-fx-background-color: #FC8B12; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-size: 11;");
                btnEliminar.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-size: 11;");

                btnDetalles.setOnAction(e -> {
                    Problematica p = getTableView().getItems().get(getIndex());
                    abrirDetallesProblematica(p);
                });
                btnEditar.setOnAction(e -> {
                    Problematica p = getTableView().getItems().get(getIndex());
                    abrirEditarProblematica(p);
                });
                btnEliminar.setOnAction(e -> {
                    Problematica p = getTableView().getItems().get(getIndex());
                    eliminarProblematica(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }

    private void cargarProblematicas() {
        HashMap<String, Object> respuesta = ProblematicaImp.obtenerProblematicasTutor();
        if (!(boolean) respuesta.get("error")) {
            List<Problematica> lista = (List<Problematica>) respuesta.get("problematicas");
            tvProblematicas.setItems(FXCollections.observableArrayList(lista));
        } else {
            Utilidades.mostrarAlertaSimple("Sin problemáticas",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.INFORMATION);
        }
    }

    private void abrirDetallesProblematica(Problematica problematica) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/sistematutoriascomp/sistematutorias/views/tutoria/FXMLDetallesProblematica.fxml")
            );
            Parent root = loader.load();
            FXMLDetallesProblematicaController controlador = loader.getController();
            controlador.inicializarValores(problematica);
            Stage escenario = new Stage();
            escenario.setScene(new Scene(root));
            escenario.setTitle("Detalles de la problemática");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            manejarError("Error al abrir detalles de problemática " + problematica.getIdProblematica(), ex,
                    "No se pudo abrir la ventana de detalles.");
        } catch (Exception ex) {
            manejarError("Error inesperado al abrir detalles de problemática", ex,
                    "Ocurrió un error inesperado al abrir los detalles.");
        }
    }

    private void abrirEditarProblematica(Problematica problematica) {
        // Punto de integración para CU08 - Editar Problemática de Tutorado
        Utilidades.mostrarAlertaSimple("En desarrollo",
                "La función de editar problemática (CU08) será implementada próximamente.",
                Alert.AlertType.INFORMATION);
    }

    private void eliminarProblematica(Problematica problematica) {
        // Punto de integración para CU09 - Eliminar Problemática
        Utilidades.mostrarAlertaSimple("En desarrollo",
                "La función de eliminar problemática (CU09) será implementada próximamente.",
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
