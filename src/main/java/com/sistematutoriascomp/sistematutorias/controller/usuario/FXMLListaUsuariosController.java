/*
 * Autor:
 * Ultima modificación hecha por:
 * Versión:
 */
package com.sistematutoriascomp.sistematutorias.controller.usuario;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.RolDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.TutorDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Rol;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;
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

public class FXMLListaUsuariosController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLListaUsuariosController.class);
    private static final String RUTA_VISTAS = "/com/sistematutoriascomp/sistematutorias/views";

    @FXML private TableView<Tutor> tblUsuarios;
    @FXML private TableColumn<Tutor, String> tcNumeroPersonal;
    @FXML private TableColumn<Tutor, String> tcNombre;
    @FXML private TableColumn<Tutor, String> tcApellidoPaterno;
    @FXML private TableColumn<Tutor, String> tcApellidoMaterno;
    @FXML private TableColumn<Tutor, Integer> tcRol;
    @FXML private TableColumn<Tutor, Boolean> tcEstado;

    private TutorDAO tutorDAO = new TutorDAO();
    private RolDAO rolDAO = new RolDAO();
    private Map<Integer, String> nombresRoles = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarRoles();
        configurarColumnas();
        cargarUsuarios();
    }

    private void cargarRoles() {
        try {
            List<Rol> roles = rolDAO.obtenerTodosRoles();
            for (Rol rol : roles) {
                nombresRoles.put(rol.getIdRol(), rol.getNombreRol());
            }
        } catch (Exception e) {
            LOGGER.warn("No se pudieron cargar los nombres de roles", e);
        }
    }

    private void configurarColumnas() {
        tcNumeroPersonal.setCellValueFactory(new PropertyValueFactory<>("numeroDePersonal"));
        tcNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tcApellidoPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        tcApellidoMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        tcRol.setCellValueFactory(new PropertyValueFactory<>("idRol"));
        tcRol.setCellFactory(col -> new TableCell<Tutor, Integer>() {
            @Override
            protected void updateItem(Integer idRol, boolean empty) {
                super.updateItem(idRol, empty);
                if (empty || idRol == null) {
                    setText(null);
                } else {
                    setText(nombresRoles.getOrDefault(idRol, "Desconocido"));
                }
            }
        });
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("esActivo"));
        tcEstado.setCellFactory(col -> new TableCell<Tutor, Boolean>() {
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

    private void cargarUsuarios() {
        try {
            List<Tutor> usuarios = tutorDAO.getAllTutors();
            tblUsuarios.setItems(FXCollections.observableArrayList(usuarios));
        } catch (SQLException ex) {
            manejarError("Error al cargar usuarios", ex, "Error de base de datos",
                    "No se pudo cargar la lista de usuarios.");
        } catch (Exception e) {
            manejarError("Error inesperado al cargar usuarios", e, "Error inesperado",
                    "Ocurrió un error inesperado al cargar los usuarios.");
        }
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        Tutor seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple("Sin selección",
                    "Seleccione un usuario de la lista para editarlo.",
                    Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(RUTA_VISTAS + "/usuario/FXMLEditarUsuario.fxml"));
            Parent root = loader.load();
            FXMLEditarUsuarioController controller = loader.getController();
            controller.initData(seleccionado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Usuario");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarUsuarios();
        } catch (IOException ex) {
            manejarError("Error al abrir ventana de edición de usuario", ex, "Error",
                    "No se pudo abrir la ventana de edición.");
        } catch (Exception e) {
            manejarError("Error inesperado al abrir ventana de edición", e, "Error inesperado",
                    "Ocurrió un error inesperado al abrir la ventana de edición.");
        }
    }

    @FXML
    private void clicDarBaja(ActionEvent event) {
        Tutor seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple("Sin selección",
                    "Seleccione un usuario de la lista para darlo de baja.",
                    Alert.AlertType.WARNING);
            return;
        }
        if (!seleccionado.isEsActivo()) {
            Utilidades.mostrarAlertaSimple("Ya inactivo",
                    "El usuario seleccionado ya se encuentra inactivo.",
                    Alert.AlertType.INFORMATION);
            return;
        }
        boolean confirmar = Utilidades.mostrarAlertaConfirmacion(
                "Confirmar baja",
                "¿Desea dar de baja al usuario " + seleccionado.getNombre()
                + " " + seleccionado.getApellidoPaterno() + "?\nEsta acción lo marcará como inactivo.");
        if (!confirmar) {
            return;
        }
        try {
            boolean resultado = tutorDAO.darBajaUsuario(seleccionado.getNumeroDePersonal());
            if (resultado) {
                LOGGER.info("Usuario dado de baja: {}", seleccionado.getNumeroDePersonal());
                Utilidades.mostrarAlertaSimple("Baja exitosa",
                        "El usuario ha sido dado de baja correctamente.",
                        Alert.AlertType.INFORMATION);
                cargarUsuarios();
            } else {
                Utilidades.mostrarAlertaSimple("Error",
                        "No se pudo dar de baja al usuario.",
                        Alert.AlertType.ERROR);
            }
        } catch (SQLException ex) {
            manejarError("Error al dar de baja usuario: " + seleccionado.getNumeroDePersonal(), ex,
                    "Error de base de datos", "No se pudo dar de baja al usuario.");
        } catch (Exception e) {
            manejarError("Error inesperado al dar de baja usuario", e, "Error inesperado",
                    "Ocurrió un error inesperado al dar de baja al usuario.");
        }
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        try {
            Utilidades.goToWindow("/FXMLMenuGestionarUsuarios.fxml", event, "Gestión de Usuarios");
        } catch (IOException ex) {
            manejarError("Error al volver al menú de usuarios", ex, "Error",
                    "No se pudo volver al menú de usuarios.");
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
