package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.TutorDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;
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

public class FXMLConsultarListaTutoresController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLConsultarListaTutoresController.class);
    private static final String RUTA_VISTAS = "/com/sistematutoriascomp/sistematutorias/views";

    @FXML
    private TableView<Tutor> tblTutores;
    @FXML
    private TableColumn<Tutor, String> tcNumeroPersonal;
    @FXML
    private TableColumn<Tutor, String> tcNombre;
    @FXML
    private TableColumn<Tutor, String> tcApellidoPaterno;
    @FXML
    private TableColumn<Tutor, String> tcApellidoMaterno;
    @FXML
    private TableColumn<Tutor, Boolean> tcEstatus;

    private final TutorDAO tutorDAO = new TutorDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarTutores();
    }

    private void configurarColumnas() {
        tcNumeroPersonal.setCellValueFactory(new PropertyValueFactory<>("numeroDePersonal"));
        tcNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tcApellidoPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        tcApellidoMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        tcEstatus.setCellValueFactory(new PropertyValueFactory<>("esActivo"));
        tcEstatus.setCellFactory(col -> new TableCell<Tutor, Boolean>() {
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

    private void cargarTutores() {
        try {
            List<Tutor> tutores = tutorDAO.getAllTutors();
            if (tutores.isEmpty()) {
                Platform.runLater(() -> {
                    Utilidades.mostrarAlertaSimple("Sin tutores",
                            "Actualmente no hay Tutores registrados en el sistema.",
                            Alert.AlertType.INFORMATION);
                    try {
                        Stage stage = (Stage) tblTutores.getScene().getWindow();
                        Parent vista = Utilidades.loadFXML("/FXMLMenuGestionarUsuarios.fxml");
                        stage.setScene(new Scene(vista));
                        stage.setTitle("Gestión de Usuarios del Sistema");
                    } catch (IOException | NullPointerException ex) {
                        LOGGER.error("Error al volver al menú tras FA1.1", ex);
                    }
                });
                return;
            }
            tblTutores.setItems(FXCollections.observableArrayList(tutores));
        } catch (SQLException ex) {
            manejarError("Error de conexión al cargar tutores", ex, "Error de conexión",
                    "Error de conexión con base de datos, inténtalo más tarde");
        } catch (Exception e) {
            manejarError("Error inesperado al cargar tutores", e, "Error inesperado",
                    "Ocurrió un error inesperado al cargar los tutores.");
        }
    }

    @FXML
    private void clicVerDetalles(ActionEvent event) {
        Tutor seleccionado = tblTutores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple("Sin selección",
                    "Seleccione un tutor de la lista para ver sus detalles.",
                    Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(RUTA_VISTAS + "/tutoria/FXMLDetallesTutor.fxml"));
            Parent root = loader.load();
            FXMLDetallesTutorController controller = loader.getController();
            controller.inicializarValores(seleccionado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Detalles del Tutor");
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
    private void clicAsignarTutorado(ActionEvent event) {
        try {
            Utilidades.goToWindow("/tutoria/FXMLAsignarTutorado.fxml", event, "Asignar Tutorado");
        } catch (IOException ex) {
            manejarError("Error al navegar a Asignar Tutorado", ex, "Error",
                    "No se pudo abrir la pantalla de asignación.");
        } catch (Exception e) {
            manejarError("Error inesperado al navegar a Asignar Tutorado", e, "Error inesperado",
                    "Ocurrió un error inesperado al abrir la pantalla de asignación.");
        }
    }

    @FXML
    private void clicCerrar(ActionEvent event) {
        try {
            Utilidades.volverMenuGestionarUsuarios(event);
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
