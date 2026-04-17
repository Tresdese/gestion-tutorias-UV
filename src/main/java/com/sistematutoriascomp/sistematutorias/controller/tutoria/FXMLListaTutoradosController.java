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

public class FXMLListaTutoradosController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLListaTutoradosController.class);
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
    private TableColumn<Tutorado, Integer> tcSemestre;
    @FXML
    private TableColumn<Tutorado, Boolean> tcEstado;

    private TutoradoDAO tutoradoDAO = new TutoradoDAO();

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
        tcSemestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("activo"));
        tcEstado.setCellFactory(col -> new TableCell<Tutorado, Boolean>() {
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
            tblTutorados.setItems(FXCollections.observableArrayList(tutorados));
        } catch (SQLException ex) {
            manejarError("Error al cargar tutorados", ex, "Error de base de datos",
                    "No se pudo cargar la lista de tutorados.");
        } catch (Exception e) {
            manejarError("Error inesperado al cargar tutorados", e, "Error inesperado",
                    "Ocurrió un error inesperado al cargar los tutorados.");
        }
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        Tutorado seleccionado = tblTutorados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple("Sin selección",
                    "Seleccione un tutorado de la lista para editarlo.",
                    Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(RUTA_VISTAS + "/tutoria/FXMLEditarTutorado.fxml"));
            Parent root = loader.load();
            FXMLEditarTutoradoController controller = loader.getController();
            controller.initData(seleccionado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Tutorado");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarTutorados();
        } catch (IOException ex) {
            manejarError("Error al abrir ventana de edición", ex, "Error",
                    "No se pudo abrir la ventana de edición.");
        } catch (Exception e) {
            manejarError("Error inesperado al abrir ventana de edición", e, "Error inesperado",
                    "Ocurrió un error inesperado al abrir la ventana de edición.");
        }
    }

    @FXML
    private void clicDarBaja(ActionEvent event) {
        Tutorado seleccionado = tblTutorados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple("Sin selección",
                    "Seleccione un tutorado de la lista para darlo de baja.",
                    Alert.AlertType.WARNING);
            return;
        }
        if (!seleccionado.isActivo()) {
            Utilidades.mostrarAlertaSimple("Ya inactivo",
                    "El tutorado seleccionado ya se encuentra inactivo.",
                    Alert.AlertType.INFORMATION);
            return;
        }
        boolean confirmar = Utilidades.mostrarAlertaConfirmacion(
                "Confirmar baja",
                "¿Desea dar de baja al tutorado " + seleccionado.getNombre()
                + " " + seleccionado.getApellidoPaterno() + "?\nEsta acción lo marcará como inactivo.");
        if (!confirmar) {
            return;
        }
        try {
            boolean resultado = tutoradoDAO.darBajaTutorado(seleccionado.getMatricula());
            if (resultado) {
                LOGGER.info("Tutorado dado de baja: {}", seleccionado.getMatricula());
                Utilidades.mostrarAlertaSimple("Baja exitosa",
                        "El tutorado ha sido dado de baja correctamente.",
                        Alert.AlertType.INFORMATION);
                cargarTutorados();
            } else {
                Utilidades.mostrarAlertaSimple("Error",
                        "No se pudo dar de baja al tutorado.",
                        Alert.AlertType.ERROR);
            }
        } catch (SQLException ex) {
            manejarError("Error al dar de baja tutorado: " + seleccionado.getMatricula(), ex,
                    "Error de base de datos", "No se pudo dar de baja al tutorado.");
        } catch (Exception e) {
            manejarError("Error inesperado al dar de baja tutorado", e, "Error inesperado",
                    "Ocurrió un error inesperado al dar de baja al tutorado.");
        }
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        try {
            Utilidades.volverMenuGestionarTutorias(event);
        } catch (IOException ex) {
            manejarError("Error al volver al menú de gestión", ex, "Error",
                    "No se pudo volver al menú de gestión.");
        } catch (Exception e) {
            manejarError("Error inesperado al volver", e, "Error inesperado",
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
