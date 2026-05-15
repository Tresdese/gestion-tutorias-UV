package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.TutoradoDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutorado;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLConsultarListaTutoradosController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLConsultarListaTutoradosController.class);
    private static final String RUTA_VISTAS = "/com/sistematutoriascomp/sistematutorias/views";

    @FXML
    private TableView<Tutorado> tblTutorados;
    @FXML
    private TableColumn<Tutorado, String> tcMatricula;
    @FXML
    private TableColumn<Tutorado, String> tcNombre;
    @FXML
    private TableColumn<Tutorado, String> tcApellidoPaterno;
    @FXML
    private TableColumn<Tutorado, String> tcApellidoMaterno;
    @FXML
    private TableColumn<Tutorado, Boolean> tcEstatus;

    private final TutoradoDAO tutoradoDAO = new TutoradoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarTutorados();
    }

    private void configurarColumnas() {
        tcMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        tcNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tcApellidoPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        tcApellidoMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        tcEstatus.setCellValueFactory(new PropertyValueFactory<>("activo"));
        tcEstatus.setCellFactory(col -> new TableCell<Tutorado, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                    setStyle("");
                } else if (activo) {
                    setText("Activo");
                    setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-alignment: CENTER;");
                } else {
                    setText("Inactivo");
                    setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold; -fx-alignment: CENTER;");
                }
            }
        });
    }

    private void cargarTutorados() {
        try {
            List<Tutorado> tutorados = tutoradoDAO.getAllTutorados();
            if (tutorados.isEmpty()) {
                Platform.runLater(() -> {
                    Utilidades.mostrarAlertaSimple("Sin tutorados",
                            "Actualmente no hay Tutorados registrados en el sistema.",
                            Alert.AlertType.INFORMATION);
                    try {
                        Stage stage = (Stage) tblTutorados.getScene().getWindow();
                        Parent vista = Utilidades.loadFXML("/FXMLMenuGestionarTutorias.fxml");
                        stage.setScene(new Scene(vista));
                        stage.setTitle("Menú Gestión de Tutorías");
                    } catch (IOException | NullPointerException ex) {
                        LOGGER.error("Error al volver al menú tras FA1.1", ex);
                    }
                });
                return;
            }
            tblTutorados.setItems(FXCollections.observableArrayList(tutorados));
        } catch (SQLException ex) {
            manejarError("Error de conexión al cargar tutorados", ex, "Error de conexión",
                    "Error de conexión con base de datos, inténtalo más tarde");
        } catch (Exception e) {
            manejarError("Error inesperado al cargar tutorados", e, "Error inesperado",
                    "Ocurrió un error inesperado al cargar los tutorados.");
        }
    }

    @FXML
    private void clicVerDetalles(ActionEvent event) {
        Tutorado seleccionado = tblTutorados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple("Sin selección",
                    "Seleccione un tutorado de la lista para ver sus detalles.",
                    Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(RUTA_VISTAS + "/tutoria/FXMLDetallesTutorado.fxml"));
            Parent root = loader.load();
            FXMLDetallesTutoradoController controller = loader.getController();
            controller.inicializarValores(seleccionado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Detalles del Tutorado");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            manejarError("Error al abrir ventana de detalles", ex, "Error",
                    "No se pudo abrir la ventana de detalles.");
        } catch (Exception e) {
            manejarError("Error inesperado al abrir ventana de detalles", e, "Error inesperado",
                    "Ocurrió un error inesperado al abrir la ventana de detalles.");
        }
    }

    @FXML
    private void clicCerrar(ActionEvent event) {
        try {
            Utilidades.volverMenuGestionarTutorias(event);
        } catch (IOException ex) {
            manejarError("Error al volver al menú", ex, "Error", "No se pudo volver al menú de gestión.");
        } catch (Exception e) {
            manejarError("Error inesperado al volver al menú", e, "Error inesperado",
                    "Ocurrió un error inesperado al volver al menú.");
        }
    }

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
        Sesion.cerrarSesion();
        try {
            Utilidades.clicCerrarSesion(event);
        } catch (IOException ex) {
            manejarError("Error al cerrar sesión", ex, "Error", "No se pudo cerrar la sesión.");
        } catch (Exception e) {
            manejarError("Error inesperado al cerrar sesión", e, "Error inesperado",
                    "Ocurrió un error inesperado al cerrar la sesión.");
        }
    }

    private void manejarError(String mensajeLog, Exception excepcion, String titulo, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, titulo, mensajeUsuario);
    }
}
